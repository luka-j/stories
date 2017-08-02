package rs.luka.stories.parser.types;

import rs.luka.stories.parser.Expressions;
import rs.luka.stories.parser.Type;
import rs.luka.stories.runtime.Chapter;

/**
 * Created by luka on 4.6.17..
 */
public class IfStatement extends Statement {
    private Line endIf;
    private String expression;

    protected IfStatement(Chapter chapter, String statement, int indent) {
        super(chapter, indent);
        this.expression = statement.substring(0, statement.length()-1);
    }

    @Override
    public Line execute() {
        if(Type.isTruthy(Expressions.eval(expression, chapter.getState())))
            return nextLine;
        else
            return endIf;
    }

    public void setNextIfTrue(Line line) {
        nextLine = line;
    }

    public void setNextIfFalse(Line line) {
        endIf = line;
    }
}
