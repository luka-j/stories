package rs.luka.stories.runtime;

import rs.luka.stories.environment.BasicTerminalDisplay;
import rs.luka.stories.environment.LinuxDebugFiles;
import rs.luka.stories.exceptions.InterpretationException;

public class DebugExecution {
    public static void run() {
        Runtime runtime = new Runtime(new LinuxDebugFiles(), new BasicTerminalDisplay());
        runtime.loadBook("sample");
        try {
            System.out.println("\n\nRUNNING BOOK\n\n");
            runtime.restartBook();
            do {
                while (runtime.next()) ;
                runtime.endChapter();
            } while (runtime.nextChapter());
            System.out.println("\n\nEND RUN\n\n");
        } catch (InterpretationException e) {
            e.printStackTrace();
        }
    }
}
