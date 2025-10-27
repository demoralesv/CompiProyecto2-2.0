package Triangle.AbstractSyntaxTrees;

import java.util.List;
import Triangle.SyntacticAnalyzer.SourcePosition;

public class EnumType extends TypeDenoter {

  public final Identifier typeId;
  public final List<Identifier> values;

  public EnumType(Identifier typeId, List<Identifier> values, SourcePosition position) {
    super(position);
    this.typeId = typeId;
    this.values = values;
  }

  @Override
    public boolean equals(Object obj) {
      if (obj instanceof EnumType) {
        EnumType other = (EnumType) obj;
        // Compara por nombre del tipo
        return this.typeId.spelling.equals(other.typeId.spelling);
      }
      return false;
    }
  
  @Override
  public Object visit(Visitor v, Object o) {
    return v.visitEnumType(this, o);
  }
}
