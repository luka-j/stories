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

    void onChapterBegin(int chapterNo, String chapterName);

    void onChapterEnd(int chapterNo, String chapterName);

    void onBookBegin(String bookName);

    void onBookEnd(String bookName);
}
