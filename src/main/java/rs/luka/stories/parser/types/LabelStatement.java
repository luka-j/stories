package rs.luka.stories.parser.types;

import rs.luka.stories.runtime.Chapter;

public class LabelStatement extends Statement {
    private String label;

    protected LabelStatement(Chapter chapter, String statement, int indent) {
        super(chapter, indent);
        label = statement.substring(0, statement.length()-1);
    }

    @Override
    public Line execute() {
        return nextLine; //it essentialy does nothing
    }

    public String getLabel() {
        return label;
    }
}
