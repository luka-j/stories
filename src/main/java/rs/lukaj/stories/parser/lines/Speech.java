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
import rs.lukaj.stories.exceptions.ExecutionException;
import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.parser.Expressions;
import rs.lukaj.stories.parser.LineType;
import rs.lukaj.stories.runtime.Chapter;
import rs.lukaj.stories.runtime.State;

/**
 * Created by luka on 3.6.17..
 */
public class Speech extends Line {
    public static final LineType LINE_TYPE = LineType.SPEECH;

    protected String character;
    protected String text;

    public Speech(Chapter chapter, String character, String text, int lineNumber, int indent)
            throws InterpretationException {
        super(chapter, lineNumber, indent);
        State.checkName(character);
        this.text = text;
        this.character = character;
    }

    @Override
    public Line execute() {
        chapter.getDisplay().showSpeech(character, getAvatar(character),
                Expressions.substituteVariables(text, chapter.getState()));
        return nextLine;
    }

    @Override
    public String generateCode() {
        try {
            String var = null;
            if (!chapter.getState().hasVariable(character))
                var = new AssignStatement(chapter, getLineNumber() - 1, getIndent(), character, null).generateCode();
            String speech = Utils.generateIndent(getIndent()) + LINE_TYPE.makeLine(character, text);
            return var == null ? speech : var + "\n" + speech;
        } catch (InterpretationException e) {
            throw new ExecutionException("Cannot generate speech - variable name not valid", e);
        }
    }

    public String getRawText() {
        return text;
    }
}
