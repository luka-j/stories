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

import net.objecthunter.exp4j.function.Function;

public class Functions {
    private static final String FUNC_RANDOM = "random";
    private static final String FUNC_RANDINT = "randomInt";

    private static final Function random = new Function(FUNC_RANDOM, 0) {
        @Override
        public double apply(double... args) {
            return Math.random();
        }
    };
    private static final Function randInt = new Function(FUNC_RANDINT, 1) {
        @Override
        public double apply(double... args) {
            return (int)(Math.random() * args[0]);
        }
    };

    public static Function[] functions() {
        return new Function[] {random, randInt};
    }
}
