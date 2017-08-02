package rs.luka.stories.runtime;

import rs.luka.stories.environment.DisplayProvider;
import rs.luka.stories.environment.FileProvider;
import rs.luka.stories.exceptions.ExecutionException;
import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.exceptions.LoadingException;
import rs.luka.stories.parser.types.Line;

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

    public void loadBook(String name) {
        book = new Book(name, files, display);
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
}
