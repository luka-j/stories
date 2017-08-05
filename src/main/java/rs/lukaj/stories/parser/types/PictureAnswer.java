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
import rs.lukaj.stories.runtime.State;

import java.io.File;

/**
 * Created by luka on 4.6.17..
 */
public class PictureAnswer implements AnswerLike {
    private String variable;
    private File picture;

    public PictureAnswer(String variable, File picture) throws InterpretationException {
        State.checkName(variable);
        this.variable = variable;
        this.picture = picture;
        if(!picture.isFile())
            throw new IllegalArgumentException("Picture " + picture.getAbsolutePath() + " doesn't exist");
    }

    public File getPicture() {
        return picture;
    }

    @Override
    public String getVariable() {
        return variable;
    }

    @Override
    public Object getContent(State state) {
        return getPicture();
    }
}
