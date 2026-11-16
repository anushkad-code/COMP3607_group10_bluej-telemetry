package comp3607.group10.telemetry.command;

import java.util.ArrayList;
import java.util.List;

import comp3607.group10.telemetry.core.TelemetryRecorder;
import comp3607.group10.telemetry.model.ClassSnapshot;

/**
Command that records the classes already in a project when it opens, so they are not reported as
new.

@author Anushka Dwarika
*/
public class BaselineClassesCommand implements Command {
    /** The recorder that receives the work. */
    private final TelemetryRecorder recorder;
    /** The classes that already existed. */
    private final List<ClassSnapshot> snapshots;

    /**
    Creates the command.

    @param recorder the recorder that receives the work
    @param snapshots the classes that already existed when the project opened
    */
    public BaselineClassesCommand(TelemetryRecorder recorder, List<ClassSnapshot> snapshots) {
        this.recorder = recorder;
        this.snapshots = new ArrayList<ClassSnapshot>(snapshots);
    }

    /**
    Tells the recorder about the existing classes.
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
