/*
  Stories - an interactive storytelling language
  Copyright (C) 2017-2018 Luka Jovičić

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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
    private final String content;

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
