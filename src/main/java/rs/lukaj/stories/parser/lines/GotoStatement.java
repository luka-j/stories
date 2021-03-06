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
import rs.lukaj.stories.parser.Expressions;
import rs.lukaj.stories.parser.Type;
import rs.lukaj.stories.runtime.Chapter;

/**
 * Created by luka on 4.6.17..
 */
public class GotoStatement extends Statement {
    private Expressions condition;
    private String targetLabel;
    private LabelStatement jumpTo;

    protected GotoStatement(Chapter chapter, String statement, int lineNumber, int indent)
            throws InterpretationException {
        super(chapter, lineNumber, indent);
        if(!statement.contains("?")) {
            targetLabel = statement.substring(1);
        } else {
            String[] tokens = statement.substring(1).split("\\s*\\?\\s*", 2);
            condition = new Expressions(tokens[0], chapter.getState());
            targetLabel = tokens[1];
        }
        LabelStatement target = chapter.getLabel(targetLabel);
        if(target != null) //if there are multiple same labels, prefer closest previous
            jumpTo = target;
    }

    public GotoStatement(Chapter chapter, int lineNumber, int indent, String targetLabel) {
        super(chapter, lineNumber, indent);
        this.targetLabel = targetLabel;
        LabelStatement target = chapter.getLabel(targetLabel);
        if(target != null)
            jumpTo = target;
    }

    @Override
    public Line execute() {
        if(condition == null || Type.isTruthy(condition.eval())) {
            if(jumpTo instanceof ProcedureLabelStatement)
                ((ProcedureLabelStatement)jumpTo).jumpedFrom = this;
            return jumpTo;
        } else
            return nextLine;
    }

    public boolean hasSetJump() {
        return jumpTo != null;
    }

    public void setJump(Chapter chapter) throws InterpretationException {
        jumpTo = chapter.getLabel(targetLabel);
        if(jumpTo == null) throw new InterpretationException("Invalid label for goto");
    }

    public String getTarget() {
        return targetLabel;
    }

    @Override
    protected StringBuilder generateStatement() {
        StringBuilder sb = new StringBuilder(targetLabel);
        if(condition != null) {
            sb.insert(0, "? ");
            sb.insert(0, condition.literal);
        }
        return sb;
    }
}
