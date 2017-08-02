package rs.luka.stories.parser.types;

import rs.luka.stories.parser.Expressions;
import rs.luka.stories.runtime.Chapter;

/**
 * Created by luka on 3.6.17..
 */
public class Narrative extends Line {
    protected String text;

    public Narrative(Chapter chapter, String text, int indent) {
        super(chapter, indent);
        this.text = text;
    }

    @Override
    public Line execute() {
        text = Expressions.substituteVariables(text, chapter.getState());
        chapter.getDisplay().showNarrative(text);
        return nextLine;
    }
}
