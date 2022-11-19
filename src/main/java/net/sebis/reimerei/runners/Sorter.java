package net.sebis.reimerei.runners;

import net.sebis.reimerei.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Sorter extends Thread implements Runnable {

    private final int threadID;
    private final int threads;
    private final List<String> words;
    private final HashMap<String, List<String>> groups;

    public Sorter(int threadID, int threads, List<String> words, HashMap<String, List<String>> groups) {
        this.threadID = threadID;
        this.threads = threads;
        this.words = words;
        this.groups = groups;
    }

    @Override
    public void run() {
        float done = 0;
        for (float i = threadID-1; i < words.size(); i += threads) {
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
            Main.getInstance().getThreads().replace(threadID, (int) done); // set number of done operations
        }

        Main.getInstance().setFin(Main.getInstance().getFin() + 1);
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return null;
    }
}
