package comp3607.group10.telemetry.command;

import comp3607.group10.telemetry.core.TelemetryRecorder;
import comp3607.group10.telemetry.model.ClassSnapshot;

public class ClassStateCommand implements Command {
    private final TelemetryRecorder recorder;
    private final ClassSnapshot snapshot;
    private final long timestampMillis;

    public ClassStateCommand(TelemetryRecorder recorder, ClassSnapshot snapshot, long timestampMillis) {
        this.recorder = recorder;
        this.snapshot = snapshot;
        this.timestampMillis = timestampMillis;
    }

    @Override
    public void execute() {
        recorder.classStateChanged(snapshot, timestampMillis);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
*/
