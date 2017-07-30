package rs.luka.stories.parser.types;

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
        chapter.getDisplay().showNarrative(text);
        return nextLine;
    }
}
