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
import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.parser.LineType;
import rs.lukaj.stories.runtime.Chapter;

/**
 * Created by luka on 4.6.17..
 */
public class TextInput extends Line {
    public static final LineType LINE_TYPE = LineType.INPUT;

    protected String variable;
    protected String hint;

    public TextInput(Chapter chapter, String variable, String hint, int lineNumber,
                     int indent) throws InterpretationException {
        super(chapter, lineNumber, indent);
        chapter.getState().declareVariable(variable);
        this.variable = variable;
        this.hint = hint;
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public Line execute() {
        String input = chapter.getDisplay().showInput(hint);
        try {
            chapter.getState().setVariable(variable, input);
        } catch (InterpretationException e) {
            throw new RuntimeException("Uncaught InterpretationException in TextInput#execute", e);
        }
        return nextLine;
    }

    @Override
    public String generateCode() {
        return Utils.generateIndent(getIndent()) + LINE_TYPE.makeLine(variable, hint);
    }
}
