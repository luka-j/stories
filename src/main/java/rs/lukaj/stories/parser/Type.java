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

import java.util.List;

public enum Type {
    NULL(Void.class, "N", 0),
    STRING(String.class, "S", 0),
    DOUBLE(Double.class, "D", P.NUMERIC),
    STRING_LIST(List.class, "S[", P.LIST),
    CONSTANT_DOUBLE(Double.class, "C", P.NUMERIC | P.CONST),
    CONSTANT_STRING(String.class, "L", P.CONST);

    public static class P {
        public static final int CONST = 1;
        public static final int NUMERIC = 1 << 1;
        public static final int LIST = 1 << 2;
    }

    public final Class typeClass;
    public final String mark;
    public final long properties;

    Type(Class typeClass, String mark, int properties) {
        this.typeClass = typeClass;
        this.mark = mark;
        this.properties = properties;
    }

    public static boolean isTruthy(Object value) {
        if(value instanceof Number) {
            Double doubleVal = ((Number)value).doubleValue();
            return doubleVal != 0 && !Double.isNaN(doubleVal);
        } else if(value instanceof String) {
            String str = value.toString();
            return !str.isEmpty();
        } else if(value instanceof List) {
            return !((List) value).isEmpty();
        } else if(value instanceof Boolean) {
            return (Boolean)value;
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

    public boolean is(long prop) {
        return (properties&prop) != 0;
    }
}
