package rs.luka.stories.environment;

import rs.luka.stories.exceptions.ExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BasicTerminalDisplay implements DisplayProvider {
    @Override
    public void showNarrative(String text) {
        System.out.println(text);
    }

    @Override
    public void showSpeech(String character, File avatar, String text) {
        System.out.println(character + ": " + text);
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
                return in.nextInt();
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
}
