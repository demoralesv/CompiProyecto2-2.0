package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;
import java.util.LinkedHashMap;

// Expresión Match - Representa una expresión condicional que evalúa múltiples casos
// y devuelve el valor de la expresión correspondiente al caso que coincida
public class MatchExpression extends Expression {
    
    // Constructor que inicializa la expresión match con la expresión a evaluar,
    // la lista de casos (expresión -> expresión) y una expresión por defecto
    public MatchExpression (Expression eAST, LinkedHashMap<Expression, Expression> EList, Expression e2AST, SourcePosition thePosition) {
        super (thePosition);
        E1 = eAST;
        this.EList = EList;
        E2 = e2AST;
    } 
    
    // Método visitor para el patrón Visitor
    public Object visit (Visitor v, Object o) {
        return v.visitMatchExpression(this, o);
    }
    
    // Expresión que se evalúa para comparar con los casos
    public Expression E1;
    // Mapa que asocia cada expresión de caso con su expresión de retorno correspondiente
    public LinkedHashMap<Expression, Expression> EList;
    // Expresión por defecto que se retorna si ningún caso coincide
    public Expression E2;
    

}
