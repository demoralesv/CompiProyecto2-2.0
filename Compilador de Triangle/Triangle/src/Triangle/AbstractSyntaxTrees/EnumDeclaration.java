package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;
import java.util.List;

public class EnumDeclaration extends Declaration {

  public final Identifier typeId;
  public final List<Identifier> values;

  public EnumDeclaration(Identifier typeId, List<Identifier> values, SourcePosition position) {
    super(position);
    this.typeId = typeId;
    this.values = values;
  }

  @Override
  public Object visit(Visitor v, Object o) {
    return v.visitEnumDeclaration(this, o);
  }
}
