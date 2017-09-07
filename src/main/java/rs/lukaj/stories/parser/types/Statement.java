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
public abstract class Statement extends Line {

    protected Statement(Chapter chapter, int lineNumber, int indent) {
        super(chapter, lineNumber, indent);
    }

    public static Statement create(Chapter chapter, String statement, int lineNumber, int indent)
            throws InterpretationException {
        if(statement.endsWith("?"))
            return new IfStatement(chapter, statement, lineNumber, indent);
        if(statement.endsWith(":")) {
            LabelStatement stmt;
            if(statement.startsWith(":")) {
                stmt = new ProcedureLabelStatement(chapter, statement, lineNumber, indent);
                chapter.stashProcedureLabel((ProcedureLabelStatement)stmt);
            }
            else
                stmt= new LabelStatement(chapter, statement, lineNumber, indent);
            chapter.addLabel(stmt.getLabel(), stmt);
            return stmt;
        }
        if(statement.equals(">>"))
            return new ReturnStatement(chapter, lineNumber, indent);
        if(statement.startsWith(">"))
            return new GotoStatement(chapter, statement, lineNumber, indent);
        else
            return new AssignStatement(chapter, statement, lineNumber, indent);
    }
}
