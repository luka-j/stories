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

import rs.luka.stories.parser.Expressions;
import rs.luka.stories.runtime.Chapter;

/**
 * Created by luka on 3.6.17..
 */
public class Narrative extends Line {
    protected String text;

    public Narrative(Chapter chapter, String text, int indent) {
        super(chapter, indent);
        this.text = text;
    }

    @Override
    public Line execute() {
        text = Expressions.substituteVariables(text, chapter.getState());
        chapter.getDisplay().showNarrative(text);
        return nextLine;
    }
}
