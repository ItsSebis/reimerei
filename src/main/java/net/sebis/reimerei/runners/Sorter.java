package net.sebis.reimerei.runners;

import net.sebis.reimerei.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record Sorter(int threadID, List<String> words, HashMap<String, List<String>> groups) implements Runnable {

    @Override
    public void run() {
        float done = 0;
        for (float i = 0; i < words.size(); i += threadID) {
            String word = words.get((int) i); // get current word from words list
            String group = Main.getInstance().getRhymeGroup(word); // get rhyme group of current word
            List<String> rhymes = new ArrayList<>(); // create list to store all possible rhymes
            for (String testW : words) { // test every other word if it rhymes with current word
                if (Main.getInstance().getRhymeGroup(testW).equalsIgnoreCase(group) && !testW.toLowerCase().endsWith(word.toLowerCase()) &&
                        !word.toLowerCase().endsWith(testW.toLowerCase())) {
                    /* add testing word to list if:
                     *      the test word is has the same rhyme group
                     *      the current word does not end with the testing word or vise versa
                     */
                    rhymes.add(testW);
                }
            }
            if (rhymes.size() > 0) {
                groups.put(word, rhymes);
            }
            done++;
            float percent = done / words.size() * 100;
            System.out.print("|" + "=".repeat(Math.round(percent)) + " ".repeat(100 - Math.round(percent)) + "| " + Main.getInstance().getDf().format(percent) + "% " + done + "\r");
        }

        Main.getInstance().setFin(Main.getInstance().getFin() + 1);
    }
}
