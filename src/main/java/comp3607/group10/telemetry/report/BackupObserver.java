package comp3607.group10.telemetry.report;

import java.io.IOException;

import comp3607.group10.telemetry.core.Clock;
import comp3607.group10.telemetry.core.ErrorHandler;
import comp3607.group10.telemetry.core.TelemetryObserver;
import comp3607.group10.telemetry.core.TelemetrySession;
import comp3607.group10.telemetry.model.TelemetryEvent;

/**
A concrete observer that saves a JSON backup of the session at most once every two minutes, so
data is not lost if BlueJ closes unexpectedly.

@author Reinaldo Singh
*/
public class BackupObserver implements TelemetryObserver {
    /** The default time between backups: two minutes. */
    public static final long DEFAULT_INTERVAL_MILLIS = 2 * 60 * 1000;

    /** Reads and writes the backup file. */
    private final BackupStore store;
    /** The session to back up. */
    private final TelemetrySession session;
    /** Gives the current time. */
    private final Clock clock;
    /** How often to back up. */
    private final long intervalMillis;
    /** Reports backups that fail. */
    private final ErrorHandler errorHandler;
    /** When the last backup was saved. */
    private long lastBackupMillis;

    /**
    Creates the backup observer.

    @param store reads and writes the backup file
    @param session the session being reported on
    @param clock gives the current time
    @param intervalMillis how often to back up, in milliseconds
    @param errorHandler reports errors from background work
    */
    public BackupObserver(BackupStore store, TelemetrySession session, Clock clock, long intervalMillis,
                          ErrorHandler errorHandler) {
        this.store = store;
        this.session = session;
        this.clock = clock;
        this.intervalMillis = intervalMillis;
        this.errorHandler = errorHandler;
        this.lastBackupMillis = session.getStartMillis();
    }

    /**
    Saves a backup if enough time has passed since the last one. A failed backup is reported, not
    thrown.

    @param event the telemetry event
    */
    @Override
    public void update(TelemetryEvent event) {
        long now = clock.nowMillis();
        if (now - lastBackupMillis < intervalMillis) {
            return;
        }
        lastBackupMillis = now;
        try {
            store.save(session, now);
        } catch (IOException e) {
            errorHandler.handle(e);
        }
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Observer Design Pattern. https://sourcemaking.com/design_patterns/observer
Oracle. The Java Tutorials: Exceptions (Catching and Handling Exceptions). https://docs.oracle.com/javase/tutorial/essential/exceptions/handling.html
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
