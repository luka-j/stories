package rs.luka.stories.parser.types;

import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.parser.Expressions;
import rs.luka.stories.runtime.State;

/**
 * Created by luka on 3.6.17..
 */
public class Answer implements AnswerLike {
    protected String variable;
    protected String text;

    public Answer(String variable, String text) throws InterpretationException {
        State.checkName(variable);
        this.variable = variable;
        this.text = text;
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public Object getContent(State state) {
        return getText(state);
    }

    public String getText(State state) {
        return Expressions.substituteVariables(text, state);
    }
}
