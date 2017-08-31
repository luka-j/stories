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

/**
 * Created by luka on 4.6.17..
 */
public class GotoStatement extends Statement {
    private String targetLabel;
    private Line jumpTo;

    protected GotoStatement(Chapter chapter, String statement, int indent) throws InterpretationException {
        super(chapter, indent);
        targetLabel = statement.substring(1);
        Line target = chapter.getLabel(targetLabel);
        if(target != null) //if there are multiple same labels, prefer closest previous
            jumpTo = target;
    }

    @Override
    public Line execute() {
        return jumpTo;
    }

    public boolean hasSetJump() {
        return jumpTo != null;
    }

    public void setJump(Chapter chapter) throws InterpretationException {
        jumpTo = chapter.getLabel(targetLabel);
        if(jumpTo == null) throw new InterpretationException("Invalid label for goto");
    }
}
