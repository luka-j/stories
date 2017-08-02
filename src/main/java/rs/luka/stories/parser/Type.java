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
