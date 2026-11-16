package comp3607.group10.telemetry.command;

import comp3607.group10.telemetry.core.TelemetryRecorder;

/**
Command that records one edit in a class editor.

@author Anushka Dwarika
*/
public class EditCommand implements Command {
    /** The recorder that receives the work. */
    private final TelemetryRecorder recorder;
    /** The class that was edited. */
    private final String className;
    /** How many characters were inserted. */
    private final int charsInserted;
    /** How many characters were deleted. */
    private final int charsDeleted;
    /** When the edit happened, in epoch milliseconds. */
    private final long timestampMillis;

    /**
    Creates the command.

    @param recorder the recorder that receives the work
    @param className the name of the class
    @param charsInserted how many characters were inserted
    @param charsDeleted how many characters were deleted
    @param timestampMillis when it happened, in epoch milliseconds
    */
    public EditCommand(TelemetryRecorder recorder, String className, int charsInserted, int charsDeleted, long timestampMillis) {
        this.recorder = recorder;
        this.className = className;
        this.charsInserted = charsInserted;
        this.charsDeleted = charsDeleted;
        this.timestampMillis = timestampMillis;
    }

    /**
    Gives the edit to the recorder, which groups edits into bursts.
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
