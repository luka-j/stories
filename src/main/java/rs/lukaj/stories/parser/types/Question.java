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

package rs.lukaj.stories.parser.types;

import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.runtime.Chapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luka on 3.6.17..
 */
public class Question extends Line {
    protected String variable;
    private String text;
    protected String character;
    private List<AnswerLike> answers = new ArrayList<>();
    private boolean containsPictures;
    private double time;

    public Question(Chapter chapter, String variable, String text, String character,
                    int lineNumber, int indent) throws InterpretationException {
        this(chapter, variable, text, character, 0, lineNumber, indent);
    }

    protected Question(Chapter chapter, String variable, String text, String character, double time,
                       int lineNumber, int indent) throws InterpretationException {
        super(chapter, lineNumber, indent);
        chapter.getState().declareVariable(variable);
        this.variable = variable;
        this.text = text;
        this.character = character;
        this.time = time;
    }

    @Override
    public Line execute() {
        String chosen = null;
        int chosenIndex;
        if(containsPictures)
            chosenIndex = displayPictureQuestion();
        else
            chosenIndex = displayQuestion();
        if(chosenIndex >= 0)
            chosen = answers.get(chosenIndex).getVariable();
        try {
            if(chosen != null)
                chapter.getState().setVariable(variable, chosen);
            for(int i=0; i<answers.size(); i++)
                chapter.getState().setVariable(answers.get(i).getVariable(), i == chosenIndex);
        } catch (InterpretationException ex) {
            throw new RuntimeException("Caught unhandled InterpretationException in Quesiton#execute!", ex);
        }
        return nextLine;
    }

    private int displayQuestion() {
        String[] answers = new String[this.answers.size()];
        for(int i = 0; i< this.answers.size(); i++) answers[i] = this.answers.get(i).getContent(chapter.getState()).toString();
        return chapter.getDisplay().showQuestion(text, character, getAvatar(character), time, answers);
    }

    private int displayPictureQuestion() {
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
