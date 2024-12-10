### Frage:
Was sind jeweils Vor- und Nachteile der entsprechenden Realisierung der Vererbung in Bezug auf das Datenmodell?  
Welche Variante würden wir in der Praxis für am besten geeignet halten?

### Überlegungen aus Sicht einer Studentengruppe

#### 1. JOINED TABLE (Getrennte Tabellen für jede Klasse mit Verknüpfung über Joins)
**Vorteile:**
- Klar strukturierte Datenhaltung durch separate Tabellen je Klasse.
- Änderungen an einer Unterklasse beeinflussen andere Klassen nur indirekt.
- Durch Fremdschlüsselbeziehungen ist eine hohe Datenintegrität gewährleistet.
- Spezifische Abfragen auf einzelne Unterklassen lassen sich effizient durchführen.

**Nachteile:**
- Abfragen über die gesamte Vererbungshierarchie werden komplexer, da mehrere Joins notwendig sind.
- Bei umfangreichen oder tiefen Hierarchien können Joins die Performance beeinträchtigen.
- Das Datenbankschema wird durch zahlreiche Tabellen unübersichtlich.

---

#### 2. SINGLE TABLE (Eine einzige Tabelle für die gesamte Vererbungshierarchie)
**Vorteile:**
- Keine Joins erforderlich, da alle Daten in einer Tabelle gespeichert sind.
- Abfragen über sämtliche Vererbungsebenen lassen sich sehr effizient durchführen.
- Das Schema ist einfach gehalten, da es nur eine Tabelle gibt.

**Nachteile:**
- Viele Spalten werden von einigen Unterklassen nicht genutzt, was zu Redundanz führt.
- Änderungen an einer Unterklasse haben potenziell Auswirkungen auf die gesamte Tabelle.
- Mit zunehmender Anzahl an Unterklassen kann die Tabelle sehr groß und schwer handhabbar werden.
- Das Modell ist weniger stark normalisiert.

---

#### 3. TABLE PER CLASS (Jede Klasse hat eine eigene Tabelle ohne Joins)
**Vorteile:**
- Jede Klasse wird in einer eigenen Tabelle abgebildet, was einer normalisierten Struktur entspricht.
- Abfragen, die sich nur auf eine Unterklasse beziehen, können effizient durchgeführt werden.
- Änderungen an einer Klasse bleiben auf deren Tabelle beschränkt.

**Nachteile:**
- Es existiert keine zentrale Tabelle für Abfragen über die gesamte Vererbungshierarchie.
- Gemeinsame Attribute müssen in jeder Tabelle erneut angelegt werden, was Redundanz erzeugt.
- Polymorphe Abfragen sind komplex, da sie Union-Operationen über mehrere Tabellen benötigen.

---

### Fazit
Die optimale Wahl hängt von den individuellen Anforderungen ab:

- **JOINED TABLE**: Sinnvoll, wenn Datenintegrität, Wartbarkeit und strukturelle Klarheit wichtig sind, selbst wenn das zu komplexeren Abfragen führt.
- **SINGLE TABLE**: Geeignet für einfache Hierarchien und häufige Anfragen über alle Unterklassen hinweg, sofern man den Speicherbedarf und die verringerte Normalisierung in Kauf nimmt.
- **TABLE PER CLASS**: Empfehlenswert, wenn Klassen weitgehend unabhängig sind und selten polymorphe Abfragen gestellt werden.

Für unser Beispiel ist die **JOINED TABLE**-Variante am besten geeignet, weil sie unsere Daten sinnvoll auf einzelne Tabellen verteilt, dabei trotzdem leicht anpassbar bleibt und uns einen guten Überblick über die gesamte Struktur verschafft.