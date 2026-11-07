package comp3607.group10.telemetry.command;

import java.util.List;

import comp3607.group10.telemetry.core.TelemetryRecorder;
import comp3607.group10.telemetry.model.ClassSnapshot;

public class BaselineClassesCommand implements Command {
    private final TelemetryRecorder recorder;
    private final List<ClassSnapshot> snapshots;

    public BaselineClassesCommand(TelemetryRecorder recorder, List<ClassSnapshot> snapshots) {
        this.recorder = recorder;
        this.snapshots = List.copyOf(snapshots);
    }

    @Override
    public void execute() {
        recorder.baseline(snapshots);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
*/
