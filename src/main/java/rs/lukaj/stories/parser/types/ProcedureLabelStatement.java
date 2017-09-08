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

package rs.lukaj.stories.parser.types;

import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.runtime.Chapter;

public class ProcedureLabelStatement extends LabelStatement {
    protected GotoStatement jumpedFrom = null;

    protected ProcedureLabelStatement(Chapter chapter, String statement, int lineNumber, int indent) throws InterpretationException {
        super(chapter, statement.substring(1), lineNumber, indent);
    }

    @Override
    public Line execute() {
        if(jumpedFrom != null) return nextLine; //we got here by goto
        else {
            Line next = nextLine;
            while(next != null && !(next instanceof ReturnStatement)) next = next.nextLine;

            if(next == null) return null;
            else return next.nextLine;
        }
    }
}
