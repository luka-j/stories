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

import rs.lukaj.stories.Utils;
import rs.lukaj.stories.parser.LineType;
import rs.lukaj.stories.runtime.Chapter;

public class EndChapter extends Line {
    public static final LineType LINE_TYPE = LineType.END_CHAPTER;

    public EndChapter(Chapter chapter, int lineNumber, int indent) {
        super(chapter, lineNumber, indent);
    }

    @Override
    public Line execute() {
        chapter.getDisplay().signalEndChapter();
        return null; //returning null signals end of the chapter
    }

    @Override
    public String generateCode() {
        return Utils.generateIndent(getIndent()) + LINE_TYPE.makeLine();
    }
}
