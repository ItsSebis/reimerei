# Reimerei – Junioraufgabe 1
## Lösungsidee
Das Programm erkennt den für einen Reim wichtigen Teil jedes Wortes und vergleicht das mit jedem anderem Wort, ob das den gleichen „Reimteil“ hat.

## Implementierung in ein Java Programm
Zuerst fordert das Programm die Auswahl der Wortpalette und lädt diese dann in eine Liste:
```

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
  url = new URL(
}

```
Reimgruppe wird ermittelt indem:
- eine Liste mit allen Vokalgruppen erstellen und alle hinzufügen
- wenn vorhanden, die letzten zwei Vokalgruppen mit allen folgenden Konsonanten zurückgeben, sonst nur eine
- wenn die Vokalgruppe das ganze Wort oder weniger als die Hälfte des Wortes umfasst, keine/ungültige Rückgabe
 
```
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
```

Wenn alle Wörter geladen sind, starten die Threads zum analysieren der einzelnen Wörter, was folgendermaßen funktioniert:
- Reimgruppe des Wortes finden
- neue Liste für reimende Worte
- bei jedem anderen Wort checken ob es die gleiche Wortgruppe hat
- anderes Wort nur hinzufügen, wenn keines der Wörter mit dem kompletten anderen endet.

```
// set number of threads to use for sorting
int threadCount = 4;

for (int i = 1; i <= threadCount; i++) {
    // Start sorting threads
    Thread thread = new Thread(new Sorter(i, threadCount, words, groups));
    threads.put(i, 0);
    thread.start();
}```
```
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
}
```

Nun wird nach der Art der Rückgabe gefragt, als table.md Datei oder das Markdown in der Konsole, was aber nicht schön formatiert ist. Hier wird die Tabelle so generiert, dass man sie schön in der GitHub ansicht betrachten könnte.

```
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
}```

Die Lösungen der Beispiele befinden sich in der table.md Datei im Ordner der Ausführbaren JAR Datei.
