package comp3607.group10.telemetry.bluej;

import comp3607.group10.telemetry.command.Command;
import comp3607.group10.telemetry.command.CommandQueue;
import comp3607.group10.telemetry.core.Clock;
import comp3607.group10.telemetry.core.TelemetryEventFactory;
import comp3607.group10.telemetry.core.TelemetryRecorder;

/**
Holds the objects every BlueJ listener needs, so each listener takes one parameter instead of
four.

@author Anushka Dwarika
*/
public class ListenerContext {
    /** The queue that runs commands in the background. */
    private final CommandQueue queue;
    /** The recorder that commands send their work to. */
    private final TelemetryRecorder recorder;
    /** Creates telemetry events. */
    private final TelemetryEventFactory factory;
    /** Gives the current time. */
    private final Clock clock;

    /**
    Creates the context.

    @param queue the queue that runs commands in the background
    @param recorder the recorder that receives the work
    @param factory creates telemetry events
    @param clock gives the current time
    */
    public ListenerContext(CommandQueue queue, TelemetryRecorder recorder, TelemetryEventFactory factory, Clock clock) {
        this.queue = queue;
        this.recorder = recorder;
        this.factory = factory;
        this.clock = clock;
    }

    /**
    Gives the command queue.

    @return the queue
    */
    public CommandQueue getQueue() {
        return queue;
    }

    /**
    Gives the recorder.

    @return the recorder
    */
    public TelemetryRecorder getRecorder() {
        return recorder;
    }

    /**
    Gives the event factory.

    @return the factory
    */
    public TelemetryEventFactory getFactory() {
        return factory;
    }

    /**
    Gives the clock.

    @return the clock
    */
    public Clock getClock() {
        return clock;
    }

    /**
    Submits a command to the background queue.

    @param command the command to run
    */
    void submit(Command command) {
        queue.submit(command);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
