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

package rs.luka.stories;

import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * Created by luka on 16.6.17..
 */
public class Utils {
    /**
     * Matches all doubles except NaN and Infinity
     */
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("[\\x00-\\x20]*[+-]?((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.(\\p{Digit}+)([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?)[\\x00-\\x20]*");

    public static int countLeadingSpaces(String str) {
        int i=0;
        while(i<str.length() && Character.isWhitespace(str.charAt(i)))
            if(str.charAt(i)=='\t') i+=4;
            else if(str.charAt(i) == ' ') i++;
            //this misses other whitespace - it isn't counted
        return i;
    }

    public static String between(String str, char begin, char end) {
        int b=0, e=0;
        while(b<str.length() && str.charAt(b) != begin) b++;
        b++;
        if(b>=str.length()) return null;
        e=b;
        while(e<str.length() && str.charAt(e) != end) e++;
        if(e == str.length()) return null;
        return str.substring(b, e);
    }

    public static Comparator<String> enumeratedStringsComparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            int n1=0, n2=0;
            for(int i=0; i<o1.length() && isAsciiDigit(o1.charAt(i)); i++)
                n1 = n1*10 + (o1.charAt(i) - '0');
            for(int i=0; i<o2.length() && isAsciiDigit(o2.charAt(i)); i++)
                n2 = n2*10 + (o2.charAt(i) - '0');

            return Integer.compare(n1, n2);
        }
    };

    public static boolean isAsciiDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * Checks whether passed string is a valid finite Double value (and not a NaN)
     * @param str string to be checked
     * @return true if variable is finite and can be parsed by {@link Double#parseDouble(String)},
     *      false otherwise
     */
    public static boolean isDouble(String str) {
        return DOUBLE_PATTERN.matcher(str).matches();
    }
}
