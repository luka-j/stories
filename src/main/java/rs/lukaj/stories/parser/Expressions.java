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

package rs.lukaj.stories.parser;

import net.objecthunter.exp4j.ExpressionBuilder;
import rs.lukaj.stories.exceptions.ExecutionException;
import rs.lukaj.stories.runtime.State;

import java.util.Arrays;
import java.util.regex.Pattern;

public class Expressions {
    private static final Pattern NUMERIC_OPS = Pattern.compile(".*[<>*/%^\\-]+.*");
    private static final Pattern LOGICAL_OPS = Pattern.compile(".*[&|!]+.*"); //not allowing XORing for now

    public static Object eval(String expression, State state) {
        if(expression.isEmpty()) return expression;

        boolean containsNumericOps = NUMERIC_OPS.matcher(expression).matches();
        boolean containsLogicalOps = LOGICAL_OPS.matcher(expression).matches();

        Object res;
        if (!containsNumericOps && !containsLogicalOps) {
            expression = expression.replaceAll("[()]", "");
            res = evalAddition(expression, state);
        } else {
            res = evalOperation(expression, state);
        }

        return res;
    }

    private static Object evalAddition(String expression, State state) {
        String[] sides = expression.split("=");
        if(sides.length > 2) throw new ExecutionException("malformed basic expression: multiple =");
        String[] vars = sides[sides.length-1].split("\\s*\\+\\s*");
        boolean isNumeric = true;
        double sum = 0;
        for(String var : vars) {
            if(!state.isDouble(var)) {
                isNumeric = false;
                break;
            } else {
                sum += state.getDouble(var);
            }
        }
        if(isNumeric && sides.length == 1) return sum;
        if(isNumeric && sides.length == 2) return state.getDouble(sides[0]) == sum ? 1 : 0;

        StringBuilder res = new StringBuilder();
        String var;
        for(String str : vars)
            if((var=state.getString(str)) != null)
                res.append(state.getString(var));
            else
                res.append(str);
        if(sides.length == 1) return res.toString();
        else return res.toString().equals(state.getString(sides[0])) ? 1 : 0;
    }

    private static double evalOperation(String expression, State state) {
        return new ExpressionBuilder(expression)
                .implicitMultiplication(true)
                .operator(Arrays.asList(Operators.operators()))
                .variables(state.getVariableNames())
                .build()
                .setVariableProvider(state)
                .evaluate();
    }

    public static String substituteVariables(String expression, State state) {
        StringBuilder res = new StringBuilder(), var = new StringBuilder();
        boolean isVariable = false;
        for(int i=0; i<expression.length(); i++) {
            char ch = expression.charAt(i);
            if(!isVariable) {
                if(ch == '[')
                    isVariable = true;
                else
                    res.append(ch);
            } else {
                if(ch == ']') {
                    String varStr = var.toString();
                    if(state.hasVariable(varStr)) res.append(state.getString(varStr));
                    else res.append('[').append(var).append(']');
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
