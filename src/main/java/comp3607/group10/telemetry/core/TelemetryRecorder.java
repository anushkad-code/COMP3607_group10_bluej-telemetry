package comp3607.group10.telemetry.core;

import java.util.List;

import comp3607.group10.telemetry.model.ClassSnapshot;
import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class TelemetryRecorder {
    private final TelemetrySubject hub;
    private final TelemetrySession session;
    private final ClassTracker classTracker;
    private final EditCoalescer editCoalescer;
    private Long lastCompileStartMillis;

    public TelemetryRecorder(TelemetrySubject hub, TelemetrySession session, ClassTracker classTracker, EditCoalescer editCoalescer) {
        this.hub = hub;
        this.session = session;
        this.classTracker = classTracker;
        this.editCoalescer = editCoalescer;
    }

    public void record(TelemetryEvent event) {
        flushEdits();
        hub.notifyObservers(withCompileInterval(event));
    }

    public void baseline(List<ClassSnapshot> snapshots) {
        for (ClassSnapshot snapshot : snapshots) {
            classTracker.baseline(snapshot);
        }
    }

    public void classStateChanged(ClassSnapshot snapshot, long timestampMillis) {
        flushEdits();
        for (TelemetryEvent event : classTracker.stateChanged(snapshot, timestampMillis)) {
            hub.notifyObservers(event);
        }
    }

    public void classRenamed(TelemetryEvent renameEvent) {
        classTracker.renamed(renameEvent.getClassName(), renameEvent.getDetail(DetailKeys.OLD_NAME));
        record(renameEvent);
    }

    public void classDeleted(TelemetryEvent deleteEvent) {
        classTracker.removed(deleteEvent.getClassName());
        record(deleteEvent);
    }

    public void edit(String className, int charsInserted, int charsDeleted, long timestampMillis) {
        TelemetryEvent finishedBurst = editCoalescer.edit(className, charsInserted, charsDeleted, timestampMillis);
        if (finishedBurst != null) {
            hub.notifyObservers(finishedBurst);
        }
    }

    public void endSession(TelemetryEvent endEvent) {
        if (session.isEnded()) {
            return;
        }
        record(endEvent);
        session.end(endEvent.getTimestampMillis());
    }

    public TelemetrySession getSession() {
        return session;
    }

    private void flushEdits() {
        TelemetryEvent openBurst = editCoalescer.flush();
        if (openBurst != null) {
            hub.notifyObservers(openBurst);
        }
    }

    private TelemetryEvent withCompileInterval(TelemetryEvent event) {
        if (!event.is(EventType.COMPILE_STARTED)) {
            return event;
        }
        Long previous = lastCompileStartMillis;
        lastCompileStartMillis = event.getTimestampMillis();
        if (previous == null) {
            return event;
        }
        return event.withDetail(DetailKeys.MS_SINCE_PREVIOUS_COMPILE, Long.toString(event.getTimestampMillis() - previous));
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
*/
