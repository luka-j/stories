package rs.luka.stories.parser.types;

import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.runtime.Chapter;
import rs.luka.stories.runtime.State;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luka on 3.6.17..
 */
public class Question extends Line {
    protected String variable;
    protected String text;
    protected String character;
    protected List<AnswerLike> answers = new ArrayList<>();
    private boolean containsPictures;
    private double time;

    public Question(Chapter chapter, String variable, String text, String character, int indent) throws InterpretationException {
        this(chapter, variable, text, character, 0, indent);
    }

    protected Question(Chapter chapter, String variable, String text, String character, double time, int indent) throws InterpretationException {
        super(chapter, indent);
        State.checkName(variable);
        this.variable = variable;
        this.text = text;
        this.character = character;
        this.time = time;
    }

    @Override
    public Line execute() {
        String chosen; //todo what happens if time's up and user hasn't selected the answer, so index is -1 ?
        int chosenIndex;
        if(containsPictures)
            chosenIndex = displayPictureQuestion();
        else
            chosenIndex = displayQuestion();
        chosen = answers.get(chosenIndex).getVariable();
        try {
            chapter.getState().setVariable(variable, chosen);
            chapter.getState().setFlag(chosen);
            for(int i=0; i<answers.size(); i++)
                if(i != chosenIndex)
                    chapter.getState().setVariable(answers.get(i).getVariable(), false);
        } catch (InterpretationException ex) {
            throw new RuntimeException("Caught unhandled InterpretationException in Quesiton#execute!", ex);
        }
        return nextLine;
    }

    protected int displayQuestion() {
        String[] answers = new String[this.answers.size()];
        for(int i = 0; i< this.answers.size(); i++) answers[i] = this.answers.get(i).getContent(chapter.getState()).toString();
        return chapter.getDisplay().showQuestion(text, character, getAvatar(character), time, answers);
    }

    protected int displayPictureQuestion() {
        File[] answers = new File[this.answers.size()];
        for(int i = 0; i< this.answers.size(); i++) answers[i] = ((PictureAnswer) this.answers.get(i)).getPicture();
        return chapter.getDisplay().showPictureQuestion(text, character, getAvatar(character), time, answers);
    }

    public void addAnswer(Answer answer) throws InterpretationException {
        if(!answers.isEmpty() && containsPictures) throw new InterpretationException("Wrong answer type, expected PictureAnswer");
        answers.add(answer);
    }

    public void addPictureAnswer(PictureAnswer answer) throws InterpretationException {
        if(answers.isEmpty()) containsPictures = true;
        if(!containsPictures) throw new InterpretationException("Wrong answer type, expected textual Answer");
        answers.add(answer);
    }

    public String getVariable() {
        return variable;
    }
}
