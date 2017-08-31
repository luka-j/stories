package rs.lukaj.stories.parser.types;

import rs.lukaj.stories.runtime.Chapter;

public class Nop extends Line {
    public Nop(Chapter chapter, int indent) {
        super(chapter, indent);
    }

    @Override
    public Line execute() {
        return nextLine;
    }
}
