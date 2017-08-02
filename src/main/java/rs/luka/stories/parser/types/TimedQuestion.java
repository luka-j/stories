package rs.luka.stories.parser.types;

import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.runtime.Chapter;

/**
 * Created by luka on 4.6.17..
 */
public class TimedQuestion extends Question {
    public TimedQuestion(Chapter chapter, String variable, String text, String character, double time, int indent) throws InterpretationException {
        super(chapter, variable, text, character, time, indent);
    }
}
