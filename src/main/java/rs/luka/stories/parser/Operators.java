/*
 Stories - an interactive storytelling language
 Copyright (C) 2017 Luka Jovičić

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published
 by the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package rs.luka.stories.parser;

import net.objecthunter.exp4j.operator.Operator;

import static net.objecthunter.exp4j.operator.Operator.PRECEDENCE_ADDITION;

public class Operators {
    private static final int PRECEDENCE_COMPARISON = PRECEDENCE_ADDITION - 100;
    private static final int PRECEDENCE_AND = PRECEDENCE_ADDITION - 50;
    private static final int PRECEDENCE_OR = PRECEDENCE_ADDITION - 51;
    private static final int PRECEDENCE_NOT = PRECEDENCE_ADDITION - 52;


    public static final Operator gt =
            new Operator(">", 2, true, PRECEDENCE_COMPARISON) {
                @Override
                public double apply(double... args) {
                    if(args[0] > args[1]) return 1;
                    else return 0;
                }
            };

    public static final Operator gte =
            new Operator(">=", 2, true, PRECEDENCE_COMPARISON) {
                @Override
                public double apply(double... args) {
                    if(args[0] >= args[1]) return 1;
                    else return 0;
                }
            };

    public static final Operator lt =
            new Operator("<", 2, true, PRECEDENCE_COMPARISON) {
                @Override
                public double apply(double... args) {
                    if(args[0] < args[1]) return 1;
                    else return 0;
                }
            };

    public static final Operator lte =
            new Operator("<=", 2, true, PRECEDENCE_COMPARISON) {
                @Override
                public double apply(double... args) {
                    if(args[0] <= args[1]) return 1;
                    else return 0;
                }
            };

    public static final Operator eq =
            new Operator("=", 2, true, PRECEDENCE_COMPARISON) {
                @Override
                public double apply(double... args) {
                    if(args[0] == args[1]) return 1;
                    else return 0;
                }
            };

    public static final Operator and =
            new Operator("&", 2, true, PRECEDENCE_AND) {
                @Override
                public double apply(double... args) {
                    if(Type.isTruthy(args[0]) && Type.isTruthy(args[1])) return 1;
                    return 0;
                }
            };

    public static final Operator or =
            new Operator("|", 2, true, PRECEDENCE_OR) {
                @Override
                public double apply(double... args) {
                    if(Type.isTruthy(args[0])|| Type.isTruthy(args[1])) return 1;
                    return 0;
                }
            };

    public static final Operator not =
            new Operator("!", 1, true, PRECEDENCE_NOT) {
                @Override
                public double apply(double... args) {
                    if(Type.isTruthy(args[0])) return 0;
                    return 1;
                }
            };

    public static Operator[] operators() {
        return new Operator[] {gt, gte, lt, lte, eq, and, or, not};
    }

}
