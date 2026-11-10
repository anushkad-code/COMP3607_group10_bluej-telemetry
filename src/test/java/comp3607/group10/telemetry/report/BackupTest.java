package comp3607.group10.telemetry.report;

import comp3607.group10.telemetry.CollectedErrors;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import comp3607.group10.telemetry.ManualClock;
import comp3607.group10.telemetry.analytics.StatisticsCalculator;
import comp3607.group10.telemetry.core.TelemetrySession;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class BackupTest {
    Path dir;

    private ManualClock clock;
    private TelemetrySession session;
    private BackupStore store;

    @BeforeEach
    public void setUp() throws Exception {
        dir = Files.createTempDirectory("telemetry-test");
        clock = new ManualClock(0L);
        session = new TelemetrySession("s", 0L);
        store = new BackupStore(dir, new JsonReportGenerator(new StatisticsCalculator()));
    }

    @Test
    public void observerBacksUpAtMostOncePerInterval() throws IOException {
        BackupObserver observer = new BackupObserver(store, session, clock, 120000, new CollectedErrors());
        TelemetryEvent event = new TelemetryEvent(EventType.CLASS_CREATED, 0L, "A");

        clock.advanceSeconds(60);
        observer.update(event);
        assertTrue(!(Files.exists(store.backupFile())));

        clock.advanceSeconds(60);
        observer.update(event);
        assertTrue(Files.exists(store.backupFile()));
        long firstWrite = Files.getLastModifiedTime(store.backupFile()).toMillis();

        Files.delete(store.backupFile());
        clock.advanceSeconds(1);
        observer.update(event);
        assertTrue(!Files.exists(store.backupFile()), "too soon after the last backup");
        assertTrue(firstWrite > 0);
    }

    @Test
    public void backupFailuresAreReportedNotThrown() throws IOException {
        Path blocker = dir.resolve("not-a-folder");
        Files.writeString(blocker, "x");
        BackupStore broken = new BackupStore(blocker, new JsonReportGenerator(new StatisticsCalculator()));
        CollectedErrors errors = new CollectedErrors();
        BackupObserver observer = new BackupObserver(broken, session, clock, 0, errors);
        observer.update(new TelemetryEvent(EventType.CLASS_CREATED, 0L, "A"));
        assertEquals(1, errors.errors.size());
    }

    @Test
    public void leftoverBackupIsRecoveredUnderANewName() throws IOException {
        store.save(session, 1000L);
        Path recovered = store.recoverLeftover(1793016000000L, ZoneOffset.UTC);
        assertNotNull(recovered);
        assertEquals("telemetry-recovered-20261026-120000.json", recovered.getFileName().toString());
        assertTrue(!(Files.exists(store.backupFile())));
        assertNull(store.recoverLeftover(0L, ZoneOffset.UTC));
    }

    @Test
    public void discardRemovesTheBackup() throws IOException {
        store.save(session, 1000L);
        store.discard();
        assertTrue(!(Files.exists(store.backupFile())));
        store.discard();
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
*/
