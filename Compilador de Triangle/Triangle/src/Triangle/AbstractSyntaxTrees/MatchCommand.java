package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;
import java.util.LinkedHashMap;

public class MatchCommand extends Command {
    public MatchCommand (Expression eAST, LinkedHashMap<Expression, Command> CList, Command c2AST, SourcePosition thePosition) {
        super (thePosition);
        E1 = eAST;
        this.CList = CList;
        C = c2AST;
    } 
    
    public Object visit(Visitor v, Object o) {
        return v.visitMatchCommand(this, o);
    }
    
    public Expression E1;
    public LinkedHashMap<Expression, Command> CList;
    public Command C;
    

}
