package comp3607.group10.telemetry.command;

import comp3607.group10.telemetry.core.TelemetryRecorder;
import comp3607.group10.telemetry.model.TelemetryEvent;

/**
Command that records a class being renamed.

@author Anushka Dwarika
*/
public class ClassRenamedCommand implements Command {
    /** The recorder that receives the work. */
    private final TelemetryRecorder recorder;
    /** The class renamed event. */
    private final TelemetryEvent event;

    /**
    Creates the command.

    @param recorder the recorder that receives the work
    @param event the class renamed event
    */
    public ClassRenamedCommand(TelemetryRecorder recorder, TelemetryEvent event) {
        this.recorder = recorder;
        this.event = event;
    }

    /**
    Tells the recorder the class was renamed.
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
