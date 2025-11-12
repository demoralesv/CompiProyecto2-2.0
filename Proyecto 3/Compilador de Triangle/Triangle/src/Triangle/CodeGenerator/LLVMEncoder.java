/*
 * LLVMEncoder.java
 * Minimal visitor that generates basic LLVM IR from a subset of the Triangle AST.
 * This is an MVP implementation: supports integer literals, variables, assignments,
 * binary arithmetic (+,-,*,/), if, while, sequential commands, and calls to
 * `putint`/`puteol`/`getint` when present. It emits a single `main` function.
 *
 * Version: MVP para entrega rápida.
 */

package Triangle.CodeGenerator;

// minimal imports; SymbolTable used for variable info
import java.util.Map;

import Triangle.AbstractSyntaxTrees.*;
import Triangle.AbstractSyntaxTrees.Visitor;

public class LLVMEncoder implements Visitor {

  private StringBuilder sb = new StringBuilder();
  private StringBuilder body = new StringBuilder();
  private StringBuilder globals = new StringBuilder();
  private SymbolTable symtab = new SymbolTable();
  private int tmp = 0;
  private boolean needPutInt = false;
  private boolean needPutEol = false;
  private boolean needGetInt = false;
  private int nestingLevel = 0; // 0 = outside, 1 = top-level declarations (treat as globals), >1 = locals

  public LLVMEncoder() {
  }

  private String nextTmp() { return "%t" + (tmp++); }

  public String getLLVM() {
    StringBuilder out = new StringBuilder();
    // emit globals first
    if (globals.length() > 0) {
      out.append(globals.toString());
      out.append("\n");
    }
    // external declarations
    if (needPutInt) out.append("declare void @putint(i32)\n\n");
    if (needGetInt) out.append("declare i32 @getint()\n\n");
    if (needPutEol) out.append("declare void @puteol()\n\n");

    out.append("define i32 @main() {\n");
    out.append(body.toString());
    out.append("  ret i32 0\n}\n");
    return out.toString();
  }

  // --- Commands ---
  @Override
  public Object visitAssignCommand(AssignCommand ast, Object o) {
    // ast.V is a Vname, ast.E an expression
    // we support SimpleVname -> Identifier
    String rhs = (String) ast.E.visit(this, o);
    // ask the vname visitor for the address (i32*). Many vname visitors return a pointer name.
    Object addrObj = ast.V.visit(this, o);
    if (addrObj instanceof String) {
      String addr = (String) addrObj;
      // if the addr looks like a parameter declaration "i32* %p_x" (unlikely here), strip type
      if (addr.startsWith("i32* ")) addr = addr.substring(5);
      body.append("  store i32 " + rhs + ", i32* " + addr + "\n");
    }
    return null;
  }

  @Override
  public Object visitCallCommand(CallCommand ast, Object o) {
    String name = ast.I.spelling;
    // very simple: if putint has one arg, evaluate it and call extern
    if ("putint".equals(name) || "putintln".equals(name) || "puti".equals(name)) {
      needPutInt = true;
      // assume single actual parameter and it's an expression
      if (ast.APS != null) {
        Object v = ast.APS.visit(this, o);
        if (v instanceof String) {
          String vs = (String) v;
          if (vs.startsWith("i32 ")) body.append("  call void @putint(" + vs + ")\n");
          else body.append("  call void @putint(i32 " + vs + ")\n");
        }
      }
    } else if ("puteol".equals(name) || "putnl".equals(name) || "puteol".equals(name)) {
      needPutEol = true;
      body.append("  call void @puteol()\n");
    } else if ("getint".equals(name) || "geti".equals(name)) {
      needGetInt = true;
      String t = nextTmp();
      body.append("  " + t + " = call i32 @getint()\n");
      return t;
    }
    return null;
  }

  @Override
  public Object visitEmptyCommand(EmptyCommand ast, Object o) { return null; }

  @Override
  public Object visitIfCommand(IfCommand ast, Object o) {
    // Evaluate condition
    String cond = (String) ast.E.visit(this, o);
    String tcond = nextTmp();
    body.append("  " + tcond + " = icmp ne i32 " + cond + ", 0\n");
    String lblThen = "L" + tmp++;
    String lblElse = "L" + tmp++;
    String lblEnd = "L" + tmp++;
    body.append("  br i1 " + tcond + ", label %" + lblThen + ", label %" + lblElse + "\n");
    body.append(lblThen + ":\n");
    ast.C1.visit(this, o);
    body.append("  br label %" + lblEnd + "\n");
    body.append(lblElse + ":\n");
    ast.C2.visit(this, o);
    body.append("  br label %" + lblEnd + "\n");
    body.append(lblEnd + ":\n");
    return null;
  }

  @Override
  public Object visitLetCommand(LetCommand ast, Object o) {
    // process declarations then command
    // open a new scope for declarations
    nestingLevel++;
    symtab.enterScope();
    ast.D.visit(this, o);
    ast.C.visit(this, o);
    symtab.exitScope();
    nestingLevel--;
    return null;
  }

  @Override
  public Object visitSequentialCommand(SequentialCommand ast, Object o) {
    ast.C1.visit(this, o);
    ast.C2.visit(this, o);
    return null;
  }

  @Override
  public Object visitWhileCommand(WhileCommand ast, Object o) {
    String lblCond = "L" + tmp++;
    String lblBody = "L" + tmp++;
    String lblEnd = "L" + tmp++;
    body.append("  br label %" + lblCond + "\n");
    body.append(lblCond + ":\n");
    String cond = (String) ast.E.visit(this, o);
    String tcond = nextTmp();
    body.append("  " + tcond + " = icmp ne i32 " + cond + ", 0\n");
    body.append("  br i1 " + tcond + ", label %" + lblBody + ", label %" + lblEnd + "\n");
    body.append(lblBody + ":\n");
    ast.C.visit(this, o);
    body.append("  br label %" + lblCond + "\n");
    body.append(lblEnd + ":\n");
    return null;
  }

  // --- Expressions ---
  @Override
  public Object visitIntegerExpression(IntegerExpression ast, Object o) {
    return ast.IL.spelling;
  }

  @Override
  public Object visitBinaryExpression(BinaryExpression ast, Object o) {
    String l = (String) ast.E1.visit(this, o);
    String r = (String) ast.E2.visit(this, o);
    String res = nextTmp();
    String op = "add";
    String opSymbol = ast.O.spelling;
    if ("+".equals(opSymbol)) op = "add";
    else if ("-".equals(opSymbol)) op = "sub";
    else if ("*".equals(opSymbol)) op = "mul";
    else if ("/".equals(opSymbol)) op = "sdiv";
    body.append("  " + res + " = " + op + " i32 " + l + ", " + r + "\n");
    return res;
  }

  @Override
  public Object visitVnameExpression(VnameExpression ast, Object o) {
    // Fetch variable value
    // delegate to vname visitor to get address, then load from it
    Object addrObj = ast.V.visit(this, o);
    if (addrObj instanceof String) {
      String addr = (String) addrObj;
      if (addr.startsWith("i32* ")) addr = addr.substring(5);
      String t = nextTmp();
      body.append("  " + t + " = load i32, i32* " + addr + "\n");
      return t;
    }
    return "0";
  }

  @Override
  public Object visitIdentifier(Identifier ast, Object o) {
    return ast.spelling;
  }

  // Declarations: only VarDeclaration minimally
  @Override
  public Object visitVarDeclaration(VarDeclaration ast, Object o) {
    // ast.I is Identifier
    String var = ast.I.spelling;
    if (nestingLevel == 1) {
      // treat as global
      SymbolTable.Symbol s = symtab.lookup(var);
      if (s == null) {
        s = symtab.declareGlobal(var);
        globals.append(s.llvmName + " = dso_local global i32 0, align 4\n");
      }
    } else {
      SymbolTable.Symbol s = symtab.lookup(var);
      if (s == null) {
        s = symtab.declareLocal(var);
        body.insert(0, "  " + s.llvmName + " = alloca i32\n");
      }
    }
    return null;
  }

  // Many visitor methods left as simple stubs to satisfy interface.
  @Override public Object visitArrayExpression(ArrayExpression ast, Object o){ return null; }
  @Override public Object visitCallExpression(CallExpression ast, Object o){
    String name = ast.I.spelling;
    String args = "";
    if (ast.APS != null) {
      Object a = ast.APS.visit(this, o);
      args = (a == null) ? "" : a.toString();
    }
    if ("getint".equals(name) || "geti".equals(name)) {
      needGetInt = true;
      String t = nextTmp();
      body.append("  " + t + " = call i32 @getint()\n");
      return t;
    }
    String t = nextTmp();
    if (args.isEmpty()) body.append("  " + t + " = call i32 @" + name + "()\n");
    else body.append("  " + t + " = call i32 @" + name + "(" + args + ")\n");
    return t;
  }
  @Override public Object visitCharacterExpression(CharacterExpression ast, Object o){ return null; }
  @Override public Object visitEmptyExpression(EmptyExpression ast, Object o){ return "0"; }
  @Override public Object visitIfExpression(IfExpression ast, Object o){ return null; }
  @Override public Object visitLetExpression(LetExpression ast, Object o){ return ast.E.visit(this, o); }
  @Override public Object visitRecordExpression(RecordExpression ast, Object o){ return null; }
  @Override public Object visitUnaryExpression(UnaryExpression ast, Object o){ return null; }

  @Override public Object visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, Object o){ return null; }
  @Override public Object visitConstDeclaration(ConstDeclaration ast, Object o){ return null; }
  @Override public Object visitFuncDeclaration(FuncDeclaration ast, Object o){
    String name = ast.I.spelling;
    // enter function scope for parameters and locals
    symtab.enterScope();
    nestingLevel++;
    // collect parameter signature (this will declare params in symtab)
    String params = "";
    if (ast.FPS != null) {
      Object p = ast.FPS.visit(this, o);
      params = (p == null) ? "" : p.toString();
    }

    // prepare a temporary body builder for this function
    StringBuilder prevBody = body;
    StringBuilder fbody = new StringBuilder();
    body = fbody;

    // visit expression body (will emit code into fbody; var-decls will insert allocas at front)
    Object retval = null;
    if (ast.E != null) retval = ast.E.visit(this, o);

    // after generating fbody, collect local allocas prefix vs rest of code
    String ftext = fbody.toString();
    String[] lines = ftext.split("\\n");
    StringBuilder localAllocas = new StringBuilder();
    StringBuilder rest = new StringBuilder();
    boolean inAllocas = true;
    for (String ln : lines) {
      if (inAllocas && ln.trim().endsWith("= alloca i32")) {
        localAllocas.append(ln).append("\n");
      } else {
        inAllocas = false;
        rest.append(ln).append("\n");
      }
    }

    // param allocas and stores
    StringBuilder paramAllocs = new StringBuilder();
    StringBuilder paramStores = new StringBuilder();
    Map<String, SymbolTable.Symbol> cur = symtab.getCurrentScopeSymbols();
    for (SymbolTable.Symbol s : cur.values()) {
      if (s.isParam) {
        if (s.paramIsRef) {
          // parameter already an i32* (pointer). Use param name directly as the address.
          s.llvmName = s.paramName; // bind symbol's llvmName to the incoming pointer
        } else {
          paramAllocs.append("  " + s.llvmName + " = alloca i32\n");
          paramStores.append("  store i32 " + s.paramName + ", i32* " + s.llvmName + "\n");
        }
      }
    }

    // assemble function text: header, allocas (params then locals), param stores, rest, return
    StringBuilder fn = new StringBuilder();
    fn.append("define i32 @" + name + "(" + params + ") {\n");
    fn.append(paramAllocs.toString());
    fn.append(localAllocas.toString());
    fn.append(paramStores.toString());
    fn.append(rest.toString());
    String r = (retval == null) ? "0" : retval.toString();
  fn.append("  ret i32 " + r + "\n}\n\n");

    // append function before main (globals block)
    globals.append(fn.toString());

    // restore outer body and scope
    body = prevBody;
    symtab.exitScope();
    nestingLevel--;
    return null;
  }

  @Override public Object visitProcDeclaration(ProcDeclaration ast, Object o){
    String name = ast.I.spelling;
    symtab.enterScope();
    nestingLevel++;
    String params = "";
    if (ast.FPS != null) {
      Object p = ast.FPS.visit(this, o);
      params = (p == null) ? "" : p.toString();
    }
    StringBuilder prevBody = body;
    StringBuilder fbody = new StringBuilder();
    body = fbody;
    if (ast.C != null) ast.C.visit(this, o);
    String ftext = fbody.toString();
    String[] lines = ftext.split("\\n");
    StringBuilder localAllocas = new StringBuilder();
    StringBuilder rest = new StringBuilder();
    boolean inAllocas = true;
    for (String ln : lines) {
      if (inAllocas && ln.trim().endsWith("= alloca i32")) {
        localAllocas.append(ln).append("\n");
      } else {
        inAllocas = false;
        rest.append(ln).append("\n");
      }
    }
    StringBuilder paramAllocs = new StringBuilder();
    StringBuilder paramStores = new StringBuilder();
    Map<String, SymbolTable.Symbol> cur = symtab.getCurrentScopeSymbols();
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
    StringBuilder fn = new StringBuilder();
    fn.append("define void @" + name + "(" + params + ") {\n");
    fn.append(paramAllocs.toString());
    fn.append(localAllocas.toString());
    fn.append(paramStores.toString());
    fn.append(rest.toString());
    fn.append("  ret void\n}\n\n");
    globals.append(fn.toString());
    body = prevBody;
    symtab.exitScope();
    nestingLevel--;
    return null;
  }
  @Override public Object visitSequentialDeclaration(SequentialDeclaration ast, Object o){ return null; }
  @Override public Object visitTypeDeclaration(TypeDeclaration ast, Object o){ return null; }
  @Override public Object visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, Object o){ return null; }

  @Override public Object visitMultipleArrayAggregate(MultipleArrayAggregate ast, Object o){ return null; }
  @Override public Object visitSingleArrayAggregate(SingleArrayAggregate ast, Object o){ return null; }
  @Override public Object visitMultipleRecordAggregate(MultipleRecordAggregate ast, Object o){ return null; }
  @Override public Object visitSingleRecordAggregate(SingleRecordAggregate ast, Object o){ return null; }

  @Override public Object visitConstFormalParameter(ConstFormalParameter ast, Object o){
    // declare parameter in current scope and return its signature fragment
    String name = ast.I.spelling;
    SymbolTable.Symbol s = symtab.declareParam(name);
    return "i32 " + s.paramName;
  }
  @Override public Object visitFuncFormalParameter(FuncFormalParameter ast, Object o){ return null; }
  @Override public Object visitProcFormalParameter(ProcFormalParameter ast, Object o){ return null; }
  @Override public Object visitVarFormalParameter(VarFormalParameter ast, Object o){
    // Var-formal: pass by reference. Parameter type is i32* and we mark symbol as reference
    String name = ast.I.spelling;
    SymbolTable.Symbol s = symtab.declareParam(name);
    s.paramIsRef = true;
    return "i32* " + s.paramName;
  }
  @Override public Object visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, Object o){ return ""; }
  @Override public Object visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, Object o){
    Object first = ast.FP.visit(this, o);
    Object rest = ast.FPS.visit(this, o);
    String sfirst = (first == null) ? "" : first.toString();
    String srest = (rest == null) ? "" : rest.toString();
    if (srest.isEmpty()) return sfirst;
    if (sfirst.isEmpty()) return srest;
    return sfirst + ", " + srest;
  }
  @Override public Object visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, Object o){
    Object first = ast.FP.visit(this, o);
    return (first == null) ? "" : first.toString();
  }

  @Override public Object visitConstActualParameter(ConstActualParameter ast, Object o){
    Object v = ast.E.visit(this, o);
    if (v == null) return "";
    return "i32 " + v.toString();
  }
  @Override public Object visitFuncActualParameter(FuncActualParameter ast, Object o){ return null; }
  @Override public Object visitProcActualParameter(ProcActualParameter ast, Object o){ return null; }
  @Override public Object visitVarActualParameter(VarActualParameter ast, Object o){
    // Var actual must pass the address (i32*) of the variable/expression
    // For simple variables we can return their alloca/global name; for subscripts we compute a GEP
    if (ast.V instanceof SimpleVname) {
      SimpleVname sv = (SimpleVname) ast.V;
      String var = sv.I.spelling;
      SymbolTable.Symbol sym = symtab.lookup(var);
      if (sym == null) {
        sym = symtab.declareLocal(var);
        body.insert(0, "  " + sym.llvmName + " = alloca i32\n");
      }
      return "i32* " + sym.llvmName;
    } else {
      // delegate to the vname visitor (e.g., subscript) which should return a pointer name
      Object addr = ast.V.visit(this, o);
      if (addr == null) return "";
      return "i32* " + addr.toString();
    }
  }
  @Override public Object visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, Object o){ return ""; }
  @Override public Object visitSingleActualParameterSequence(SingleActualParameterSequence ast, Object o){
    Object first = ast.AP.visit(this, o);
    return (first == null) ? "" : first.toString();
  }
  @Override public Object visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, Object o){
    Object first = ast.AP.visit(this, o);
    Object rest = ast.APS.visit(this, o);
    String sfirst = (first == null) ? "" : first.toString();
    String srest = (rest == null) ? "" : rest.toString();
    if (srest.isEmpty()) return sfirst;
    if (sfirst.isEmpty()) return srest;
    return sfirst + ", " + srest;
  }

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
  @Override public Object visitIntegerLiteral(IntegerLiteral ast, Object o){ return null; }
  @Override public Object visitOperator(Operator ast, Object o){ return null; }

  @Override public Object visitDotVname(DotVname ast, Object o){
    // Record field access not yet implemented; return null for now
    return null;
  }

  @Override public Object visitSimpleVname(SimpleVname ast, Object o){
    String var = ast.I.spelling;
    SymbolTable.Symbol sym = symtab.lookup(var);
    if (sym == null) {
      sym = symtab.declareLocal(var);
      body.insert(0, "  " + sym.llvmName + " = alloca i32\n");
    }
    // return the pointer to the storage (alloca or global name)
    return sym.llvmName;
  }

  @Override public Object visitSubscriptVname(SubscriptVname ast, Object o){
    // assumes base is a SimpleVname and elements are i32; emits GEP to compute address
    if (!(ast.V instanceof SimpleVname)) return null;
    SimpleVname base = (SimpleVname) ast.V;
    String baseName = base.I.spelling;
    SymbolTable.Symbol sym = symtab.lookup(baseName);
    if (sym == null) {
      sym = symtab.declareLocal(baseName);
      body.insert(0, "  " + sym.llvmName + " = alloca i32\n");
    }
    // compute index
    String idx = (String) ast.E.visit(this, o);
    String t = nextTmp();
    body.append("  " + t + " = getelementptr inbounds i32, i32* " + sym.llvmName + ", i32 " + idx + "\n");
    return t;
  }

  @Override
  public Object visitProgram(Program ast, Object o) {
    // Generate code for the program body
    ast.C.visit(this, null);
    return getLLVM();
  }
}
