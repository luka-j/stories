package rs.luka.stories.parser.types;

import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.runtime.State;

import java.io.File;

/**
 * Created by luka on 4.6.17..
 */
public class PictureAnswer implements AnswerLike {
    private String variable;
    private File picture;

    public PictureAnswer(String variable, File picture) throws InterpretationException {
        State.checkName(variable);
        this.variable = variable;
        this.picture = picture;
        if(!picture.isFile())
            throw new IllegalArgumentException("Picture " + picture.getAbsolutePath() + " doesn't exist");
    }

    public File getPicture() {
        return picture;
    }

    @Override
    public String getVariable() {
        return variable;
    }

    @Override
    public Object getContent(State state) {
        return getPicture();
    }
}
