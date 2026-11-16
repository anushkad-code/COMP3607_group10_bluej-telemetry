package comp3607.group10.telemetry.command;

import java.nio.file.Path;

import comp3607.group10.telemetry.core.TelemetryRecorder;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class ProjectOpenedCommand implements Command {
    private final TelemetryRecorder recorder;
    private final TelemetryEvent event;
    private final Path projectFolder;

    public ProjectOpenedCommand(TelemetryRecorder recorder, TelemetryEvent event, Path projectFolder) {
        this.recorder = recorder;
        this.event = event;
        this.projectFolder = projectFolder;
    }

    @Override
    public void execute() {
        recorder.projectOpened(event, projectFolder);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
