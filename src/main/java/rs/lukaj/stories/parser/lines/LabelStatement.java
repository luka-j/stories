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

package rs.lukaj.stories.parser.lines;

import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.runtime.Chapter;

public class LabelStatement extends Statement {
    private String label;

    public LabelStatement(Chapter chapter, String label, int lineNumber, int indent)
            throws InterpretationException {
        super(chapter, lineNumber, indent);
        if(label.contains("?") || label.contains(">"))
            throw new InterpretationException("Label shouldn't contain ? or >");
        if(label.isEmpty())
            throw new InterpretationException("Label shouldn't be empty!");
        this.label = label.substring(0, label.length()-1);
    }

    @Override
    public Line execute() {
        return nextLine; //it essentialy does nothing
    }

    public String getLabel() {
        return label;
    }

    @Override
    protected StringBuilder generateStatement() {
        return new StringBuilder(label);
    }
}
