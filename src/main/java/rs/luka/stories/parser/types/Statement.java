package rs.luka.stories.parser.types;

import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.runtime.Chapter;

/**
 * Created by luka on 4.6.17..
 */
public abstract class Statement extends Line {

    protected Statement(Chapter chapter, int indent) {
        super(chapter, indent);
    }

    public static Statement create(Chapter chapter, String statement, int indent) throws InterpretationException {
        statement = statement.substring(1);
        if(statement.endsWith("?"))
            return new IfStatement(chapter, statement, indent);
        if(statement.endsWith(":")) {
            LabelStatement stmt = new LabelStatement(chapter, statement, indent);
            chapter.addLabel(stmt.getLabel(), stmt);
        }
        if(statement.startsWith(">"))
            return new GotoStatement(chapter, statement, indent);
        else
            return new AssignStatement(chapter, statement, indent);
    }
}
