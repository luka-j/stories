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
import rs.lukaj.stories.parser.lines.Line;

import java.io.IOException;

/**
 * Created by luka on 4.6.17..
 */
public class Runtime {
    public static final double VERSION = 0.1;

    private FileProvider files;
    private DisplayProvider display;

    private Book book;
    private Line current;

    public Runtime(FileProvider files, DisplayProvider display) {
        this.files = files;
        this.display = display;
    }

    public Book loadBook(String name) throws LoadingException {
        book = new Book(name, files, display);
        return book;
    }

    public void restartBook() throws InterpretationException, LoadingException {
        current = book.restart();
    }

    /**
     * Starts book from the given chapter, overwriting any previous saves.
     * This starts the chapter from the beginning.
     * @param chapterNo 1-based chapter index
     */
    public void startChapter(int chapterNo) throws InterpretationException {
        current = book.start(chapterNo);
    }

    /**
     * Advances the chapter by one line
     * @return true if there are more lines, false if it's the end of the chapter
     *      Calling this method after it has returned false will yield a NPE
     * @throws ExecutionException if any exception occurs during execution
     */
    public boolean next() throws ExecutionException {
        try {
            current = current.execute();
            if(current != null)
                book.getState().setVariable(Book.CURRENT_LINE, current.getLineNumber());
            return current != null;
        } catch (ExecutionException e) {
            throw e;
        } catch (InterpretationException|RuntimeException e) {
            throw new ExecutionException("Unknown execution exception", e);
        }
    }

    /**
     * Advances the book by one chapter, if previous chapter has been completed
     * @return true if next chapter exists, false if it doesn't
     *      Calling this or {@link #next()} method after this method returns false
     *      will yield a NPE
     * @throws InterpretationException if any exception occurs during interpretation of the code
     */
    public boolean nextChapter() throws InterpretationException, LoadingException {
        current = book.resume();
        return current != null;
    }

    /**
     * Alias for {@link #nextChapter()}. Resumes book according to previous state.
     * @return whether the end of the book has been reached
     * @throws InterpretationException if any exception occurs during interpretation of the code
     * @throws LoadingException if any exception occurs during loading of the book
     */
    public boolean resumeBook() throws InterpretationException, LoadingException {
        return nextChapter();
    }

    public void endChapter() throws InterpretationException {
        book.endChapter();
    }

    public Book getCurrentlyExecutingBook() {
        return book;
    }

    /**
     * This provides a way of executing the whole book in a tight loop (i.e. it advances
     * statements and chapters until it reaches the end). It obviously takes a while
     * to execute.
     * @param restart whether the book should start from the beginning or from where the
     *                user left off
     * @param save whether state should be saved after every statement
     * @throws InterpretationException if any of the {@link Book} methods throw InterpretationException
     */
    public void executeInTightLoop(boolean restart, boolean save) throws InterpretationException {
        if(restart) restartBook();
        else if (!resumeBook()) return; //attempting to resume ended book
        do {
            while (next()) {
                if (save)
                    try {
                        save();
                    } catch (IOException e) {
                        throw new ExecutionException("I/O exception while saving state", e);
                    }
                if(Thread.currentThread().isInterrupted()) return;
            }
            endChapter();
        } while (nextChapter());
    }

    /**
     * Saves currently executing book's state to file. State is automatically saved
     * on the end of every chapter, and it can be triggered manually by calling
     * this method at any time.
     * @throws IOException in case IOException occurs during saving
     */
    public void save() throws IOException {
        book.getState().saveToFile(book.getStateFile());
    }
}
