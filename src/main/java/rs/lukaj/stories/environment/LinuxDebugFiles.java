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
    public static final String IMAGES_FOLDER = "images";
    public static final File BOOKS_ROOT = new File("/data/Shared/Projects/Stories/books/");

    @Override
    public File getImage(String bookName, String path) {
        return new File(BOOKS_ROOT, bookName + File.separator + IMAGES_FOLDER + File.separator + path);
    }

    @Override
    public File getSourceDirectory(String path) {
        return new File(BOOKS_ROOT, path);
    }

    @Override
    public File getSourceFile(String rootPath, String filePath) {
        return new File(getSourceDirectory(rootPath), filePath);
    }

    @Override
    public File getRootDirectory(String path) {
        return getSourceDirectory(path);
    }
}
