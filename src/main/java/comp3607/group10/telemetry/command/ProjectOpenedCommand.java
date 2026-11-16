package comp3607.group10.telemetry.command;

import java.nio.file.Path;

import comp3607.group10.telemetry.core.TelemetryRecorder;
import comp3607.group10.telemetry.model.TelemetryEvent;

/**
Command that records a project opening and remembers its folder, so the reports can be saved
there.

@author Anushka Dwarika
*/
public class ProjectOpenedCommand implements Command {
    /** The recorder that receives the work. */
    private final TelemetryRecorder recorder;
    /** The project opened event. */
    private final TelemetryEvent event;
    /** The project's folder, or null if it is not known. */
    private final Path projectFolder;

    /**
    Creates the command.

    @param recorder the recorder that receives the work
    @param event the project opened event
    @param projectFolder the folder of the BlueJ project, or null if it is not known
    */
    public ProjectOpenedCommand(TelemetryRecorder recorder, TelemetryEvent event, Path projectFolder) {
        this.recorder = recorder;
        this.event = event;
        this.projectFolder = projectFolder;
    }

    /**
    Gives the event and the folder to the recorder.
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
