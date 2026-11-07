package comp3607.group10.telemetry.bluej;

import comp3607.group10.telemetry.command.Command;
import comp3607.group10.telemetry.command.CommandQueue;
import comp3607.group10.telemetry.core.Clock;
import comp3607.group10.telemetry.core.TelemetryEventFactory;
import comp3607.group10.telemetry.core.TelemetryRecorder;

public class ListenerContext {
    private final CommandQueue queue;
    private final TelemetryRecorder recorder;
    private final TelemetryEventFactory factory;
    private final Clock clock;

    public ListenerContext(CommandQueue queue, TelemetryRecorder recorder, TelemetryEventFactory factory, Clock clock) {
        this.queue = queue;
        this.recorder = recorder;
        this.factory = factory;
        this.clock = clock;
    }

    public CommandQueue getQueue() {
        return queue;
    }

    public TelemetryRecorder getRecorder() {
        return recorder;
    }

    public TelemetryEventFactory getFactory() {
        return factory;
    }

    public Clock getClock() {
        return clock;
    }

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
*/
