package rs.luka.stories.parser.types;

import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.runtime.Chapter;

import java.io.File;

/**
 * Created by luka on 4.6.17..
 */
public class TimedQuestion extends Question {
    protected double time;

    public TimedQuestion(Chapter chapter, String variable, String text, String character, double time, int indent) throws InterpretationException {
        super(chapter, variable, text, character, indent);
        this.time = time;
    }

    @Override
    protected int displayQuestion() {
        String[] answers = new String[this.answers.size()];
        for(int i = 0; i< this.answers.size(); i++) answers[i] = this.answers.get(i).getVariable();
        return chapter.getDisplay().showQuestion(text, character, getAvatar(character), time, answers);
    }

    protected int displayPictureQuestion() {
        File[] answers = new File[this.answers.size()];
        for(int i = 0; i< this.answers.size(); i++) answers[i] = ((PictureAnswer) this.answers.get(i)).getPicture();
        return chapter.getDisplay().showPictureQuestion(text, character, getAvatar(character), time, answers);
    }
}
