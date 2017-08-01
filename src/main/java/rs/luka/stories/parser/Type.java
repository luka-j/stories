package rs.luka.stories.parser;

public enum Type {
    STRING(String.class, "S"), DOUBLE(Double.class, "N");

    public final Class typeClass;
    public final String mark;
    Type(Class typeClass, String mark) {
        this.typeClass = typeClass;
        this.mark = mark;
    }

    public static boolean isTruthy(Double value) {
        return value != null && value != 0 && value != Double.NaN;
    }

    public static Type getByMark(String mark) {
        for(Type t : Type.values())
            if(t.mark.equals(mark))
                return t;
        return null;
    }
}
