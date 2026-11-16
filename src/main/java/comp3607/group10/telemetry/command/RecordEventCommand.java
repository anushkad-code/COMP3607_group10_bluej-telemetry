package comp3607.group10.telemetry.command;

import comp3607.group10.telemetry.core.TelemetryRecorder;
import comp3607.group10.telemetry.model.TelemetryEvent;

/**
Command that records one ready-made event, such as a compile error or a program run.

@author Anushka Dwarika
*/
public class RecordEventCommand implements Command {
    /** The recorder that receives the work. */
    private final TelemetryRecorder recorder;
    /** The event to record. */
    private final TelemetryEvent event;

    /**
    Creates the command.

    @param recorder the recorder that receives the work
    @param event the event to record
    */
    public RecordEventCommand(TelemetryRecorder recorder, TelemetryEvent event) {
        this.recorder = recorder;
        this.event = event;
    }

    /**
    Gives the event to the recorder.
    */
    @Override
    public void execute() {
        recorder.record(event);
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
