package net.sebis.reimerei;

import net.sebis.reimerei.runners.Sorter;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

public class Main {

    private static Main instance;

    private final List<Thread> threads = new ArrayList<>();
    private int fin = 0;

    private final List<String> vocals = new ArrayList<>();
    private final URL Examples0 = new URL("https://bwinf.de/fileadmin/bundeswettbewerb/41/reimerei0.txt");
    private final URL Examples1 = new URL("https://bwinf.de/fileadmin/bundeswettbewerb/41/reimerei1.txt");
    private final URL Examples2 = new URL("https://bwinf.de/fileadmin/bundeswettbewerb/41/reimerei2.txt");
    private final URL Examples3 = new URL("https://bwinf.de/fileadmin/bundeswettbewerb/41/reimerei3.txt");
    //private final URL AllGermanWords = new URL("https://gist.githubusercontent.com/MarvinJWendt/2f4f4154b8ae218600eb091a5706b5f4/raw/36b70dd6be330aa61cd4d4cdfda6234dcb0b8784/wordlist-german.txt");
    private final HashMap<String, URL> urls = new HashMap<>();

    private final List<String> words = new ArrayList<>();
    private final HashMap<String, List<String>> groups = new HashMap<>();

    private final Scanner s = new Scanner(System.in);

    private final DecimalFormat df = new DecimalFormat("##.##");

    public static void main(String[] args) throws IOException, InterruptedException {
        new Main();
    }

    public Main() throws IOException, InterruptedException {
        instance = this;

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        String[] vocals = new String[]{"a", "e", "i", "o", "u", "ä", "ö", "ü", "y"};
        this.vocals.addAll(Arrays.stream(vocals).toList());

        collectURLMap();
        readWords();
        sort();
        output();
    }

    public void collectURLMap() {
        // Put all examples in the previously created HashMap
        urls.put("Examples0.txt", getExamples0());
        urls.put("Examples1.txt", getExamples1());
        urls.put("Examples2.txt", getExamples2());
        urls.put("Examples3.txt", getExamples3());
    }

    public void readWords() throws IOException {
        // Select word list
        System.out.println("Select word pallet: ");
        int n = 0;
        for (String example : urls.keySet().stream().toList()) {
            System.out.println("   " + n + ": " + example);
            n++;
        }
        System.out.println("   " + n + ": Custom URL to text file");
        System.out.print("> ");
        String input = s.nextLine();
        while (input.isBlank() || (!input.equals("0") && !input.equals("1") && !input.equals("2")) && !input.equals("3") && !input.equals("4")) {
            System.out.print("> ");
            input = s.nextLine();
        }
        int c = Integer.parseInt(input);
        URL url;
        if (c < urls.values().stream().toList().size()) {
            url = urls.values().stream().toList().get(c);
        } else {
            System.out.println("Give a custom .txt URL");
            System.out.print("> ");
            input = s.nextLine();
            while (input.isBlank() || !input.endsWith(".txt")) {
                System.out.print("> ");
                input = s.nextLine();
            }
            url = new URL(input);
        }

        // Read URL line by line with Scanner
        System.out.println("Downloading...");
        Scanner s = new Scanner(url.openStream());
        while (s.hasNextLine()) {
            String word = s.nextLine();
            if (!getRhymeGroup(word).equals("{N/A}")) {
                words.add(word);
            }
            System.out.print("D: " + words.size() + "\r");
        }
        System.out.println("Done - " + words.size());
    }

    public void sort() throws InterruptedException {
        System.out.println("Sorting words in the table...");

        // Start sorting thread
        Thread thread = new Thread(new Sorter(1, words, groups));
        threads.add(thread);
        thread.start();
        while (fin < threads.size()) {
            Thread.sleep(10);
        }
        System.out.println("Done");
    }

    public void output() {

        // Select output format
        System.out.println("Select output type: ");
        System.out.println("   1: Console");
        System.out.println("   2: table.md (recommended)");
        System.out.print("> ");
        String input = s.nextLine();
        while (input.isBlank() || (!input.equals("1") && !input.equals("2"))) {
            System.out.print("> ");
            input = s.nextLine();
        }

        // Generating Table in Github markdown format
        System.out.println("Generating output table...");

        StringBuilder table = new StringBuilder("| ");

        float i = 0;
        for (String group : groups.keySet()) {
            table.append(group).append(" | ");
            i++;
            float percent = i/groups.keySet().size()*100;
            System.out.print("|" + "=".repeat(Math.round(percent)) + " ".repeat(100-Math.round(percent)) + "| " + df.format(percent) + "%\r");
        }
        i = 0;
        table.append("\n| ");
        for (String group : groups.keySet()) {
            table.append("-".repeat(group.length())).append(" | ");
            i++;
            float percent = i/groups.keySet().size()*100;
            System.out.print("|" + "=".repeat(Math.round(percent)) + " ".repeat(100-Math.round(percent)) + "| " + df.format(percent) + "%\r");
        }
        table.append("\n");

        int rowsNeeded = 0;
        for (List<String> list : groups.values()) {
            if (list.size() > rowsNeeded) {
                rowsNeeded = list.size();
            }
        }

        int groupCount = groups.keySet().size();
        i = 0;
        for (int row = 0; row < rowsNeeded; row++) {
            table.append("| ");
            for (int group = 0; group < groupCount; group++) {
                List<String> list = groups.values().stream().toList().get(group);
                String word;
                if (list.size()-1 < row) {
                    word = " ".repeat(groups.keySet().stream().toList().get(group).length());
                } else {
                    word = list.get(row);
                }
                table.append(word).append(" | ");
                i++;
                float percent = i/(rowsNeeded*groupCount)*100;
                System.out.print("|" + "=".repeat(Math.round(percent)) + " ".repeat(100-Math.round(percent)) + "| " + df.format(percent) + "% Generating data rows...\r");
            }
            table.append("\n");
        }

        // Outputting table in console or table markdown file
        if (input.equals("1")) {
            System.out.println(table);
        } else {
            writeInFile(table.toString(), "table.md");
        }
    }

    public String getRhymeGroup(String word) {
        // get the rhyme group of the given word
        List<String> vGroups = new ArrayList<>();

        for (int i=0;i<word.length();i++) {
            // go through the word charakter by charakter and find all vocal groups
            // with their following consonants
            StringBuilder group = new StringBuilder();
            while (i < word.length() && vocals.contains(word.toLowerCase().charAt(i)+"")) {
                group.append(word.charAt(i));
                i++;
            }
            if (!group.isEmpty()) {
                while (i < word.length() && !vocals.contains(word.toLowerCase().charAt(i) + "")) {
                    group.append(word.charAt(i));
                    i++;
                }
                vGroups.add(group.toString());
                i--;
            }
        }

        // build the important group of the last vocal group
        String rhymeGroup;
        if (vGroups.size() >= 2) {
            rhymeGroup = vGroups.get(vGroups.size()-2) + vGroups.get(vGroups.size()-1);
        } else if (vGroups.size() == 1) {
            rhymeGroup = vGroups.get(0);
        } else {
            rhymeGroup = "{N/A}";
        }

        if (rhymeGroup.equalsIgnoreCase(word)) { // make the word invalid if the full word is equal to the rhyme group
            rhymeGroup = "{N/A}";
        } else if (rhymeGroup.length()<Math.round(word.length()/2F)) { // make the word invalid if the rhyme group is shorter than half of the word
            rhymeGroup = "{N/A}";
        }

        return rhymeGroup;
    }

    public void writeInFile(String text, String fileName) {
        // outputting text to file with BufferedWriter
        if (text.equals("")) {
            return;
        }
        try {
            FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(text);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Main getInstance() {
        return instance;
    }

    public URL getExamples0() {
        return Examples0;
    }

    public URL getExamples1() {
        return Examples1;
    }

    public URL getExamples2() {
        return Examples2;
    }

    public URL getExamples3() {
        return Examples3;
    }

    public int getFin() {
        return fin;
    }

    public void setFin(int fin) {
        this.fin = fin;
    }

    public DecimalFormat getDf() {
        return df;
    }
}
