package rs.luka.stories.parser;

public enum Type {
    STRING(String.class), DOUBLE(Double.class);

    public final Class typeClass;
    Type(Class typeClass) {
        this.typeClass = typeClass;
    }

    public static boolean isTruthy(Double value) {
        return value != null && value != 0 && value != Double.NaN;
    }
}
