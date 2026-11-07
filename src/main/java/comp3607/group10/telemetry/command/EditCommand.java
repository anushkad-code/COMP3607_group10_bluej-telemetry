package comp3607.group10.telemetry.command;

import comp3607.group10.telemetry.core.TelemetryRecorder;

public class EditCommand implements Command {
    private final TelemetryRecorder recorder;
    private final String className;
    private final int charsInserted;
    private final int charsDeleted;
    private final long timestampMillis;

    public EditCommand(TelemetryRecorder recorder, String className, int charsInserted, int charsDeleted, long timestampMillis) {
        this.recorder = recorder;
        this.className = className;
        this.charsInserted = charsInserted;
        this.charsDeleted = charsDeleted;
        this.timestampMillis = timestampMillis;
    }

    @Override
    public void execute() {
        recorder.edit(className, charsInserted, charsDeleted, timestampMillis);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
*/
