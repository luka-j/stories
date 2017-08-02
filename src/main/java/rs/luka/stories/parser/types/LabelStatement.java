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

package rs.luka.stories.parser.types;

import rs.luka.stories.runtime.Chapter;

public class LabelStatement extends Statement {
    private String label;

    protected LabelStatement(Chapter chapter, String statement, int indent) {
        super(chapter, indent);
        label = statement.substring(0, statement.length()-1);
    }

    @Override
    public Line execute() {
        return nextLine; //it essentialy does nothing
    }

    public String getLabel() {
        return label;
    }
}
