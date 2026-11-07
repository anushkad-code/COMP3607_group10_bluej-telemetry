package comp3607.group10.telemetry.bluej;

import java.util.ArrayList;
import java.util.List;

import comp3607.group10.telemetry.TestData;
import comp3607.group10.telemetry.CollectingQueue;
import comp3607.group10.telemetry.ManualClock;
import comp3607.group10.telemetry.command.Command;
import comp3607.group10.telemetry.core.ClassTracker;
import comp3607.group10.telemetry.core.EditCoalescer;
import comp3607.group10.telemetry.core.ErrorCodeClassifier;
import comp3607.group10.telemetry.core.TelemetryEventFactory;
import comp3607.group10.telemetry.core.TelemetryHub;
import comp3607.group10.telemetry.core.TelemetryRecorder;
import comp3607.group10.telemetry.core.TelemetrySession;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

class ListenerTestSupport {
    final ManualClock clock = new ManualClock(1000L);
    final TelemetrySession session = new TelemetrySession("s", 0L);
    final CollectingQueue queue = new CollectingQueue();
    final List<Command> queued = queue.queued;
    final ListenerContext context;

    ListenerTestSupport() {
        TelemetryHub hub = new TelemetryHub();
        hub.register(session);
        TelemetryRecorder recorder = new TelemetryRecorder(hub, session, new ClassTracker(), new EditCoalescer());
        context = new ListenerContext(queue, recorder,
                new TelemetryEventFactory(clock, new ErrorCodeClassifier()), clock);
    }

    List<TelemetryEvent> runQueued() throws Exception {
        for (Command command : queued) {
            command.execute();
        }
        queued.clear();
        context.getRecorder().record(new TelemetryEvent(EventType.SESSION_ENDED, clock.nowMillis(), null));
        List<TelemetryEvent> events = new ArrayList<TelemetryEvent>(session.getEvents());
        events.remove(events.size() - 1);
        return events;
    }

    List<EventType> runQueuedTypes() throws Exception {
        return TestData.typesOf(runQueued());
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
*/
