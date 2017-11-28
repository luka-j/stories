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

package rs.lukaj.stories.environment;

import java.io.File;

public interface FileProvider {
    /**
     * Returns the image file denoted by this path. If no such image exists,
     * returns null.
     *
     * It is expected that most of the outputs of this method to be fed
     * back to the {@link DisplayProvider}, so it is left to the environment
     * which provides these interfaces to follow/break conventions at its
     * own discretion. It is important to note, however, that caller
     * shouldn't assume this method returns non-null values.
     *
     * @param path path to the image
     * @return File pointing to the image file
     */
    File getImage(String path);

    File getSourceDirectory(String path);

    File getSourceFile(String rootPath, String filePath);

    File getRootDirectory(String path);

    boolean imageExists(String path);

    default File getAvatar(String bookName, String path) {
        return getImage(bookName + File.separator + path);
    }
}
