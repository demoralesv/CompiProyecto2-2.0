package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;
import java.util.List;


public class EnumTypeDenoter extends TypeDenoter {
  public Identifier typeId;
  public List<Identifier> values;

  public EnumTypeDenoter(Identifier typeId, List<Identifier> values, SourcePosition pos) {
    super(pos);
    this.typeId = typeId;
    this.values = values;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitEnumTypeDenoter(this, o);
  }

  public boolean equals(Object obj) {
    if (obj instanceof EnumTypeDenoter) {
      EnumTypeDenoter other = (EnumTypeDenoter) obj;
      return this.typeId.spelling.equals(other.typeId.spelling);
    }
    return false;
  }
}
