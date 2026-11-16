package comp3607.group10.telemetry.core;

import java.util.LinkedHashMap;
import java.util.Map;

import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

/**
A group of edits to one class that happened close together. It becomes one EDIT_BURST event, so
the log is not flooded with single keystrokes.

@author Haeley Parbhoo
*/
public class EditBurst {
    /** The class being edited. */
    private final String className;
    /** When the first edit happened. */
    private final long startMillis;
    /** When the latest edit happened. */
    private long lastMillis;
    /** How many edits are in the burst. */
    private int keystrokes;
    /** Total characters inserted. */
    private int inserted;
    /** Total characters deleted. */
    private int deleted;

    /**
    Starts a burst.

    @param className the name of the class
    @param startMillis when it started, in epoch milliseconds
    */
    public EditBurst(String className, long startMillis) {
        this.className = className;
        this.startMillis = startMillis;
        this.lastMillis = startMillis;
    }

    /**
    Tells whether a new edit belongs to this burst: same class, and soon enough after the last
    edit.

    @param otherClass the class of the new edit
    @param timestampMillis when it happened, in epoch milliseconds
    @param gapMillis the longest pause between edits that still belong to one burst
    @return true if it belongs
    */
    public boolean accepts(String otherClass, long timestampMillis, long gapMillis) {
        return className.equals(otherClass) && timestampMillis - lastMillis <= gapMillis;
    }

    /**
    Adds an edit to the burst.

    @param charsInserted how many characters were inserted
    @param charsDeleted how many characters were deleted
    @param timestampMillis when it happened, in epoch milliseconds
    */
    public void add(int charsInserted, int charsDeleted, long timestampMillis) {
        keystrokes++;
        inserted = inserted + charsInserted;
        deleted = deleted + charsDeleted;
        lastMillis = timestampMillis;
    }

    /**
    Turns the burst into an EDIT_BURST event with its counts and duration.

    @return the event
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
