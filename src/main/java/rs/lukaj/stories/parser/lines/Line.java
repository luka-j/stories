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

package rs.lukaj.stories.parser.lines;

import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.runtime.Chapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luka on 3.6.17.
 */
public abstract class Line {
    protected Line nextLine;
    protected final Chapter chapter;

    private int indent;
    private final int lineNumber;
    private final List<Directive> directives = new ArrayList<>();

    public abstract Line execute();
    public abstract String generateCode();


    public Line(Chapter chapter, int lineNumber, int indent) {
        this.chapter = chapter;
        this.indent = indent;
        this.lineNumber = lineNumber;
    }

    protected File getAvatar(String character) {
        return chapter.getFileProvider().getAvatar(chapter.getBook().getName(), chapter.getState().getString(character));
    }

    public void setNextLine(Line nextLine) throws InterpretationException {
        if(nextLine == this) throw new InterpretationException("Attempting to set nextLine to this, will cause infinite loop");
        this.nextLine = nextLine;
    }

    public Line getNextLine() {
        return nextLine;
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void addDirectives(List<Directive> directives) {
        this.directives.addAll(directives);
    }

    public List<Directive> getDirectives() {
        return directives;
    }
}
