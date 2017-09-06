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

import rs.lukaj.stories.Utils;
import rs.lukaj.stories.environment.DisplayProvider;
import rs.lukaj.stories.environment.FileProvider;
import rs.lukaj.stories.exceptions.ExecutionException;
import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.exceptions.LoadingException;
import rs.lukaj.stories.parser.types.Line;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Book {
    private static final String STATE_FILENAME = ".state";
    /**
     * 1-based index of currently executing chapter, updated on chapter end
     * (i.e. on the end of the first chapter it is set to 2)
     */
    protected static final String CURRENT_CHAPTER = "__chapter__";
    protected static final String CURRENT_LINE = "__line__";
    private static final String METADATA_FILENAME = ".info";

    private String name;
    private State info;

    private FileProvider files;
    private DisplayProvider display;
    private List<File> chapters = new ArrayList<>();
    private List<String> chapterNames = new ArrayList<>();
    private State state;
    private File stateFile;
    private Runtime runtime;

    public Book(String name, Runtime runtime, FileProvider files, DisplayProvider display) {
        this.name = name;
        this.runtime = runtime;
        this.files = files;
        this.display = display;
        File sourceDir = files.getSourceDirectory(name);
        File rootDir = files.getRootDirectory(name);
        if(!sourceDir.isDirectory()) throw new LoadingException("Cannot find book source directory at " + sourceDir.getAbsolutePath());
        String[] children = sourceDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name1) {
                return name1.endsWith(".ch") && Character.isDigit(name1.charAt(0));
            }
        });
        if(children == null || children.length == 0)
            throw new LoadingException("Cannot list() sources in source directory or there are no valid sources");
        stateFile = new File(rootDir, STATE_FILENAME);

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
        for(int i=0; i<children.length; i++) {
            chapters.add(new File(sourceDir, children[i]));
            chapterNames.add(getChapterName(children[i]));
        }
    }

    /**
     * Attempts to resume book from last known chapter.
     * If there is no such chapter (i.e. the book has never been played),
     * starts book from the beginning.
     */
    protected Line resume() throws InterpretationException {
        return startFrom(state.getOrDefault(CURRENT_CHAPTER, 1).intValue());
    }
    protected Line restart() throws InterpretationException {
        state = new State();
        return startFrom(1);
    }
    protected Line start(int chapterNo) throws InterpretationException {
        state.setVariable(CURRENT_CHAPTER, chapterNo);
        state.setVariable(CURRENT_LINE, 0);
        return startFrom(chapterNo);
    }

    protected void endChapter() throws InterpretationException {
        int current = state.getOrDefault(CURRENT_CHAPTER, 1).intValue();
        try {
            state.setVariable(CURRENT_CHAPTER, current + 1);
            state.setVariable(CURRENT_LINE, 0);
            state.saveToFile(stateFile);
            display.onChapterEnd(current, chapterNames.get(current-1));
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
     * @throws InterpretationException if chapter fails to be parsed
     */
    private Line startFrom(int chapterNo) throws InterpretationException {
        chapterNo--;
        if(chapters.size() <= chapterNo) return null;

        File source = chapters.get(chapterNo);
        String chapterName = chapterNames.get(chapterNo);
        try {
            Chapter chapter = new Chapter(chapterName, this, state, display, source);
            Line begin = chapter.compile();
            int initialNumber = begin.getLineNumber();
            int line = state.getOrDefault(CURRENT_LINE, 0).intValue();
            while(begin != null && begin.getLineNumber() < line) begin = begin.getNextLine();

            if(line <= initialNumber) {
                if(chapterNo == 0)
                    display.onBookBegin(name);
                display.onChapterBegin(chapterNo + 1, chapterName);
            }

            return begin;
        } catch (FileNotFoundException e) {
            throw new LoadingException("Unexpected: cannot find source", e);
        } catch (IOException e) {
            throw new LoadingException("Error loading source", e);
        }
    }

    private String getChapterName(String filename) {
        int start = filename.indexOf(' ');
        int end = filename.lastIndexOf('.');
        return filename.substring(start+1, end);
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

    public String getName() {
        return name;
    }

    public List<String> getChapterNames() {
        return chapterNames;
    }

    public State getState() {
        return state;
    }

    public void reloadBookInfo() {
        File infoFile = new File(files.getRootDirectory(name), METADATA_FILENAME);
        if(infoFile.isFile()) {
            try {
                info = new State(infoFile);
            } catch (IOException e) {
                throw new LoadingException("Failed to load metadata", e);
            }
        }
    }

    public State getBookInfo() {
        if(info == null) reloadBookInfo();
        return info;
    }

    public File getStateFile() {
        return new File(files.getRootDirectory(name), STATE_FILENAME);
    }

    public File getInfoFile() {
        return new File(files.getRootDirectory(name), METADATA_FILENAME);
    }
}
