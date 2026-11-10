package comp3607.group10.telemetry;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import comp3607.group10.telemetry.command.RecordEventCommand;
import comp3607.group10.telemetry.core.TelemetryEventFactory;

public class TelemetryServiceTest {
    Path dir;

    @BeforeEach
    public void createTemporaryFolder() throws Exception {
        dir = Files.createTempDirectory("telemetry-test");
    }

    @Test
    public void fullSessionProducesJsonAndPdfAndCleansUpTheBackup() throws Exception {
        ManualClock clock = new ManualClock(SampleSessions.START);
        TelemetryService service = new TelemetryService(dir, "seed", clock, ZoneOffset.UTC, 0);
        assertNull(service.start());

        TelemetryEventFactory factory = service.getEventFactory();
        clock.advanceSeconds(10);
        service.getCommandQueue().submit(new RecordEventCommand(service.getRecorder(), factory.compileStarted(Arrays.asList("A.java"))));
        service.getCommandQueue().submit(new RecordEventCommand(service.getRecorder(), factory.compileError("A.java", 3, "';' expected")));
        service.getCommandQueue().submit(new RecordEventCommand(service.getRecorder(), factory.compileFailed(Arrays.asList("A.java"))));
        clock.advanceSeconds(20);

        CollectedFiles manual = new CollectedFiles();
        assertTrue(service.exportNow(manual));

        CollectedFiles finalReports = new CollectedFiles();
        clock.advanceSeconds(30);
        assertTrue(service.shutdown(5, TimeUnit.SECONDS, finalReports));

        assertEquals(2, manual.files.size());
        assertEquals(2, finalReports.files.size());
        JsonObject metadata = JsonParser.parseString(Files.readString(finalReports.files.get(0)))
                .getAsJsonObject().getAsJsonObject("metadata");
        assertEquals(1, metadata.get("failedCompilations").getAsInt());
        assertEquals(60, metadata.get("sessionDurationSec").getAsInt());
        assertTrue(metadata.get("studentID").getAsString().startsWith("student-"));
        assertTrue(Files.size(finalReports.files.get(1)) > 0);
        assertTrue(!Files.exists(dir.resolve("telemetry-backup.json")), "clean shutdown removes the backup");
        assertEquals(2, service.getHub().observerCount());
        assertEquals(dir, service.getOutputDirectory());
        assertEquals(clock, service.getClock());
    }

    @Test
    public void aBackupLeftByACrashIsRecoveredAtStartup() throws Exception {
        Files.writeString(dir.resolve("telemetry-backup.json"), "{}");
        TelemetryService service = new TelemetryService(dir, "seed", new ManualClock(0L), ZoneOffset.UTC, 0);
        Path recovered = service.start();
        assertNotNull(recovered);
        assertTrue(Files.exists(recovered));
        service.shutdown(5, TimeUnit.SECONDS, new CollectedFiles());
    }

    @Test
    public void defaultConstructorUsesTheRealClock() throws Exception {
        TelemetryService service = new TelemetryService(dir, "seed");
        long now = System.currentTimeMillis();
        assertTrue(Math.abs(service.getClock().nowMillis() - now) < 5000);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
Google. Gson User Guide. https://github.com/google/gson/blob/main/UserGuide.md
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
*/
