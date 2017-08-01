package rs.luka.stories.runtime;

import rs.luka.stories.Utils;
import rs.luka.stories.environment.DisplayProvider;
import rs.luka.stories.environment.FileProvider;
import rs.luka.stories.exceptions.ExecutionException;
import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.exceptions.LoadingException;
import rs.luka.stories.parser.types.Line;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Book {
    private static final String STATE_FILENAME = "state";
    private static final String CURRENT_CHAPTER = "__chapter__";

    private String name;
    private FileProvider files;
    private DisplayProvider display;
    private List<File> chapters;
    private State state;
    private File stateFile;

    public Book(String name, FileProvider files, DisplayProvider display) {
        this.name = name;
        this.files = files;
        this.display = display;
        File sourceDir = files.getSourceDirectory(name);
        if(!sourceDir.isDirectory()) throw new LoadingException("Cannot find book source directory at " + sourceDir.getAbsolutePath());
        String[] children = sourceDir.list();
        if(children == null || children.length == 0)
            throw new LoadingException("Cannot list() sources in source directory");
        stateFile = new File(sourceDir, STATE_FILENAME);

        Arrays.sort(children, Utils.enumeratedStringsComparator);
        int last = children.length-1;
        if(stateFile.isFile()) {
            try {
                state = new State(new File(sourceDir, children[last]));
            } catch (IOException e) {
                throw new LoadingException("Failed to load initial state", e);
            }
        }
        if(state == null)
            state = new State();
        for(int i=0; i<last; i++) //ignoring stateFile - chapter sources will come first
            chapters.add(new File(sourceDir, children[i]));
    }

    /**
     * Attempts to resume book from last known chapter.
     * If there is no such chapter (i.e. the book has never been played),
     * starts book from the beginning.
     */
    protected Line resumeBook() throws InterpretationException {
        return startFrom(state.getOrDefault(CURRENT_CHAPTER, 1).intValue()); //this is legit the dumbest cast ever
    }
    protected Line restartBook() throws InterpretationException {
        state = new State();
        return startFrom(1);
    }

    protected void endChapter() throws InterpretationException {
        int current = state.getOrDefault(CURRENT_CHAPTER, 1).intValue();
        try {
            state.setVariable(CURRENT_CHAPTER, current + 1);
            state.saveToFile(stateFile);
            display.onChapterEnd(current, getChapterName(current));
            if(current > chapters.size())
                display.onBookEnd(name);
        } catch (InterpretationException e) {
            throw new ExecutionException("Unexpected: cannot set next chapter");
        } catch (IOException e) {
            throw new LoadingException("Cannot save stateFile after finishing chapter");
        }
    }

    /**
     * Start book from a certain chapter
     * @param chapterNo chapter, 1-based
     * @return First line of the chapter
     * @throws InterpretationException
     */
    private Line startFrom(int chapterNo) throws InterpretationException {
        chapterNo--;
        if(chapters.size() >= chapterNo) return null;

        File source = chapters.get(chapterNo);
        String chapterName = getChapterName(chapterNo);
        try {
            Chapter chapter = new Chapter(chapterName, this, state, display, source);
            Line begin = chapter.compile();
            if(chapterNo == 1)
                display.onBookBegin(name);
            display.onChapterBegin(chapterNo, chapterName);

            return begin;
        } catch (FileNotFoundException e) {
            throw new LoadingException("Unexpected: cannot find source", e);
        } catch (IOException e) {
            throw new LoadingException("Error loading source", e);
        }
    }

    private String getChapterName(int chapterNo) {
        return Utils.between(chapters.get(chapterNo).getName(), ' ', '.');
    }

    public File getImage(String path) {
        return files.getImage(generateImageFile(path));
    }

    public boolean imageExists(String path) {
        return files.imageExists(generateImageFile(path));
    }

    private String generateImageFile(String path) {
        return name + File.separator + path;
    }
}
