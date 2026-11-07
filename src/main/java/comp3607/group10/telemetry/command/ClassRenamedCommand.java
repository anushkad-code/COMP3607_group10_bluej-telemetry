package comp3607.group10.telemetry.command;

import comp3607.group10.telemetry.core.TelemetryRecorder;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class ClassRenamedCommand implements Command {
    private final TelemetryRecorder recorder;
    private final TelemetryEvent event;

    public ClassRenamedCommand(TelemetryRecorder recorder, TelemetryEvent event) {
        this.recorder = recorder;
        this.event = event;
    }

    @Override
    public void execute() {
        recorder.classRenamed(event);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
*/
