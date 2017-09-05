package rs.lukaj.stories.parser.types;

import rs.lukaj.stories.runtime.Chapter;

public class Nop extends Line {
    public Nop(Chapter chapter, int lineNumber, int indent) {
        super(chapter, lineNumber, indent);
    }

    @Override
    public Line execute() {
        return nextLine;
    }
}
