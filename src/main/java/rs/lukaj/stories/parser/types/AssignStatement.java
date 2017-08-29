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

package rs.lukaj.stories.parser.types;

import rs.lukaj.stories.exceptions.ExecutionException;
import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.parser.Expressions;
import rs.lukaj.stories.runtime.Chapter;
import rs.lukaj.stories.runtime.State;

/**
 * Created by luka on 4.6.17..
 */
public class AssignStatement extends Statement {

    private String variable, expression;

    protected AssignStatement(Chapter chapter, String statement, int indent) throws InterpretationException {
        super(chapter, indent);
        String[] tokens = statement.split("=", 2);
        if(tokens.length > 2) //this is actually always false... for now
            throw new InterpretationException("Malformed assign-statement");
        variable = tokens[0];
        chapter.getState().declareVariable(variable);
        if(tokens.length > 1)
            expression = tokens[1];
        else
            expression = "";
        //if(containsLogicalOps && containsNumericOps)
        //    throw new InterpretationException("Malformed expression in assign-statement");
        //todo we're allowing all kinds of expressions for now

    }

    @Override
    public Line execute() {
        State state = chapter.getState();
        try {
            Object res = Expressions.eval(expression, state);
            if(res instanceof Double)
                state.setVariable(variable, (Double)res);
            else
                state.setVariable(variable, res.toString());
        } catch (InterpretationException e) {
            throw new ExecutionException("Uncaught InterpretationException in AssignStatement#execute");
        }
        return nextLine;
    }


}
