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

import rs.luka.stories.exceptions.ExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BasicTerminalDisplay implements DisplayProvider {
    @Override
    public void showNarrative(String text) {
        System.out.print(text);
        System.out.flush();
        new Scanner(System.in).nextLine();
    }

    @Override
    public void showSpeech(String character, File avatar, String text) {
        System.out.print(character + ": " + text);
        System.out.flush();
        new Scanner(System.in).nextLine();
    }

    @Override
    public int showQuestion(String question, String character, File avatar, double time, String... answers) {
        System.out.println(question);
        for(int i=0; i<answers.length; i++) {
            System.out.println("(" + (i+1) + ") " + answers[i]);
        }
        System.out.print("Enter number next to your choice: ");

        Scanner in = new Scanner(System.in);
        long startTime = System.currentTimeMillis();
        long allowedTime = (int)(time*1000);
        try {
            while(System.in.available() == 0) {
                if(allowedTime > 0 && System.currentTimeMillis() - startTime > allowedTime)
                    return -1;
                try {
                    Thread.sleep(allowedTime/1000 + 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                return in.nextInt()-1;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input");
                if(time > 0) return -1;
                else return showQuestion(question, character, avatar, time, answers);
            }
        } catch (IOException e) {
            throw new ExecutionException("IOException in BasicTerminalDisplay#showQuestion while reading from stdin");
        }
    }

    @Override
    public int showPictureQuestion(String question, String character, File avatar, double time, File... answers) {
        String[] stringAnswers = new String[answers.length];
        for(int i=0; i<answers.length; i++)
            stringAnswers[i] = answers[i].getName();
        return showQuestion(question, character, avatar, time, stringAnswers);
    }

    @Override
    public String showInput(String hint) {
        if(hint != null && !hint.trim().isEmpty()) {
            System.out.print(hint + ": ");
        }
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    @Override
    public void onChapterBegin(int chapterNo, String chapterName) {
        System.out.println("\nChapter " + chapterNo + ": " + chapterName);
    }

    @Override
    public void onChapterEnd(int chapterNo, String chapterName) {
        System.out.println("Finished chapter " + chapterNo + ": " + chapterName);
    }

    @Override
    public void onBookBegin(String bookName) {
        System.out.println("Book " + bookName);
    }

    @Override
    public void onBookEnd(String bookName) {
        System.out.println("The end of book " + bookName);
    }
}
