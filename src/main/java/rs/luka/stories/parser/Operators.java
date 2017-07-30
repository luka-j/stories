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
