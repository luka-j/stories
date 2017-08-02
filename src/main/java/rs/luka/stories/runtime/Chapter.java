/*
 Stories - an interactive storytelling language
 Copyright (C) 2017 Luka Jovičić

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published
 by the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package rs.luka.stories.runtime;

import rs.luka.stories.environment.DisplayProvider;
import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.parser.Parser;
import rs.luka.stories.parser.types.Line;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Chapter {
    private Book book;
    private String name;
    private State state;
    private DisplayProvider display;
    private File source;

    private Map<String, Line> labels = new HashMap<>();

    public Chapter(String name, Book book, State initialState, DisplayProvider display, File source) throws FileNotFoundException {
        if(!source.isFile()) throw new FileNotFoundException("Source doesn't exist: " + source.getAbsolutePath());
        this.book = book;
        this.name = name;
        this.state = initialState;
        this.display = display;
        this.source = source;
    }

    public Line compile() throws IOException, InterpretationException {
        List<String> lines = Files.readAllLines(source.toPath());
        lines = lines.stream().filter(s->!s.trim().isEmpty()).collect(Collectors.toList());
        if(lines.isEmpty()) throw new InterpretationException("File is empty!");

        Parser parser = new Parser();
        return parser.parse(lines, this);
    }

    public State getState() {
        return state;
    }

    public DisplayProvider getDisplay() {
        return display;
    }

    public File getImage(String filePath) {
        return book.getImage(filePath);
    }
    public boolean imageExists(String filePath) {
        return book.imageExists(filePath);
    }

    public void addLabel(String label, Line line) {
        labels.put(label, line);
    }
    public Line getLabel(String label) {
        return labels.get(label);
    }
}
