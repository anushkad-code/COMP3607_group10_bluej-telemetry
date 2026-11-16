package comp3607.group10.telemetry.core;

import java.nio.file.Path;
import java.util.List;

import comp3607.group10.telemetry.model.ClassSnapshot;
import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

/**
Receives the work from the commands and publishes the resulting events to the hub. It is the
receiver in the Command pattern.

@author Haeley Parbhoo
*/
public class TelemetryRecorder {
    /** The Observer subject events are published to. */
    private final TelemetrySubject hub;
    /** The session being recorded. */
    private final TelemetrySession session;
    /** Detects class creations, modifications and new methods. */
    private final ClassTracker classTracker;
    /** Groups edits into bursts. */
    private final EditCoalescer editCoalescer;
    /** When the last compile started, or null. */
    private Long lastCompileStartMillis;

    /**
    Creates the recorder.

    @param hub the subject events are published to
    @param session the session being reported on
    @param classTracker detects class changes
    @param editCoalescer groups edits into bursts
    */
    public TelemetryRecorder(TelemetrySubject hub, TelemetrySession session, ClassTracker classTracker, EditCoalescer editCoalescer) {
        this.hub = hub;
        this.session = session;
        this.classTracker = classTracker;
        this.editCoalescer = editCoalescer;
    }

    /**
    Records an event. Any open edit burst is written first, and compile starts get the time since
    the previous compile.

    @param event the telemetry event
    */
    public void record(TelemetryEvent event) {
        flushEdits();
        hub.notifyObservers(withCompileInterval(event));
    }

    /**
    Records a project opening and remembers its folder, so the reports can be saved there.

    @param openEvent the project opened event
    @param projectFolder the folder of the BlueJ project, or null if it is not known
    */
    public void projectOpened(TelemetryEvent openEvent, Path projectFolder) {
        if (projectFolder != null) {
            session.setProjectFolder(projectFolder);
        }
        record(openEvent);
    }

    /**
    Remembers the classes that already existed when the project opened.

    @param snapshots the classes that already existed when the project opened
    */
    public void baseline(List<ClassSnapshot> snapshots) {
        for (ClassSnapshot snapshot : snapshots) {
            classTracker.baseline(snapshot);
        }
    }

    /**
    Records the events a class's new state causes.

    @param snapshot the state of the class at this moment
    @param timestampMillis when it happened, in epoch milliseconds
    */
    public void classStateChanged(ClassSnapshot snapshot, long timestampMillis) {
        flushEdits();
        for (TelemetryEvent event : classTracker.stateChanged(snapshot, timestampMillis)) {
            hub.notifyObservers(event);
        }
    }

    /**
    Records a class rename.

    @param renameEvent the class renamed event
    */
    public void classRenamed(TelemetryEvent renameEvent) {
        classTracker.renamed(renameEvent.getClassName(), renameEvent.getDetail(DetailKeys.OLD_NAME));
        record(renameEvent);
    }

    /**
    Records a class deletion.

    @param deleteEvent the class deleted event
    */
    public void classDeleted(TelemetryEvent deleteEvent) {
        classTracker.removed(deleteEvent.getClassName());
        record(deleteEvent);
    }

    /**
    Adds an edit. A finished edit burst is recorded.

    @param className the name of the class
    @param charsInserted how many characters were inserted
    @param charsDeleted how many characters were deleted
    @param timestampMillis when it happened, in epoch milliseconds
    */
    public void edit(String className, int charsInserted, int charsDeleted, long timestampMillis) {
        TelemetryEvent finishedBurst = editCoalescer.edit(className, charsInserted, charsDeleted, timestampMillis);
        if (finishedBurst != null) {
            hub.notifyObservers(finishedBurst);
        }
    }

    /**
    Records the end of the session, only once.

    @param endEvent the session ended event
    */
    public void endSession(TelemetryEvent endEvent) {
        if (session.isEnded()) {
            return;
        }
        record(endEvent);
        session.end(endEvent.getTimestampMillis());
    }

    /**
    Gives the session being recorded.

    @return the session
    */
    public TelemetrySession getSession() {
        return session;
    }

    /**
    Records the open edit burst, if there is one.
    */
    private void flushEdits() {
        TelemetryEvent openBurst = editCoalescer.flush();
        if (openBurst != null) {
            hub.notifyObservers(openBurst);
        }
    }

    /**
    Adds the time since the previous compile to a compile started event.

    @param event the telemetry event
    @return the event, with the time since the last compile added
    */
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
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
