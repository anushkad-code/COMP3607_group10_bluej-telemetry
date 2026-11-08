package comp3607.group10.telemetry.core;

import java.util.LinkedHashMap;
import java.util.Map;

import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class EditBurst {
    private final String className;
    private final long startMillis;
    private long lastMillis;
    private int keystrokes;
    private int inserted;
    private int deleted;

    public EditBurst(String className, long startMillis) {
        this.className = className;
        this.startMillis = startMillis;
        this.lastMillis = startMillis;
    }

    public boolean accepts(String otherClass, long timestampMillis, long gapMillis) {
        return className.equals(otherClass) && timestampMillis - lastMillis <= gapMillis;
    }

    public void add(int charsInserted, int charsDeleted, long timestampMillis) {
        keystrokes++;
        inserted = inserted + charsInserted;
        deleted = deleted + charsDeleted;
        lastMillis = timestampMillis;
    }

    public TelemetryEvent toEvent() {
        Map<String, String> details = new LinkedHashMap<String, String>();
        details.put(DetailKeys.KEYSTROKES, Integer.toString(keystrokes));
        details.put(DetailKeys.CHARS_INSERTED, Integer.toString(inserted));
        details.put(DetailKeys.CHARS_DELETED, Integer.toString(deleted));
        details.put(DetailKeys.DURATION_MS, Long.toString(lastMillis - startMillis));
        return new TelemetryEvent(EventType.EDIT_BURST, startMillis, className, details);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
