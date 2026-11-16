package comp3607.group10.telemetry.core;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp3607.group10.telemetry.model.TelemetryEvent;

/**
One BlueJ session: its anonymous student ID, start and end times, the folder of the last project
opened, and the event log. It is a concrete observer in the Observer pattern.

@author Haeley Parbhoo
*/
public class TelemetrySession implements TelemetryObserver {
    /** The anonymous student ID. */
    private final String studentId;
    /** When the session started. */
    private final long startMillis;
    /** When the session ended, or null while it is running. */
    private Long endMillis;
    /** The folder of the last project opened, or null. */
    private Path projectFolder;
    /** The event log. */
    private final List<TelemetryEvent> events = new ArrayList<TelemetryEvent>();

    /**
    Starts a session.

    @param studentId the anonymous student ID
    @param startMillis when it started, in epoch milliseconds
    */
    public TelemetrySession(String studentId, long startMillis) {
        this.studentId = studentId;
        this.startMillis = startMillis;
    }

    /**
    Adds the event to the log.

    @param event the telemetry event
    */
    @Override
    public void update(TelemetryEvent event) {
        events.add(event);
    }

    /**
    Marks the session as ended.

    @param endMillis when it ended, in epoch milliseconds
    */
    public void end(long endMillis) {
        this.endMillis = endMillis;
    }

    /**
    Tells whether the session has ended.

    @return true if ended
    */
    public boolean isEnded() {
        return endMillis != null;
    }

    /**
    Remembers the folder of the project that was opened.

    @param projectFolder the project folder
    */
    public void setProjectFolder(Path projectFolder) {
        this.projectFolder = projectFolder;
    }

    /**
    Gives the folder of the last project opened.

    @return the folder, or null if none is known
    */
    public Path getProjectFolder() {
        return projectFolder;
    }

    /**
    Gives the anonymous student ID.

    @return the student ID
    */
    public String getStudentId() {
        return studentId;
    }

    /**
    Gives when the session started.

    @return epoch milliseconds
    */
    public long getStartMillis() {
        return startMillis;
    }

    /**
    Gives when the session ended.

    @return epoch milliseconds, or null while running
    */
    public Long getEndMillis() {
        return endMillis;
    }

    /**
    Gives the event log.

    @return a read-only list of events
    */
    public List<TelemetryEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Observer Design Pattern. https://sourcemaking.com/design_patterns/observer
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
