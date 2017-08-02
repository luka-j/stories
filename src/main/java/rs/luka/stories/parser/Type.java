package rs.luka.stories.parser;

public enum Type {
    STRING(String.class, "S"), DOUBLE(Double.class, "N");

    public final Class typeClass;
    public final String mark;
    Type(Class typeClass, String mark) {
        this.typeClass = typeClass;
        this.mark = mark;
    }

    public static boolean isTruthy(Object value) {
        if(value instanceof Double) {
            Double doubleVal = (Double)value;
            return doubleVal != 0 && doubleVal != Double.NaN;
        } else {
            return false; //this can be thought about... later
        }
    }

    public static Type getByMark(String mark) {
        for(Type t : Type.values())
            if(t.mark.equals(mark))
                return t;
        return null;
    }
}
