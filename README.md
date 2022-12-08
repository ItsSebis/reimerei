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
<-- Link -->

Wenn alle Wörter geladen sind, starten die Threads zum analysieren der einzelnen Wörter, was folgendermaßen funktioniert:
- Reimgruppe des Wortes finden
- neue Liste für reimende Worte
- bei jedem anderen Wort checken ob es die gleiche Wortgruppe hat
  - anderes Wort nur hinzufügen, wenn keines der Wörter mit dem kompletten anderen endet.
<-- Link -->

Nun wird nach der Art der Rückgabe gefragt, als table.md Datei oder das Markdown in der Konsole, was aber nicht schön formatiert ist. Hier wird die Tabelle so generiert, dass man sie schön in der GitHub ansicht betrachten könnte.
<-- Link -->

Die Lösungen der Beispiele befinden sich in der table.md Datei im Ordner der Ausführbaren JAR Datei.
