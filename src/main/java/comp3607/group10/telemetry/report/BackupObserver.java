package comp3607.group10.telemetry.report;

import java.io.IOException;

import comp3607.group10.telemetry.core.Clock;
import comp3607.group10.telemetry.core.ErrorHandler;
import comp3607.group10.telemetry.core.TelemetryObserver;
import comp3607.group10.telemetry.core.TelemetrySession;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class BackupObserver implements TelemetryObserver {
    public static final long DEFAULT_INTERVAL_MILLIS = 2 * 60 * 1000;

    private final BackupStore store;
    private final TelemetrySession session;
    private final Clock clock;
    private final long intervalMillis;
    private final ErrorHandler errorHandler;
    private long lastBackupMillis;

    public BackupObserver(BackupStore store, TelemetrySession session, Clock clock, long intervalMillis,
                          ErrorHandler errorHandler) {
        this.store = store;
        this.session = session;
        this.clock = clock;
        this.intervalMillis = intervalMillis;
        this.errorHandler = errorHandler;
        this.lastBackupMillis = session.getStartMillis();
    }

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
*/
