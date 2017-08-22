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
    STRING(String.class, "S", false),
    DOUBLE(Double.class, "N", false),
    CONSTANT_DOUBLE(Double.class, "N", true);

    public final Class typeClass;
    public final String mark;
    public final boolean isConst;
    Type(Class typeClass, String mark, boolean isConst) {
        this.typeClass = typeClass;
        this.mark = mark;
        this.isConst = isConst;
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
