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

package rs.luka.stories.parser.types;

import rs.luka.stories.parser.Expressions;
import rs.luka.stories.parser.Type;
import rs.luka.stories.runtime.Chapter;

/**
 * Created by luka on 4.6.17..
 */
public class IfStatement extends Statement {
    private Line endIf;
    private String expression;

    protected IfStatement(Chapter chapter, String statement, int indent) {
        super(chapter, indent);
        this.expression = statement.substring(0, statement.length()-1);
    }

    @Override
    public Line execute() {
        if(Type.isTruthy(Expressions.eval(expression, chapter.getState())))
            return nextLine;
        else
            return endIf;
    }

    public void setNextIfTrue(Line line) {
        nextLine = line;
    }

    public void setNextIfFalse(Line line) {
        endIf = line;
    }
}
