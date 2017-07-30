package rs.luka.stories.parser;

import rs.luka.stories.Utils;
import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.parser.types.*;
import rs.luka.stories.runtime.Chapter;
import rs.luka.stories.runtime.State;

import java.util.List;

import static rs.luka.stories.parser.Parser.LineType.*;

public class Parser {

    public enum LineType {
        ANSWER {
            @Override
            public Line parse(String line, Chapter chapter, Object... additionalParams) throws InterpretationException {
                Question question = (Question)additionalParams[0];
                if(question == null) throw new InterpretationException("Creating answer without the question");
                String[] tokens = line.trim().substring(1).split("\\s*:\\s*", 2);
                if(chapter.imageExists(tokens[1])) {
                    PictureAnswer ans = new PictureAnswer(tokens[0], chapter.getImage(tokens[1]));
                    question.addPictureAnswer(ans);
                } else {
                    Answer ans = new Answer(tokens[0], tokens[1]);
                    question.addAnswer(ans);
                }
                //we're being quite strict here: disallowing mixing of image and String, even if
                //String only looks like an image. An alternative would be falling back to
                //String in case Question detects mixing, though I'm afraid that'd make things
                //quite complicated and unpredictable todo reconsider
                return question;
            }
        },
        STATEMENT {
            @Override
            public Line parse(String line, Chapter chapter, Object... additionalParams) throws InterpretationException {
                return Statement.create(chapter, line.trim(), Utils.countLeadingSpaces(line));
            }
        },
        NARRATIVE {
            @Override
            public Line parse(String line, Chapter chapter, Object... additionalParams) throws InterpretationException {
                return new Narrative(chapter, line.trim(), Utils.countLeadingSpaces(line));
            }
        },
        QUESTION {
            @Override
            public Line parse(String line, Chapter chapter, Object... additionalParams) throws InterpretationException {
                String[] parts = line.trim().substring(1).split("\\s*:\\s*", 2);
                String text = parts[1];
                String var = Utils.between(parts[0], '[', ']');
                String time = Utils.between(parts[0], '(', ')');
                String charName = Utils.between(line, ']', ':');
                if(time != null) time = time.trim();
                if(var == null) throw new InterpretationException("Variable name for question is empty");
                charName = charName.trim(); //this shouldn't be null (both ] and : must exist, otherwise NPE would be thrown earlier)
                var = var.trim();
                int indent = Utils.countLeadingSpaces(line);

                if(time == null) {
                    return new Question(chapter, var, text, charName, indent);
                } else {
                    double multiplicator=1;
                    if(time.endsWith("ms")) {
                        multiplicator = 1./1000;
                        time = time.substring(0, time.length()-2);
                    } else if(time.endsWith("s")) {
                        multiplicator = 1;
                        time = time.substring(0, time.length()-1);
                    } else if(time.endsWith("m")) {
                        multiplicator = 60;
                        time = time.substring(0, time.length()-1);
                    }
                    double seconds = Double.parseDouble(time) * multiplicator;
                    return new TimedQuestion(chapter, var, text, charName, seconds, indent);
                }
            }
        },
        SPEECH {
            @Override
            public Line parse(String line, Chapter chapter, Object... additionalParams) throws InterpretationException {
                String[] parts = line.trim().split("\\s*:\\s*", 2);
                return new Speech(chapter, parts[0], parts[1], Utils.countLeadingSpaces(line));
            }
        },
        INPUT {
            @Override
            public Line parse(String line, Chapter chapter, Object... additionalParams) throws InterpretationException {
                String[] parts = line.trim().split("\\s*]\\s*", 2);
                return new TextInput(chapter, parts[0].substring(1), parts[2], Utils.countLeadingSpaces(line));
            }
        };

        public abstract Line parse(String line, Chapter chapter, Object... additionalParams) throws InterpretationException;
    }

    private Question previousQuestion;
    private Line previousLine;
    private Line head;
    private boolean finished = false;

    public LineType getType(String line, State state) throws InterpretationException {
        line = line.trim();
        if(line.isEmpty()) throw new InterpretationException("Empty line");
        if(line.startsWith(":"))
            return STATEMENT;
        if(line.startsWith("?"))
            return QUESTION;
        if(line.startsWith("*"))
            return ANSWER;
        if(line.startsWith("[") && line.contains("]"))
            return INPUT;
        if(line.contains(":") && state.getString(line.split(":", 2)[0]) != null)
            return SPEECH;
        return NARRATIVE;
    }

    public Line parse(List<String> lines, Chapter chapter) throws InterpretationException {
        for(String line : lines) parse(line, chapter);
        setJumps(chapter);
        return getHead();
    }

    private Line parse(String line, Chapter chapter) throws InterpretationException {
        LineType type = getType(line, chapter.getState());
        Line current;
        if(type == ANSWER) {
            current = type.parse(line, chapter, previousQuestion);
        } else {
            current = type.parse(line, chapter);
        }
        if(head == null) head = current;
        if(previousLine != null && previousLine != current) previousLine.setNextLine(current);
        //previousLine == current iff current instanceof Question && type == ANSWER, because ANSWER isn't a Line
        //todo improve parsing (move Question#nextLine setting to jumps)
        previousLine = current;
        if(current instanceof Question) previousQuestion = (Question) current;
        return current;
    }

    public Line getHead() {
        if(!finished) System.err.println("warning: requested head before parsing is finished");
        return head;
    }

    private void setJumps(Chapter chapter) throws InterpretationException {
        Line curr = head;
        int indent = 0;
        boolean inside = false;
        IfStatement conditional = null;
        while(curr != null) {
            if(curr instanceof GotoStatement) {
                ((GotoStatement)curr).setJump(chapter);
            }

            if(!inside && curr instanceof IfStatement) {
                inside = true;
                conditional = (IfStatement)curr;
                indent = conditional.getIndent();
            } else if(inside) {
                if(curr.getIndent() == indent) {
                    conditional.setNextIfFalse(curr);
                    inside = false;
                } else if(curr.getIndent() < indent) {
                    throw new InterpretationException("Bad indent inside if-statement body (:?)");
                }
            }

            curr = curr.getNextLine();
        }
        finished = true;
    }
}
