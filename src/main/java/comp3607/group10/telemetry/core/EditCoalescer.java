package comp3607.group10.telemetry.core;

import comp3607.group10.telemetry.model.TelemetryEvent;

/**
Groups edits that are less than 2 seconds apart into edit bursts.

@author Haeley Parbhoo
*/
public class EditCoalescer {
    /** The default longest pause between edits in one burst: 2 seconds. */
    public static final long DEFAULT_GAP_MILLIS = 2000;

    /** The longest pause between edits in one burst. */
    private final long gapMillis;
    /** The burst being built, or null. */
    private EditBurst current;

    /**
    Creates a coalescer with a chosen gap.

    @param gapMillis the longest pause between edits that still belong to one burst
    */
    public EditCoalescer(long gapMillis) {
        this.gapMillis = gapMillis;
    }

    /**
    Creates a coalescer with a 2-second gap.
    */
    public EditCoalescer() {
        this(DEFAULT_GAP_MILLIS);
    }

    /**
    Adds an edit. If it starts a new burst, the previous burst is finished.

    @param className the name of the class
    @param charsInserted how many characters were inserted
    @param charsDeleted how many characters were deleted
    @param timestampMillis when it happened, in epoch milliseconds
    @return the finished burst event, or null if none finished
    */
    public TelemetryEvent edit(String className, int charsInserted, int charsDeleted, long timestampMillis) {
        TelemetryEvent finished = null;
        if (current != null && !current.accepts(className, timestampMillis, gapMillis)) {
            finished = flush();
        }
        if (current == null) {
            current = new EditBurst(className, timestampMillis);
        }
        current.add(charsInserted, charsDeleted, timestampMillis);
        return finished;
    }

    /**
    Finishes the open burst.

    @return the burst event, or null if none was open
    */
    public TelemetryEvent flush() {
        if (current == null) {
            return null;
        }
        TelemetryEvent event = current.toEvent();
        current = null;
        return event;
    }

    /**
    Tells whether a burst is being built.

    @return true if one is open
    */
    public boolean hasOpenBurst() {
        return current != null;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
