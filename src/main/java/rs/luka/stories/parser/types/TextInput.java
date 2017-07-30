package rs.luka.stories.parser.types;

import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.runtime.Chapter;
import rs.luka.stories.runtime.State;

/**
 * Created by luka on 4.6.17..
 */
public class TextInput extends Line {
    protected String variable;
    protected String hint;

    public TextInput(Chapter chapter, String variable, String hint, int indent) throws InterpretationException {
        super(chapter, indent);
        State.checkName(variable);
        this.variable = variable;
        this.hint = hint;
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public Line execute() {
        String input = chapter.getDisplay().showInput(hint);
        try {
            chapter.getState().setVariable(variable, input);
        } catch (InterpretationException e) {
            throw new RuntimeException("Uncaught InterpretationException in TextInput#execute", e);
        }
        return nextLine;
    }
}
