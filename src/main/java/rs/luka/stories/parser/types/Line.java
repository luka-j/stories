package rs.luka.stories.parser.types;

import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.runtime.Chapter;

import java.io.File;

/**
 * Created by luka on 3.6.17.
 */
public abstract class Line {
    protected Line nextLine;
    protected final Chapter chapter;

    private final int indent;

    public abstract Line execute();

    public int getIndent() {
        return indent;
    }

    public Line(Chapter chapter, int indent) {
        this.chapter = chapter;
        this.indent = indent;
    }

    protected File getAvatar(String character) {
        return chapter.getImage(chapter.getState().getString(character));
    }

    public void setNextLine(Line nextLine) throws InterpretationException {
        if(nextLine == this) throw new InterpretationException("Attempting to set nextLine to this, will cause infinite loop");
        this.nextLine = nextLine;
    }

    public Line getNextLine() {
        return nextLine;
    }
}
