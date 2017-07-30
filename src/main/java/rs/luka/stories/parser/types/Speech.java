package rs.luka.stories.parser.types;

import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.runtime.Chapter;
import rs.luka.stories.runtime.State;

/**
 * Created by luka on 3.6.17..
 */
public class Speech extends Line {
    protected String character;
    protected String text;

    public Speech(Chapter chapter, String character, String text, int indent) throws InterpretationException {
        super(chapter, indent);
        State.checkName(character);
        this.text = text;
        this.character = character;
    }

    @Override
    public Line execute() {
        chapter.getDisplay().showSpeech(character, getAvatar(character), text);
        return nextLine;
    }
}
