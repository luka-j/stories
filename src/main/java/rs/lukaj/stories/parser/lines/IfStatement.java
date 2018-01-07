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

import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.parser.Expressions;
import rs.lukaj.stories.parser.Type;
import rs.lukaj.stories.runtime.Chapter;

/**
 * Created by luka on 4.6.17..
 */
public class IfStatement extends Statement {
    private Line endIf;
    private Expressions expression;

    protected IfStatement(Chapter chapter, String statement, int lineNumber, int indent)
            throws InterpretationException {
        super(chapter, lineNumber, indent);
        this.expression = new Expressions(statement.substring(0, statement.length()-1), chapter.getState());
    }

    public IfStatement(Chapter chapter, int lineNumber, int indent, String expression) throws InterpretationException {
        super(chapter, lineNumber, indent);
        this.expression = new Expressions(expression, chapter.getState());
    }

    @Override
    public Line execute() {
        if(Type.isTruthy(expression.eval()))
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

    /**
     * Used for generating the statement - generates the expression part of this statement, stripped of
     * all the extra symbols such as : (at the beginning) and ? (at the end)
     * @return the expression this if statement tests for
     */
    @Override
    public StringBuilder generateStatement() {
        return new StringBuilder(expression.literal);
    }

    public Expressions getExpression() {
        return expression;
    }
}
