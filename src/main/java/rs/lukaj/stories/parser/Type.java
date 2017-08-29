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

public enum Type {
    NULL(Void.class, "N", false, false),
    STRING(String.class, "S", false, false),
    DOUBLE(Double.class, "D", false, true),
    CONSTANT_DOUBLE(Double.class, "C", true, true);

    public final Class typeClass;
    public final String mark;
    public final boolean isConst;
    public final boolean isNumeric;
    Type(Class typeClass, String mark, boolean isConst, boolean isNumeric) {
        this.typeClass = typeClass;
        this.mark = mark;
        this.isConst = isConst;
        this.isNumeric = isNumeric;
    }

    public static boolean isTruthy(Object value) {
        if(value instanceof Number) {
            Double doubleVal = ((Number)value).doubleValue();
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
