package rs.lukaj.stories.parser.lines;

import rs.lukaj.stories.Utils;
import rs.lukaj.stories.parser.LineType;
import rs.lukaj.stories.runtime.Chapter;

/**
 * Essentially a Nop. Holds a string. Kind of a persistent comment.
 * Some directives are discarded in the preprocessor step, others are left in the compiled code.
 * Implementation-specific directives shall start with a special character (e.g. !)
 */
public class Directive extends Line {
    private String content;

    public Directive(Chapter chapter, int lineNumber, int indent, String content) {
        super(chapter, lineNumber, indent);
        this.content = content;
    }

    @Override
    public Line execute() {
        return nextLine;
    }

    @Override
    public String generateCode() {
        return Utils.generateIndent(getIndent()) + LineType.DIRECTIVE.makeLine(content);
    }

    public String getDirective() {
        return content;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Directive && ((Directive)obj).content.equals(content);
    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }
}
