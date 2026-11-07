package comp3607.group10.telemetry.command;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import comp3607.group10.telemetry.TestData;
import comp3607.group10.telemetry.CollectedFiles;
import comp3607.group10.telemetry.ManualClock;
import comp3607.group10.telemetry.analytics.StatisticsCalculator;
import comp3607.group10.telemetry.core.ClassTracker;
import comp3607.group10.telemetry.core.EditCoalescer;
import comp3607.group10.telemetry.core.TelemetryHub;
import comp3607.group10.telemetry.core.TelemetryRecorder;
import comp3607.group10.telemetry.core.TelemetrySession;
import comp3607.group10.telemetry.model.ClassSnapshot;
import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;
import comp3607.group10.telemetry.report.BackupStore;
import comp3607.group10.telemetry.report.JsonReportGenerator;
import comp3607.group10.telemetry.report.ReportExporter;

public class CommandsTest {
    Path dir;

    private ManualClock clock;
    private TelemetrySession session;
    private TelemetryRecorder recorder;
    private ReportExporter exporter;
    private BackupStore backup;

    @BeforeEach
    public void setUp() throws Exception {
        dir = Files.createTempDirectory("telemetry-test");
        clock = new ManualClock(1000000L);
        TelemetryHub hub = new TelemetryHub();
        session = new TelemetrySession("student-x", clock.nowMillis());
        hub.register(session);
        recorder = new TelemetryRecorder(hub, session, new ClassTracker(), new EditCoalescer());
        JsonReportGenerator json = new JsonReportGenerator(new StatisticsCalculator());
        exporter = new ReportExporter(Arrays.asList(json), dir, ZoneOffset.UTC);
        backup = new BackupStore(dir, json);
    }

    @Test
    public void recordingCommandsReachTheRecorder() throws Exception {
        new BaselineClassesCommand(recorder, Arrays.asList(new ClassSnapshot("A", true, false, Arrays.asList()))).execute();
        new ClassStateCommand(recorder, new ClassSnapshot("B", false, false, Arrays.asList()), 1L).execute();
        new EditCommand(recorder, "B", 3, 0, 2L).execute();
        new RecordEventCommand(recorder, new TelemetryEvent(EventType.COMPILE_STARTED, 3L, "B")).execute();
        new ClassRenamedCommand(recorder, new TelemetryEvent(EventType.CLASS_RENAMED, 4L, "C", TestData.details(DetailKeys.OLD_NAME, "B"))).execute();
        new ClassDeletedCommand(recorder, new TelemetryEvent(EventType.CLASS_DELETED, 5L, "C")).execute();

        assertEquals(Arrays.asList(EventType.CLASS_CREATED, EventType.EDIT_BURST, EventType.COMPILE_STARTED, EventType.CLASS_RENAMED, EventType.CLASS_DELETED), TestData.typesOf(session.getEvents()));
    }

    @Test
    public void exportWritesReportsButKeepsTheSessionOpen() throws Exception {
        CollectedFiles exported = new CollectedFiles();
        clock.advanceSeconds(30);
        new ExportReportsCommand(exporter, session, clock, exported).execute();
        assertEquals(1, exported.files.size());
        assertTrue(Files.exists(exported.files.get(0)));
        assertTrue(!session.isEnded());
    }

    @Test
    public void endSessionWritesReportsAndRemovesTheBackup() throws Exception {
        backup.save(session, clock.nowMillis());
        assertTrue(Files.exists(backup.backupFile()));

        CollectedFiles exported = new CollectedFiles();
        clock.advanceSeconds(60);
        new EndSessionCommand(recorder, new TelemetryEvent(EventType.SESSION_ENDED, clock.nowMillis(), null),exporter, backup, exported).execute();

        assertTrue(session.isEnded());
        assertEquals(1, exported.files.size());
        assertTrue(!(Files.exists(backup.backupFile())));
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
*/
