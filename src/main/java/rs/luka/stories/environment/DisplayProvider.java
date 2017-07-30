package rs.luka.stories.environment;

import java.io.File;

public interface DisplayProvider {
    /**
     *
     * @param text text of the narrative
     */
    void showNarrative(String text);

    /**
     *
     * @param character character speaking
     * @param avatar avatar of the character (can be null if there is none)
     * @param text text of the speech
     */
    void showSpeech(String character, File avatar, String text);

    /**
     *
     * @param question question to be displayed
     * @param character character asking the question (can be null if it is posed by narrator)
     * @param avatar avatar of the character asking (can be null if there is none)
     * @param time time allowed for answering the question in seconds, 0 for unlimited
     * @param answers possible answers to the question
     * @return index (0-based) of the selected answer, or -1 if no answer was selected
     */
    int showQuestion(String question, String character, File avatar, double time, String... answers);

    /**
     *
     * @param question question to be displayed
     * @param character character asking the question (can be null if it is posed by narrator)
     * @param avatar avatar of the character asking (can be null if there is none)
     * @param time time allowed for answering the question in seconds, 0 for unlimited
     * @param answers possible answers to the question, as image files
     * @return index (0-based) of the selected answer, or -1 if no answer was selected
     */
    int showPictureQuestion(String question, String character, File avatar, double time, File... answers);

    /**
     * Asks user for input and returns the result
     * @param hint hint to be displayed to the user, if adapter supports it
     * @return entered text
     */
    String showInput(String hint);
}
