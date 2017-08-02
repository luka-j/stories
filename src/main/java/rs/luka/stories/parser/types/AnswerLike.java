package rs.luka.stories.parser.types;

import rs.luka.stories.runtime.State;

public interface AnswerLike {
    String getVariable();
    Object getContent(State state);
}
