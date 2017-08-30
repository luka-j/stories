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

public class LinuxDebugFiles implements FileProvider {
    public static final File IMAGE_ROOT = new File("/data/Shared/Projects/Stories/images/");
    public static final File BOOKS_ROOT = new File("/data/Shared/Projects/Stories/books/");

    @Override
    public File getImage(String path) {
        return new File(IMAGE_ROOT, path);
    }

    @Override
    public File getSourceDirectory(String path) {
        return new File(BOOKS_ROOT, path);
    }

    @Override
    public File getRootDirectory(String path) {
        return getSourceDirectory(path);
    }

    @Override
    public boolean imageExists(String path) {
        return getImage(path).isFile();
    }
}
