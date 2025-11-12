package Triangle.CodeGenerator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

  public static class Symbol {
    public String name;
    public String llvmName;
    public String paramName; // name used in function signature for parameters, e.g. %p_x
    public int level;
    public boolean isGlobal;
    public boolean isParam = false;
    // Layout and storage metadata (for LLVM codegen)
    public int offset = 0; // offset within frame (or 0 for globals)
    public int size = 0;
    public int align = 0;
    public StorageKind storage = null;
    public boolean paramIsRef = false; // for var-formal parameters passed by reference
    public Symbol(String name, String llvmName, int level, boolean isGlobal) {
      this.name = name; this.llvmName = llvmName; this.level = level; this.isGlobal = isGlobal;
    }
  }

  private static class Scope {
    Map<String,Symbol> symbols = new HashMap<>();
    int nextOffset = 0; // for local allocation offsets (frame)
  }

  private Deque<Scope> scopes = new ArrayDeque<>();
  private int currentLevel = 0;

  public SymbolTable() {
    // push global (level 0)
    scopes.push(new Scope());
    currentLevel = 0;
  }

  public void enterScope() {
    currentLevel++;
    scopes.push(new Scope());
  }

  public void exitScope() {
    if (scopes.size() > 1) scopes.pop();
    currentLevel = Math.max(0, currentLevel-1);
  }

  public int getCurrentLevel() { return currentLevel; }

  public Symbol declareGlobal(String name) {
    Scope globals = scopes.getLast();
    Symbol s = new Symbol(name, "@" + name, 0, true);
    s.storage = StorageKind.GLOBAL;
    s.size = 4;
    s.align = 4;
    globals.symbols.put(name, s);
    return s;
  }

  public Symbol declareParam(String name) {
    Scope local = scopes.peek();
    Symbol s = new Symbol(name, "%" + name, currentLevel, false);
    s.isParam = true;
    s.paramName = "%p_" + name;
    s.storage = StorageKind.PARAM;
    // params are passed on the caller side; record a slot (for bookkeeping)
    s.offset = local.nextOffset;
    local.nextOffset += 4;
    s.size = 4;
    s.align = 4;
    local.symbols.put(name, s);
    return s;
  }

  public Symbol declareLocal(String name) {
    Scope local = scopes.peek();
    Symbol s = new Symbol(name, "%" + name, currentLevel, false);
    s.storage = StorageKind.LOCAL;
    s.offset = local.nextOffset;
    local.nextOffset += 4;
    s.size = 4;
    s.align = 4;
    local.symbols.put(name, s);
    return s;
  }

  /** Return the mapping of the current (top) scope so callers can inspect declared symbols. */
  /** Return the mapping of the current (top) scope so callers can inspect declared symbols. */
  public Map<String,Symbol> getCurrentScopeSymbols() {
    return scopes.peek().symbols;
  }

  public Symbol lookup(String name) {
    // search from top to bottom
    for (Scope s : scopes) {
      if (s.symbols.containsKey(name)) return s.symbols.get(name);
    }
    // not found
    return null;
  }

  // Helpers to expose layout information
  public enum StorageKind { GLOBAL, LOCAL, PARAM }

  public void setLayout(Symbol sym, int offset, int size, int align, StorageKind kind) {
    sym.offset = offset;
    sym.size = size;
    sym.align = align;
    sym.storage = kind;
  }
}
