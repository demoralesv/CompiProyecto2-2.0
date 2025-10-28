package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;
import java.util.List;

public class EnumTypeDenoter extends TypeDenoter {

  public EnumTypeDenoter(Identifier typeId, List<Identifier> values, SourcePosition thePosition) {
    super(thePosition);
    this.typeId = typeId;
    this.values = values;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitEnumTypeDenoter(this, o);
  }

  public boolean equals(Object obj) {
    if (obj != null && obj instanceof EnumTypeDenoter) {
      return this.typeId.spelling.equals(((EnumTypeDenoter)obj).typeId.spelling);
    }
    return false;
  }

  public Identifier typeId;
  public List<Identifier> values;
}