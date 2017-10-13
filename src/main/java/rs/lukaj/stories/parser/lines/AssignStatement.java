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

import rs.lukaj.stories.exceptions.ExecutionException;
import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.parser.Expressions;
import rs.lukaj.stories.runtime.Chapter;
import rs.lukaj.stories.runtime.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luka on 4.6.17..
 */
public class AssignStatement extends Statement {

    private List<String> variable = new ArrayList<>();
    private List<Expressions> expression = new ArrayList<>();

    private static List<String> splitAssignments(String statement) {
        List<String> assignments = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for(int i=0; i<statement.length(); i++) {
            char ch = statement.charAt(i);
            if(ch == '\\') {
                char next = i+1 < statement.length() ? statement.charAt(i+1) : 0;
                if(next == ',') {
                    current.append(next);
                    i++;
                }
            } else if(ch == ',') {
                assignments.add(current.toString());
                current.delete(0, current.length());
            } else {
                current.append(ch);
            }
        }
        assignments.add(current.toString());
        return assignments;
    }

    protected AssignStatement(Chapter chapter, String statement, int lineNumber, int indent)
            throws InterpretationException {
        super(chapter, lineNumber, indent);
        List<String> statements = splitAssignments(statement);
        for(String stmt : statements) {
            String[] tokens = stmt.split("=", 2);
            if (tokens.length > 2) //this is actually always false... for now
                throw new InterpretationException("Malformed assign-statement");
            String varName = tokens[0].trim();
            chapter.getState().declareVariable(varName);
            variable.add(varName);
            if (tokens.length > 1)
                expression.add(new Expressions(tokens[1].trim(), chapter.getState()));
            else
                expression.add(null);
        }
        //if(containsLogicalOps && containsNumericOps)
        //    throw new InterpretationException("Malformed expression in assign-statement");
        //todo we're allowing all kinds of expressions for now, going between the types as necessary

    }

    public AssignStatement(Chapter chapter, int lineNumber, int indent, String variable, String expression) throws InterpretationException {
        super(chapter, lineNumber, indent);
        this.variable.add(variable);
        this.expression.add(new Expressions(expression, chapter.getState()));
    }

    @Override
    public Line execute() {
        State state = chapter.getState();
        for(int i=0; i<variable.size(); i++) {
            String variable = this.variable.get(i);
            Expressions expression = this.expression.get(i);
            if(expression == null) continue;

            try {
                Object res = expression.eval();
                if (res instanceof Double)
                    state.setVariable(variable, (Double) res);
                else
                    state.setVariable(variable, res.toString());
            } catch (InterpretationException e) {
                throw new ExecutionException("Uncaught InterpretationException in AssignStatement#execute");
            }
        }
        return nextLine;
    }

    @Override
    protected StringBuilder generateStatement() {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<variable.size(); i++) {
            sb.append(variable.get(i));
            if(expression.get(i) != null)
                sb.append(" = ").append(expression.get(i).literal);

            sb.append(',');
        }
        sb.deleteCharAt(sb.length()-1);
        return sb;
    }

//    @Override
//    public String generateCode(int indent) {
//        String code = super.generateCode(indent);
//    }


}
