package rs.luka.stories.parser.types;

import net.objecthunter.exp4j.ExpressionBuilder;
import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.parser.Operators;
import rs.luka.stories.runtime.Chapter;
import rs.luka.stories.runtime.State;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by luka on 4.6.17..
 */
public class AssignStatement extends Statement {

    private String variable, expression;
    private boolean containsNumericOps;
    private boolean containsLogicalOps;

    private static final Pattern NUMERIC_OPS = Pattern.compile(".*[*/%^\\-]+.*");
    private static final Pattern LOGICAL_OPS = Pattern.compile(".*[&|]+.*"); //not allowing XORing for now

    protected AssignStatement(Chapter chapter, String statement, int indent) throws InterpretationException {
        super(chapter, indent);
        String[] tokens = statement.split("=", 2);
        if(tokens.length != 2)
            throw new InterpretationException("Malformed assign-statement");
        variable = tokens[0];
        State.checkName(variable);
        expression = tokens[1];
        containsNumericOps = NUMERIC_OPS.matcher(expression).matches();
        containsLogicalOps = LOGICAL_OPS.matcher(expression).matches();
        //if(containsLogicalOps && containsNumericOps)
        //    throw new InterpretationException("Malformed expression in assign-statement");
        //todo we're allowing all kinds of expressions for now

    }

    @Override
    public Line execute() {
        State state = chapter.getState();
        try {
            if (!containsNumericOps && !containsLogicalOps) {
                expression = expression.replaceAll("[()]", "");
                Object res = evalAddition();
                if (res instanceof String)
                    state.setVariable(variable, (String) res);
                else
                    state.setVariable(variable, (Double) res);
            } else {
                state.setVariable(variable, eval(expression, state));
            }
        } catch (InterpretationException e) {
            throw new RuntimeException("Uncaught InterpretationException in AssignStatement#execute");
        }
        return nextLine;
    }

    private Object evalAddition() {
        State state = chapter.getState();
        String[] vars = expression.split("\\s*\\+\\s*");
        boolean isNumeric = true;
        double sum = 0;
        for(String var : vars) {
            if(!state.isDouble(var)) {
                isNumeric = false;
                break;
            } else {
                sum += state.getDouble(var);
            }
        }
        if(isNumeric) return sum;

        StringBuilder res = new StringBuilder();
        String var;
        for(String str : vars)
            if((var=state.getString(str)) != null)
                res.append(state.getString(var));
            else
                res.append(str);
        return res.toString();
    }

    public static double eval(String expression, State state) {
        return new ExpressionBuilder(expression)
                .implicitMultiplication(true)
                .operator(Arrays.asList(Operators.operators()))
                .variables(state.getVariableNames())
                .build()
                .setVariableProvider(state)
                .evaluate();
    }
}
