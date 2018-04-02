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

package rs.lukaj.stories.parser;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import rs.lukaj.stories.Utils;
import rs.lukaj.stories.exceptions.ExecutionException;
import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.runtime.State;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Expressions {
    private static final Set<Character> nonStringOps = new HashSet<>();
    static {
        nonStringOps.add('<');
        nonStringOps.add('>');
        nonStringOps.add('*');
        nonStringOps.add('/');
        nonStringOps.add('%');
        nonStringOps.add('^');
        nonStringOps.add('-');
        nonStringOps.add('&');
        nonStringOps.add('|');
        nonStringOps.add('(');
        nonStringOps.add(')');
    } //todo how do we actually concat string literals which contain these?

    private enum ExprType {
        BASIC, STRING, NUMERIC, BASIC_NEGATION, UNDECL
    }
    private ExprType type;
    private Object value;
    private String variable;
    private Expression expression;
    private State state;
    public final String literal;

    public Expressions(String expression, State state) throws InterpretationException {
        this.state = state;
        this.literal = expression;
        if(expression == null || expression.isEmpty()) {
            type = ExprType.BASIC;
            value = "";
        } else if(Utils.isDouble(expression)) {
            type = ExprType.BASIC;
            value = Double.parseDouble(expression);
        } else if(state.hasVariable(expression)) {
            type = ExprType.BASIC;
            variable = expression;
        } else if(expression.charAt(0) == '!' && state.hasVariable(expression.substring(1))) {
            type = ExprType.BASIC_NEGATION;
            variable = expression.substring(1);
        } else {
            if(isStringExpression(expression)) {
                type = ExprType.STRING;
                value = expression;
            } else {
                type = ExprType.NUMERIC;
                try {
                    this.expression = new ExpressionBuilder(expression)
                            .implicitMultiplication(true)
                            .operator(Arrays.asList(Operators.operators()))
                            .functions(Functions.functions())
                            .variables(state.getVariableNames())
                            .build();
                } catch (IllegalArgumentException e) {
                    throw new InterpretationException("Error while parsing numeric expression");
                }
            }
        }
    }

    public Expressions setDecl(boolean decl) {
        if(!decl) type = ExprType.UNDECL;
        return this;
    }

    public boolean isUndecl() {
        return type == ExprType.UNDECL;
    }

    /**
     * Checks whether expression contains any of the {@link #nonStringOps}
     * or a ! not followed by a = (!= is inequality operator, which is
     * allowed for strings)
     * @param expression expression to be checked
     * @return true if expression can be parsed as string expression, false otherwise
     */
    private static boolean isStringExpression(String expression) {
        for(int i=0; i<expression.length(); i++) {
            char curr = expression.charAt(i);
            if(nonStringOps.contains(curr))
                return false;
            char next = 0;
            if(i+1 < expression.length())
                next = expression.charAt(i+1);
            if(curr == '!' && next != '=')
                return false;
        }
        return true;
    }

    public Object eval() {
        switch (type) {
            case BASIC:
                if(value != null) return value;
                if(state.isNumeric(variable)) return state.getDouble(variable);
                else return state.getString(variable);
            case BASIC_NEGATION:
                return Type.isTruthy(state.getObject(variable)) ? 0. : 1.;
            case STRING: return evalAddition(value.toString(), state);
            case NUMERIC: return expression.setVariableProvider(state).evaluate();
            case UNDECL: return null;
            default: throw new IllegalStateException("Illegal expression type"); //this shouldn't happen
        }
    }

    private static Object evalAddition(String expression, State state) {
        String[] sides = expression.split("=");
        boolean negation = false;
        if(sides.length > 1 && sides[0].charAt(sides[0].length()-1) == '!') {
            negation = true;
            sides[0] = sides[0].substring(0, sides[0].length()-1);
        }
        if(sides.length > 2) throw new ExecutionException("malformed basic expression: multiple =");
        String[] rvars = sides[sides.length-1].split("\\s*\\+\\s*");
        boolean isNumeric = true;
        double sum = 0;
        if(!state.isNumeric(sides[0])) {
            isNumeric = false;
        } else {
            for(String var : rvars) {
                if(!state.isNumeric(var)) {
                    isNumeric = false;
                    break;
                } else {
                    sum += state.getDouble(var);
                }
            }
        }
        if(isNumeric && sides.length == 1) return sum;
        if(isNumeric && sides.length == 2) {
            boolean ret = state.getDouble(sides[0]) == sum;
            if(ret ^ negation) return 1;
            else return 0;
        }

        if(sides.length == 2 && !state.isNumeric(sides[0]) && state.hasAssignedVariable(sides[0]) && state.getString(sides[0]).equals(sides[1])) {
            return negation ? 0 : 1;
            //the idea is to support expressions such as q=ans where ans is a variable, and q
            //is supposed to have the value ans
            //no idea how to put that into grammar
        }
        StringBuilder rhs = new StringBuilder();
        String var;
        boolean allVarsAssigned = true;
        for(String varName : rvars)
            if((var=state.getString(varName)) != null) {
                rhs.append(var);
                if(!state.hasAssignedVariable(varName)) allVarsAssigned = false;
            } else {
                rhs.append(varName);
            }
        if(sides.length == 1) return rhs.toString();
        else {
            StringBuilder lhs = new StringBuilder();
            String[] lvars = sides[0].split("\\s*\\+\\s*");
            for(String varName : lvars)
                if((var=state.getString(varName)) != null) {
                    lhs.append(var);
                    if(!state.hasAssignedVariable(varName)) allVarsAssigned = false;
                } else {
                    lhs.append(varName);
                }
            boolean ret = allVarsAssigned && rhs.toString().equals(lhs.toString());
            if(ret ^ negation) return 1;
            else return 0;
        }
    }

    public static String substituteVariables(String expression, State state) {
        StringBuilder res = new StringBuilder(), var = new StringBuilder();
        boolean isVariable = false, skippedBracket = false;
        for(int i=0; i<expression.length(); i++) {
            char ch = expression.charAt(i);
            if(!isVariable) {
                if(ch == '[') {
                    char next = 0;
                    if(i+1<expression.length()) next = expression.charAt(i+1);
                    if(next != '[') {
                        isVariable = true;
                    } else {
                        i++;
                        res.append(ch);
                        skippedBracket = true;
                    }
                } else {
                    res.append(ch);
                    char next = 0;
                    if(i+1<expression.length()) next = expression.charAt(i+1);
                    if(skippedBracket && ch == ']' && next == ']') {
                        i++;
                        skippedBracket = false;
                    }
                }
            } else {
                if(ch == ']') {
                    try {
                        res.append(new Expressions(var.toString(), state).eval());
                    } catch (InterpretationException e) {
                        res.append('[').append(var.toString()).append(']');
                        //todo figure out if falling back like this is really a good idea
                    }
                    isVariable = false;
                    var.delete(0, var.length());
                } else {
                    var.append(ch);
                }
            }
        }
        return res.toString();
    }
}
