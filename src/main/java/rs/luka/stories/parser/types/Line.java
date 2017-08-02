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

package rs.luka.stories.parser.types;

import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.runtime.Chapter;

import java.io.File;

/**
 * Created by luka on 3.6.17.
 */
public abstract class Line {
    protected Line nextLine;
    protected final Chapter chapter;

    private final int indent;

    public abstract Line execute();

    public int getIndent() {
        return indent;
    }

    public Line(Chapter chapter, int indent) {
        this.chapter = chapter;
        this.indent = indent;
    }

    protected File getAvatar(String character) {
        return chapter.getImage(chapter.getState().getString(character));
    }

    public void setNextLine(Line nextLine) throws InterpretationException {
        if(nextLine == this) throw new InterpretationException("Attempting to set nextLine to this, will cause infinite loop");
        this.nextLine = nextLine;
    }

    public Line getNextLine() {
        return nextLine;
    }
}
