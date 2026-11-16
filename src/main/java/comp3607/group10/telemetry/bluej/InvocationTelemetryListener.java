package comp3607.group10.telemetry.bluej;

import bluej.extensions2.event.InvocationFinishedEvent;
import bluej.extensions2.event.InvocationFinishedListener;

import comp3607.group10.telemetry.command.RecordEventCommand;
import comp3607.group10.telemetry.model.RunOutcome;

/**
Listens for finished program runs (method calls and object creation from BlueJ) and records how
each run ended. Part of the Command pattern: this class is a client.

@author Anushka Dwarika
*/
public class InvocationTelemetryListener implements InvocationFinishedListener {
    /** The shared objects every listener needs. */
    private final ListenerContext context;

    /**
    Creates the listener.

    @param context the shared objects every listener needs
    */
    public InvocationTelemetryListener(ListenerContext context) {
        this.context = context;
    }

    /**
    Records a finished run and whether it ended normally, with an exception, or was terminated.

    @param event the telemetry event
    */
    @Override
    public void invocationFinished(InvocationFinishedEvent event) {
        RunOutcome outcome = RunOutcome.fromExitType(event.getEventType().name());
        context.submit(new RecordEventCommand(context.getRecorder(), context.getFactory().programRun(event.getClassName(), event.getMethodName(), outcome)));
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
BlueJ. Writing Extensions for BlueJ 5+. https://www.bluej.org/extensions/writingextensions2.html
BlueJ / King's College London. BlueJ source code, Extensions API package bluej.extensions2. https://github.com/k-pet-group/BlueJ-Greenfoot
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
