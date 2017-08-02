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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Book {
    private static final String STATE_FILENAME = "state";
    private static final String CURRENT_CHAPTER = "__chapter__";

    private String name;
    private FileProvider files;
    private DisplayProvider display;
    private List<File> chapters = new ArrayList<>();
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
        if(stateFile.isFile()) {
            try {
                state = new State(stateFile);
            } catch (IOException e) {
                throw new LoadingException("Failed to load initial state", e);
            }
        }
        if(state == null) {
            state = new State();
        }
        for(int i=1; i<children.length; i++) //ignoring stateFile - it is at [0]
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
            display.onChapterEnd(current, getChapterName(current-1));
            if(current >= chapters.size())
                display.onBookEnd(name);
        } catch (InterpretationException e) {
            throw new ExecutionException("Unexpected: cannot set next chapter", e);
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
        if(chapters.size() <= chapterNo) return null;

        File source = chapters.get(chapterNo);
        String chapterName = getChapterName(chapterNo);
        try {
            Chapter chapter = new Chapter(chapterName, this, state, display, source);
            Line begin = chapter.compile();
            if(chapterNo == 0)
                display.onBookBegin(name);
            display.onChapterBegin(chapterNo+1, chapterName);

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
