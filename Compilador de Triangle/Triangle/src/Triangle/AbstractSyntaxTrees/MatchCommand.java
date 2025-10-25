package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;
import java.util.LinkedHashMap;

// Comando Match - Representa una estructura de control tipo switch que permite comparar
// una expresión con múltiples casos y ejecutar el comando correspondiente al caso que coincida
public class MatchCommand extends Command {
    
    // Constructor que inicializa el comando match con la expresión a evaluar,
    // la lista de casos (expresión -> comando) y un comando por defecto
    public MatchCommand (Expression eAST, LinkedHashMap<Expression, Command> CList, Command c2AST, SourcePosition thePosition) {
        super (thePosition);
        E1 = eAST;
        this.CList = CList;
        C = c2AST;
    } 
    
    // Método visitor para el patrón Visitor
    public Object visit (Visitor v, Object o) {
        return v.visitMatchCommand(this, o);
    }
    
    // Expresión que se evalúa para comparar con los casos
    public Expression E1;
    // Mapa que asocia cada expresión de caso con su comando correspondiente
    public LinkedHashMap<Expression, Command> CList;
    // Comando por defecto que se ejecuta si ningún caso coincide
    public Command C;
    

}
