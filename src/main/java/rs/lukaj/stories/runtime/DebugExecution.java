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

import rs.lukaj.stories.environment.BasicTerminalDisplay;
import rs.lukaj.stories.environment.LinuxDebugFiles;
import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.parser.lines.Line;

import java.util.ArrayList;
import java.util.List;

public class DebugExecution {
    public static void run(String name) {
        Runtime runtime = new Runtime(new LinuxDebugFiles(), new BasicTerminalDisplay());
        runtime.loadBook(name);
        try {
            System.out.println("\n\nRUNNING BOOK\n\n");
            runtime.executeInTightLoop(true, true);
            System.out.println("\n\nEND RUN\n\n");
        } catch (InterpretationException e) {
            e.printStackTrace();
        }
    }

    public static void testCodeGeneration(String bookName) {
        Runtime runtime = new Runtime(new LinuxDebugFiles(), new BasicTerminalDisplay());
        runtime.loadBook(bookName);
        Book book = runtime.getCurrentlyExecutingBook();
        try {
            Line start = book.restart();
            List<String> lines = new ArrayList<>();
            while(start != null) {
                lines.add(start.generateCode());
                start = start.getNextLine();
            }
            for(String line : lines)
                System.out.println(line);
        } catch (InterpretationException e) {
            e.printStackTrace();
        }
    }
}
