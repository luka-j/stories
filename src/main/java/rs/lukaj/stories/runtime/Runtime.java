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

package rs.lukaj.stories.runtime;

import rs.lukaj.stories.environment.DisplayProvider;
import rs.lukaj.stories.environment.FileProvider;
import rs.lukaj.stories.exceptions.ExecutionException;
import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.exceptions.LoadingException;
import rs.lukaj.stories.parser.types.Line;

/**
 * Created by luka on 4.6.17..
 */
public class Runtime {
    private FileProvider files;
    private DisplayProvider display;

    private Book book;
    private Line current;

    public Runtime(FileProvider files, DisplayProvider display) {
        this.files = files;
        this.display = display;
    }

    public Book loadBook(String name) {
        book = new Book(name, files, display);
        return book;
    }

    public void restartBook() throws InterpretationException, LoadingException {
        current = book.restartBook();
    }

    /**
     * Advances the chapter by one line
     * @return true if there are more lines, false if it's the end of the chapter
     *      Calling this method after it has returned false will yield a NPE
     * @throws ExecutionException if any exception occurs during execution
     */
    public boolean next() throws ExecutionException {
        current = current.execute();
        return current != null;
    }

    /**
     * Advances the book by one chapter
     * @return true if next chapter exists, false if it doesn't
     *      Calling this or {@link #next()} method after this method returns false
     *      will yield a NPE
     * @throws InterpretationException if any exception occurs during interpretation of the code
     */
    public boolean nextChapter() throws InterpretationException, LoadingException {
        current = book.resumeBook();
        return current != null;
    }

    public void endChapter() throws InterpretationException {
        book.endChapter();
    }

    public Book getCurrentlyExecutingBook() {
        return book;
    }
}
