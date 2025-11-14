
package Triangle.CodeGenerator;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import Triangle.AbstractSyntaxTrees.*;
import Triangle.AbstractSyntaxTrees.Visitor;

public class LLVMEncoder implements Visitor {

  private StringBuilder typesSection = new StringBuilder();
  private StringBuilder globals = new StringBuilder();
  private StringBuilder body = new StringBuilder(); // current function body
  private StringBuilder prologue = new StringBuilder(); // current function allocas
  private SymbolTable symtab = new SymbolTable();
  private int tmp = 0;
  private boolean needPutInt = false;
  private boolean needGetInt = false;
  private boolean needPutEol = false;
  private java.util.Set<String> emittedFunctions = new java.util.HashSet<>();

  public LLVMEncoder() {}

  private String nextTmp() { return "%t" + (tmp++); }

  public String getLLVM() {
    StringBuilder out = new StringBuilder();
    if (typesSection.length() > 0) out.append(typesSection.toString()).append('\n');
    if (globals.length() > 0) out.append(globals.toString()).append('\n');
    if (needPutInt) out.append("declare void @putint(i32)\n\n");
    if (needGetInt) out.append("declare i32 @getint()\n\n");
    if (needPutEol) out.append("declare void @puteol()\n\n");

    out.append("define i32 @main() {\n");
    out.append(prologue.toString());
    out.append(body.toString());
    out.append("  ret i32 0\n}\n");
    return out.toString();
  }

  // --- Helpers ---
  private String stripTypePrefix(String s) {
    if (s == null) return null;
    if (s.startsWith("i32* ")) return s.substring(5);
    if (s.startsWith("i32 ")) return s.substring(4);
    return s;
  }

  // --- Commands ---
  @Override
  public Object visitAssignCommand(AssignCommand ast, Object o) {
    Object rhs = ast.E.visit(this, o);
    Object addr = ast.V.visit(this, o);
    if (rhs instanceof String && addr instanceof String) {
      String r = (String) rhs;
      String a = stripTypePrefix((String) addr);
      body.append("  store i32 " + r + ", i32* " + a + "\n");
    }
    return null;
  }

  @Override
  public Object visitCallCommand(CallCommand ast, Object o) {
    String name = ast.I.spelling;
    if ("putint".equals(name)) {
      needPutInt = true;
      Object v = (ast.APS != null) ? ast.APS.visit(this, o) : null;
      String arg = (v instanceof String) ? stripTypePrefix((String) v) : "0";
      body.append("  call void @putint(i32 " + arg + ")\n");
    } else if ("puteol".equals(name)) {
      needPutEol = true;
      body.append("  call void @puteol()\n");
    } else if ("getint".equals(name)) {
      needGetInt = true;
      String t = nextTmp();
      body.append("  " + t + " = call i32 @getint()\n");
      return t;
    } else {
      // user function/proc call (no return expected for command form)
      String args = "";
      if (ast.APS != null) args = ast.APS.visit(this, o).toString();
      body.append("  call void @" + name + "(" + args + ")\n");
    }
    return null;
  }

  @Override public Object visitEmptyCommand(EmptyCommand ast, Object o) { return null; }

  @Override
  public Object visitIfCommand(IfCommand ast, Object o) {
    String cond = (String) ast.E.visit(this, o);
    String tcond = nextTmp();
    body.append("  " + tcond + " = icmp ne i32 " + cond + ", 0\n");
    String lthen = "L" + (tmp++);
    String lelse = "L" + (tmp++);
    String lend = "L" + (tmp++);
    body.append("  br i1 " + tcond + ", label %" + lthen + ", label %" + lelse + "\n");
    body.append(lthen + ":\n");
    ast.C1.visit(this, o);
    body.append("  br label %" + lend + "\n");
    body.append(lelse + ":\n");
    ast.C2.visit(this, o);
    body.append(lend + ":\n");
    return null;
  }

  @Override
  public Object visitLetCommand(LetCommand ast, Object o) {
    symtab.enterScope();
    ast.D.visit(this, o);
    ast.C.visit(this, o);
    symtab.exitScope();
    return null;
  }

  @Override public Object visitSequentialCommand(SequentialCommand ast, Object o) { ast.C1.visit(this,o); ast.C2.visit(this,o); return null; }

  @Override
  public Object visitWhileCommand(WhileCommand ast, Object o) {
    String lcond = "L" + (tmp++);
    String lbody = "L" + (tmp++);
    String lend = "L" + (tmp++);
    body.append("  br label %" + lcond + "\n");
    body.append(lcond + ":\n");
    String c = (String) ast.E.visit(this, o);
    String t = nextTmp();
    body.append("  " + t + " = icmp ne i32 " + c + ", 0\n");
    body.append("  br i1 " + t + ", label %" + lbody + ", label %" + lend + "\n");
    body.append(lbody + ":\n");
    ast.C.visit(this, o);
    body.append("  br label %" + lcond + "\n");
    body.append(lend + ":\n");
    return null;
  }

  // --- Expressions ---
  @Override public Object visitIntegerExpression(IntegerExpression ast, Object o) { return ast.IL.spelling; }

  @Override
  public Object visitBinaryExpression(BinaryExpression ast, Object o) {
    String l = (String) ast.E1.visit(this, o);
    String r = (String) ast.E2.visit(this, o);
    String t = nextTmp();
    String op = ast.O.spelling;
    String codeop = "add";
    if ("-".equals(op)) codeop = "sub";
    else if ("*".equals(op)) codeop = "mul";
    else if ("/".equals(op)) codeop = "sdiv";
    body.append("  " + t + " = " + codeop + " i32 " + l + ", " + r + "\n");
    return t;
  }

  @Override
  public Object visitVnameExpression(VnameExpression ast, Object o) {
    Object addr = ast.V.visit(this, o);
    if (addr instanceof String) {
      String a = stripTypePrefix((String) addr);
      String t = nextTmp();
      body.append("  " + t + " = load i32, i32* " + a + "\n");
      return t;
    }
    return "0";
  }

  @Override public Object visitCallExpression(CallExpression ast, Object o) {
    String name = ast.I.spelling;
    if ("getint".equals(name)) {
      needGetInt = true;
      String t = nextTmp();
      body.append("  " + t + " = call i32 @getint()\n");
      return t;
    }
    // general function call returning i32
    String args = "";
    if (ast.APS != null) args = ast.APS.visit(this, o).toString();
    String t = nextTmp();
    body.append("  " + t + " = call i32 @" + name + "(" + args + ")\n");
    return t;
  }

  @Override public Object visitArrayExpression(ArrayExpression ast, Object o){ return null; }
  @Override public Object visitCharacterExpression(CharacterExpression ast, Object o){ return null; }
  @Override public Object visitEmptyExpression(EmptyExpression ast, Object o){ return "0"; }
  @Override public Object visitIfExpression(IfExpression ast, Object o){ return null; }
  @Override public Object visitLetExpression(LetExpression ast, Object o){ return ast.E.visit(this, o); }
  @Override public Object visitRecordExpression(RecordExpression ast, Object o){ return null; }
  @Override public Object visitUnaryExpression(UnaryExpression ast, Object o){ return null; }

  // --- Declarations ---
  @Override public Object visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, Object o){ return null; }
  @Override public Object visitConstDeclaration(ConstDeclaration ast, Object o){ return null; }

  @Override
  public Object visitFuncDeclaration(FuncDeclaration ast, Object o) {
    String name = ast.I.spelling;
    if (emittedFunctions.contains(name)) return null; 
    emittedFunctions.add(name);
    System.out.println("[LLVMEncoder] emitting function: " + name);
    // Enter function scope
    symtab.enterScope();
    // collect parameters (this will declare symbols)
    String params = "";
    if (ast.FPS != null) params = (String) ast.FPS.visit(this, o);

    // Save outer builders
    StringBuilder prevBody = body;
    StringBuilder prevPrologue = prologue;
    body = new StringBuilder();
    prologue = new StringBuilder();

    // Visit expression body (will emit into body/prologue and declare locals)
    Object retval = null;
    if (ast.E != null) retval = ast.E.visit(this, o);

    // Build function text
    StringBuilder fn = new StringBuilder();
    fn.append("define i32 @" + name + "(" + params + ") {\n");

    // param allocas and stores for non-ref params
    Map<String, SymbolTable.Symbol> cur = symtab.getCurrentScopeSymbols();
    StringBuilder paramAllocs = new StringBuilder();
    StringBuilder paramStores = new StringBuilder();
    for (SymbolTable.Symbol s : cur.values()) {
      if (s.isParam) {
        if (s.paramIsRef) {
          
          s.llvmName = s.paramName; 
        } else {
          paramAllocs.append("  " + s.llvmName + " = alloca i32\n");
          paramStores.append("  store i32 " + s.paramName + ", i32* " + s.llvmName + "\n");
        }
      }
    }

    fn.append(paramAllocs.toString());
    fn.append(prologue.toString());
    fn.append(paramStores.toString());
    fn.append(body.toString());
    String r = (retval == null) ? "0" : retval.toString();
    fn.append("  ret i32 " + r + "\n}\n\n");

    globals.append(fn.toString());

    // Restore
    body = prevBody;
    prologue = prevPrologue;
    symtab.exitScope();
    return null;
  }

  @Override
  public Object visitProcDeclaration(ProcDeclaration ast, Object o) {
    String name = ast.I.spelling;
    if (emittedFunctions.contains(name)) 
    emittedFunctions.add(name);
    System.out.println("[LLVMEncoder] emitting procedure: " + name);
    symtab.enterScope();
    String params = "";
    if (ast.FPS != null) params = (String) ast.FPS.visit(this, o);

    StringBuilder prevBody = body;
    StringBuilder prevPrologue = prologue;
    body = new StringBuilder();
    prologue = new StringBuilder();

    if (ast.C != null) ast.C.visit(this, o);

    // Build proc
    StringBuilder fn = new StringBuilder();
    fn.append("define void @" + name + "(" + params + ") {\n");

    Map<String, SymbolTable.Symbol> cur = symtab.getCurrentScopeSymbols();
    StringBuilder paramAllocs = new StringBuilder();
    StringBuilder paramStores = new StringBuilder();
    for (SymbolTable.Symbol s : cur.values()) {
      if (s.isParam) {
        if (s.paramIsRef) {
          s.llvmName = s.paramName;
        } else {
          paramAllocs.append("  " + s.llvmName + " = alloca i32\n");
          paramStores.append("  store i32 " + s.paramName + ", i32* " + s.llvmName + "\n");
        }
      }
    }

    fn.append(paramAllocs.toString());
    fn.append(prologue.toString());
    fn.append(paramStores.toString());
    fn.append(body.toString());
    fn.append("  ret void\n}\n\n");

    globals.append(fn.toString());

    body = prevBody;
    prologue = prevPrologue;
    symtab.exitScope();
    return null;
  }

  @Override public Object visitSequentialDeclaration(SequentialDeclaration ast, Object o){ ast.D1.visit(this,o); ast.D2.visit(this,o); return null; }
  @Override public Object visitTypeDeclaration(TypeDeclaration ast, Object o){ return null; }
  @Override public Object visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, Object o){ return null; }

  @Override
  public Object visitVarDeclaration(VarDeclaration ast, Object o) {
    String name = ast.I.spelling;
    if (symtab.getCurrentLevel() == 0) {
      // global
      SymbolTable.Symbol s = symtab.lookup(name);
      if (s == null) s = symtab.declareGlobal(name);
      if (ast.T instanceof ArrayTypeDenoter) {
        ArrayTypeDenoter at = (ArrayTypeDenoter) ast.T;
        int len = Integer.parseInt(at.IL.spelling);
        String arrType = "[" + len + " x i32]";
        s.typeLLVM = arrType; s.isArray = true; s.arrayLength = len;
        globals.append(s.llvmName + " = dso_local global " + arrType + " zeroinitializer, align 4\n");
      } else if (ast.T instanceof RecordTypeDenoter) {
        RecordTypeDenoter rt = (RecordTypeDenoter) ast.T;
        List<String> fieldNames = new ArrayList<>();
        List<String> fieldTypes = new ArrayList<>();
        FieldTypeDenoter ft = rt.FT;
        while (ft != null) {
          if (ft instanceof SingleFieldTypeDenoter) {
            SingleFieldTypeDenoter sft = (SingleFieldTypeDenoter) ft;
            fieldNames.add(sft.I.spelling); fieldTypes.add("i32"); break;
          } else if (ft instanceof MultipleFieldTypeDenoter) {
            MultipleFieldTypeDenoter mft = (MultipleFieldTypeDenoter) ft;
            fieldNames.add(mft.I.spelling); fieldTypes.add("i32"); ft = mft.FT; continue;
          } else break;
        }
        String structName = "%struct" + (tmp++);
        StringBuilder fieldsSb = new StringBuilder();
        for (int i = 0; i < fieldTypes.size(); i++) { if (i>0) fieldsSb.append(", "); fieldsSb.append(fieldTypes.get(i)); }
        typesSection.append(structName + " = type { " + fieldsSb.toString() + " }\n");
        s.isRecord = true; s.structName = structName; s.fieldIndex = new java.util.HashMap<>();
        for (int i = 0; i < fieldNames.size(); i++) s.fieldIndex.put(fieldNames.get(i), i);
        s.typeLLVM = structName;
        globals.append(s.llvmName + " = dso_local global " + structName + " zeroinitializer, align 4\n");
      } else {
        s.typeLLVM = "i32";
        globals.append(s.llvmName + " = dso_local global i32 0, align 4\n");
      }
    } else {
      SymbolTable.Symbol s = symtab.lookup(name);
      if (s == null) s = symtab.declareLocal(name);
      if (ast.T instanceof ArrayTypeDenoter) {
        ArrayTypeDenoter at = (ArrayTypeDenoter) ast.T;
        int len = Integer.parseInt(at.IL.spelling);
        String arrType = "[" + len + " x i32]";
        s.typeLLVM = arrType; s.isArray = true; s.arrayLength = len;
        prologue.append("  " + s.llvmName + " = alloca " + arrType + "\n");
      } else if (ast.T instanceof RecordTypeDenoter) {
        RecordTypeDenoter rt = (RecordTypeDenoter) ast.T;
        List<String> fieldNames = new ArrayList<>(); List<String> fieldTypes = new ArrayList<>();
        FieldTypeDenoter ft = rt.FT;
        while (ft != null) {
          if (ft instanceof SingleFieldTypeDenoter) { SingleFieldTypeDenoter sft = (SingleFieldTypeDenoter) ft; fieldNames.add(sft.I.spelling); fieldTypes.add("i32"); break; }
          else if (ft instanceof MultipleFieldTypeDenoter) { MultipleFieldTypeDenoter mft = (MultipleFieldTypeDenoter) ft; fieldNames.add(mft.I.spelling); fieldTypes.add("i32"); ft = mft.FT; continue; }
          else break;
        }
        String structName = "%struct" + (tmp++);
        StringBuilder fieldsSb = new StringBuilder();
        for (int i = 0; i < fieldTypes.size(); i++) { if (i>0) fieldsSb.append(", "); fieldsSb.append(fieldTypes.get(i)); }
        typesSection.append(structName + " = type { " + fieldsSb.toString() + " }\n");
        s.isRecord = true; s.structName = structName; s.fieldIndex = new java.util.HashMap<>();
        for (int i = 0; i < fieldNames.size(); i++) s.fieldIndex.put(fieldNames.get(i), i);
        s.typeLLVM = structName;
        prologue.append("  " + s.llvmName + " = alloca " + structName + "\n");
      } else {
        s.typeLLVM = "i32";
        prologue.append("  " + s.llvmName + " = alloca i32\n");
      }
    }
    return null;
  }

  @Override public Object visitMultipleArrayAggregate(MultipleArrayAggregate ast, Object o){ return null; }
  @Override public Object visitSingleArrayAggregate(SingleArrayAggregate ast, Object o){ return null; }
  @Override public Object visitMultipleRecordAggregate(MultipleRecordAggregate ast, Object o){ return null; }
  @Override public Object visitSingleRecordAggregate(SingleRecordAggregate ast, Object o){ return null; }

  @Override
  public Object visitConstFormalParameter(ConstFormalParameter ast, Object o) {
    String name = ast.I.spelling;
    SymbolTable.Symbol s = symtab.declareParam(name);
    return "i32 " + s.paramName;
  }

  @Override public Object visitFuncFormalParameter(FuncFormalParameter ast, Object o){ return null; }
  @Override public Object visitProcFormalParameter(ProcFormalParameter ast, Object o){ return null; }

  @Override
  public Object visitVarFormalParameter(VarFormalParameter ast, Object o) {
    String name = ast.I.spelling;
    SymbolTable.Symbol s = symtab.declareParam(name);
    s.paramIsRef = true;
    if (ast.T instanceof ArrayTypeDenoter) {
      ArrayTypeDenoter at = (ArrayTypeDenoter) ast.T;
      int len = Integer.parseInt(at.IL.spelling);
      String arrType = "[" + len + " x i32]";
      s.typeLLVM = arrType; s.isArray = true; s.arrayLength = len;
      return arrType + "* " + s.paramName;
    } else if (ast.T instanceof RecordTypeDenoter) {
      RecordTypeDenoter rt = (RecordTypeDenoter) ast.T;
      List<String> fieldTypes = new ArrayList<>(); FieldTypeDenoter ft = rt.FT;
      while (ft != null) {
        if (ft instanceof SingleFieldTypeDenoter) { fieldTypes.add("i32"); break; }
        else if (ft instanceof MultipleFieldTypeDenoter) { fieldTypes.add("i32"); ft = ((MultipleFieldTypeDenoter)ft).FT; continue; }
        else break;
      }
      String structName = "%struct" + (tmp++);
      StringBuilder fieldsSb = new StringBuilder();
      for (int i = 0; i < fieldTypes.size(); i++) { if (i>0) fieldsSb.append(", "); fieldsSb.append(fieldTypes.get(i)); }
      typesSection.append(structName + " = type { " + fieldsSb.toString() + " }\n");
      s.isRecord = true; s.structName = structName; s.fieldIndex = new java.util.HashMap<>();
      for (int i = 0; i < fieldTypes.size(); i++) s.fieldIndex.put("f" + i, i);
      s.typeLLVM = structName;
      return structName + "* " + s.paramName;
    }
    return "i32* " + s.paramName;
  }

  @Override public Object visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, Object o){ return ""; }

  @Override
  public Object visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, Object o) {
    Object first = ast.FP.visit(this, o);
    Object rest = ast.FPS.visit(this, o);
    String sfirst = (first == null) ? "" : first.toString();
    String srest = (rest == null) ? "" : rest.toString();
    if (srest.isEmpty()) return sfirst;
    if (sfirst.isEmpty()) return srest;
    return sfirst + ", " + srest;
  }

  @Override
  public Object visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, Object o) {
    Object first = ast.FP.visit(this, o);
    return (first == null) ? "" : first.toString();
  }

  @Override
  public Object visitConstActualParameter(ConstActualParameter ast, Object o) {
    Object v = ast.E.visit(this, o);
    if (v == null) return "";
    return "i32 " + v.toString();
  }

  @Override public Object visitFuncActualParameter(FuncActualParameter ast, Object o){ return null; }
  @Override public Object visitProcActualParameter(ProcActualParameter ast, Object o){ return null; }

  @Override
  public Object visitVarActualParameter(VarActualParameter ast, Object o) {
    if (ast.V instanceof SimpleVname) {
      SimpleVname sv = (SimpleVname) ast.V;
      String var = sv.I.spelling;
      SymbolTable.Symbol sym = symtab.lookup(var);
      if (sym == null) { sym = symtab.declareLocal(var); prologue.append("  " + sym.llvmName + " = alloca i32\n"); }
      return "i32* " + sym.llvmName;
    } else {
      Object addr = ast.V.visit(this, o);
      if (addr == null) return "";
      return "i32* " + addr.toString();
    }
  }

  @Override public Object visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, Object o){ return ""; }

  @Override
  public Object visitSingleActualParameterSequence(SingleActualParameterSequence ast, Object o) {
    Object first = ast.AP.visit(this, o);
    return (first == null) ? "" : first.toString();
  }

  @Override
  public Object visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, Object o) {
    Object first = ast.AP.visit(this, o);
    Object rest = ast.APS.visit(this, o);
    String sfirst = (first == null) ? "" : first.toString();
    String srest = (rest == null) ? "" : rest.toString();
    if (srest.isEmpty()) return sfirst;
    if (sfirst.isEmpty()) return srest;
    return sfirst + ", " + srest;
  }

  // --- Types ---
  @Override public Object visitAnyTypeDenoter(AnyTypeDenoter ast, Object o){ return null; }
  @Override public Object visitArrayTypeDenoter(ArrayTypeDenoter ast, Object o){ return null; }
  @Override public Object visitBoolTypeDenoter(BoolTypeDenoter ast, Object o){ return null; }
  @Override public Object visitCharTypeDenoter(CharTypeDenoter ast, Object o){ return null; }
  @Override public Object visitErrorTypeDenoter(ErrorTypeDenoter ast, Object o){ return null; }
  @Override public Object visitSimpleTypeDenoter(SimpleTypeDenoter ast, Object o){ return null; }
  @Override public Object visitIntTypeDenoter(IntTypeDenoter ast, Object o){ return null; }
  @Override public Object visitRecordTypeDenoter(RecordTypeDenoter ast, Object o){ return null; }
  @Override public Object visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, Object o){ return null; }
  @Override public Object visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, Object o){ return null; }

  @Override public Object visitCharacterLiteral(CharacterLiteral ast, Object o){ return null; }
  @Override public Object visitIdentifier(Identifier ast, Object o){ return ast.spelling; }
  @Override public Object visitIntegerLiteral(IntegerLiteral ast, Object o){ return ast.spelling; }
  @Override public Object visitOperator(Operator ast, Object o){ return null; }

  // --- Vnames ---
  @Override
  public Object visitDotVname(DotVname ast, Object o) {
    if (!(ast.V instanceof SimpleVname)) return null;
    String base = ((SimpleVname)ast.V).I.spelling;
    SymbolTable.Symbol sym = symtab.lookup(base);
    if (sym == null || !sym.isRecord) return null;
    String field = ast.I.spelling;
    Integer idx = sym.fieldIndex.get(field);
    if (idx == null) return null;
    String t = nextTmp();
    body.append("  " + t + " = getelementptr inbounds " + sym.structName + ", " + sym.structName + "* " + sym.llvmName + ", i32 0, i32 " + idx + "\n");
    return t;
  }

  @Override
  public Object visitSimpleVname(SimpleVname ast, Object o) {
    String var = ast.I.spelling;
    SymbolTable.Symbol sym = symtab.lookup(var);
    if (sym == null) { sym = symtab.declareLocal(var); prologue.append("  " + sym.llvmName + " = alloca i32\n"); }
    return sym.llvmName;
  }

  @Override
  public Object visitSubscriptVname(SubscriptVname ast, Object o) {
    if (!(ast.V instanceof SimpleVname)) return null;
    SimpleVname base = (SimpleVname) ast.V;
    String baseName = base.I.spelling;
    SymbolTable.Symbol sym = symtab.lookup(baseName);
    if (sym == null) { sym = symtab.declareLocal(baseName); prologue.append("  " + sym.llvmName + " = alloca i32\n"); }
    String idx = (String) ast.E.visit(this, o);
    String t = nextTmp();
    if (sym.isArray) {
      body.append("  " + t + " = getelementptr inbounds " + sym.typeLLVM + ", " + sym.typeLLVM + "* " + sym.llvmName + ", i32 0, i32 " + idx + "\n");
    } else {
      body.append("  " + t + " = getelementptr inbounds i32, i32* " + sym.llvmName + ", i32 " + idx + "\n");
    }
    return t;
  }

  @Override
  public Object visitProgram(Program ast, Object o) {
    if (ast.C != null) ast.C.visit(this, null);

    // Prepend prologue to main body
    String p = prologue.toString();
    prologue = new StringBuilder(); // clear
    body.insert(0, p);
    return getLLVM();
  }

}
