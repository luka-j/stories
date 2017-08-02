package rs.luka.stories.parser.types;

import rs.luka.stories.exceptions.ExecutionException;
import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.parser.Expressions;
import rs.luka.stories.runtime.Chapter;
import rs.luka.stories.runtime.State;

/**
 * Created by luka on 4.6.17..
 */
public class AssignStatement extends Statement {

    private String variable, expression;

    protected AssignStatement(Chapter chapter, String statement, int indent) throws InterpretationException {
        super(chapter, indent);
        String[] tokens = statement.split("=", 2);
        if(tokens.length > 2)
            throw new InterpretationException("Malformed assign-statement");
        variable = tokens[0];
        State.checkName(variable);
        if(tokens.length > 1)
            expression = tokens[1];
        else
            expression = "";
        //if(containsLogicalOps && containsNumericOps)
        //    throw new InterpretationException("Malformed expression in assign-statement");
        //todo we're allowing all kinds of expressions for now

    }

    @Override
    public Line execute() {
        State state = chapter.getState();
        try {
            Object res = Expressions.eval(expression, state);
            if(res instanceof Double)
                state.setVariable(variable, (Double)res);
            else
                state.setVariable(variable, res.toString());
        } catch (InterpretationException e) {
            throw new ExecutionException("Uncaught InterpretationException in AssignStatement#execute");
        }
        return nextLine;
    }


}
