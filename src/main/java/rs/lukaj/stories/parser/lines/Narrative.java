/*
  Stories - an interactive storytelling language
  Copyright (C) 2017 Luka Jovičić

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
import rs.lukaj.stories.parser.Expressions;
import rs.lukaj.stories.parser.LineType;
import rs.lukaj.stories.runtime.Chapter;

/**
 * Created by luka on 3.6.17..
 */
public class Narrative extends Line {
    public static final LineType LINE_TYPE = LineType.NARRATIVE;

    protected final String text;

    public Narrative(Chapter chapter, String text, int lineNumber, int indent) {
        super(chapter, lineNumber, indent);
        this.text = text;
    }

    @Override
    public Line execute() {
        chapter.getDisplay().showNarrative(Expressions.substituteVariables(text, chapter.getState()));
        return nextLine;
    }

    @Override
    public String generateCode() {
        return Utils.generateIndent(getIndent()) + LINE_TYPE.makeLine(text);
    }

    public String getRawText() {
        return text;
    }
}
