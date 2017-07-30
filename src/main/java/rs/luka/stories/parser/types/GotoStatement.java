package rs.luka.stories.parser.types;

import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.runtime.Chapter;

/**
 * Created by luka on 4.6.17..
 */
public class GotoStatement extends Statement {
    private String targetLabel;

    protected GotoStatement(Chapter chapter, String statement, int indent) throws InterpretationException {
        super(chapter, indent);
        targetLabel = statement.substring(1);
    }

    @Override
    public Line execute() {
        return nextLine;
    }

    public void setJump(Chapter chapter) throws InterpretationException {
        nextLine = chapter.getLabel(targetLabel);
        if(nextLine == null) throw new InterpretationException("Invalid label for goto");
    }
}
