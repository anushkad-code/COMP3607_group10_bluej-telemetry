package comp3607.group10.telemetry.core;

import comp3607.group10.telemetry.model.TelemetryEvent;

public class EditCoalescer {
    public static final long DEFAULT_GAP_MILLIS = 2000;

    private final long gapMillis;
    private EditBurst current;

    public EditCoalescer(long gapMillis) {
        this.gapMillis = gapMillis;
    }

    public EditCoalescer() {
        this(DEFAULT_GAP_MILLIS);
    }

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

    public TelemetryEvent flush() {
        if (current == null) {
            return null;
        }
        TelemetryEvent event = current.toEvent();
        current = null;
        return event;
    }

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
*/
