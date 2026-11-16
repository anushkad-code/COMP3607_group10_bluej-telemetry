package comp3607.group10.telemetry.command;

import comp3607.group10.telemetry.core.TelemetryRecorder;
import comp3607.group10.telemetry.model.ClassSnapshot;

/**
Command that records a class's new state, so creations, modifications and new methods can be
detected.

@author Anushka Dwarika
*/
public class ClassStateCommand implements Command {
    /** The recorder that receives the work. */
    private final TelemetryRecorder recorder;
    /** The state of the class. */
    private final ClassSnapshot snapshot;
    /** When the state changed, in epoch milliseconds. */
    private final long timestampMillis;

    /**
    Creates the command.

    @param recorder the recorder that receives the work
    @param snapshot the state of the class at this moment
    @param timestampMillis when it happened, in epoch milliseconds
    */
    public ClassStateCommand(TelemetryRecorder recorder, ClassSnapshot snapshot, long timestampMillis) {
        this.recorder = recorder;
        this.snapshot = snapshot;
        this.timestampMillis = timestampMillis;
    }

    /**
    Gives the class's new state to the recorder.
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
