package rs.lukaj.stories.parser;

import net.objecthunter.exp4j.function.Function;

public class Functions {
    private static final String FUNC_RANDOM = "random";
    private static final String FUNC_RANDINT = "randomInt";

    private static Function random = new Function(FUNC_RANDOM, 0) {
        @Override
        public double apply(double... args) {
            return Math.random();
        }
    };
    private static Function randInt = new Function(FUNC_RANDINT, 1) {
        @Override
        public double apply(double... args) {
            return (int)(Math.random() * args[0]);
        }
    };

    public static Function[] functions() {
        return new Function[] {random, randInt};
    }
}
