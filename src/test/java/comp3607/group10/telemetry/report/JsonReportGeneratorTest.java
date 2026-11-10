package comp3607.group10.telemetry.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import comp3607.group10.telemetry.SampleSessions;
import comp3607.group10.telemetry.analytics.StatisticsCalculator;
import comp3607.group10.telemetry.core.TelemetrySession;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class JsonReportGeneratorTest {
    Path dir;

    private JsonReportGenerator generator;
    private JsonObject root;

    @BeforeEach
    public void setUp() throws Exception {
        dir = Files.createTempDirectory("telemetry-test");
        generator = new JsonReportGenerator(new StatisticsCalculator());
        root = write(SampleSessions.typicalSession(), SampleSessions.at(510));
    }

    @Test
    public void outputIsValidJsonWithMetadataAndEvents() {
        assertEquals(JsonReportGenerator.SCHEMA_VERSION, root.get("schemaVersion").getAsInt());
        assertTrue(root.get("metadata").isJsonObject());
        assertTrue(root.get("events").isJsonArray());
    }

    @Test
    public void metadataHasExactlyTheKeysPromisedInTheScope() {
        Set<String> keys = root.getAsJsonObject("metadata").keySet();
        assertEquals(new HashSet<String>(Arrays.asList("studentID", "sessionStart", "sessionEnd", "sessionDurationSec", "totalCompilations", "successfulCompilations", "failedCompilations", "totalRuns", "runtimeExceptions", "activeTimeSec", "idleTimeSec", "totalEdits", "typingCadenceCpm", "averageCompileIntervalSec")), keys);
    }

    @Test
    public void metadataValuesMatchTheStatistics() {
        JsonObject m = root.getAsJsonObject("metadata");
        assertEquals("student-1234abcd", m.get("studentID").getAsString());
        assertEquals("2026-10-26T12:00:00.000Z", m.get("sessionStart").getAsString());
        assertEquals("2026-10-26T12:08:30.000Z", m.get("sessionEnd").getAsString());
        assertEquals(510, m.get("sessionDurationSec").getAsLong());
        assertEquals(3, m.get("totalCompilations").getAsInt());
        assertEquals(2, m.get("successfulCompilations").getAsInt());
        assertEquals(1, m.get("failedCompilations").getAsInt());
        assertEquals(2, m.get("totalRuns").getAsInt());
        assertEquals(1, m.get("runtimeExceptions").getAsInt());
        assertEquals(210, m.get("activeTimeSec").getAsLong());
        assertEquals(300, m.get("idleTimeSec").getAsLong());
        assertEquals(120.0, m.get("typingCadenceCpm").getAsDouble());
    }

    @Test
    public void everyEventHasTypeTimestampClassNameAndDetails() {
        JsonArray events = root.getAsJsonArray("events");
        assertEquals(SampleSessions.typicalSession().getEvents().size(), events.size());
        for (int i = 0; i < events.size(); i++) {
            JsonObject event = events.get(i).getAsJsonObject();
            assertEquals(new HashSet<String>(Arrays.asList("type", "timestamp", "className", "details")), event.keySet());
        }
        JsonObject error = events.get(5).getAsJsonObject();
        assertEquals("COMPILE_ERROR", error.get("type").getAsString());
        assertEquals("2026-10-26T12:01:01.000Z", error.get("timestamp").getAsString());
        assertEquals("Circle", error.get("className").getAsString());
        assertEquals("7", error.getAsJsonObject("details").get("line").getAsString());
    }

    @Test
    public void missingClassNameIsWrittenAsNullNotLeftOut() {
        assertTrue(root.getAsJsonArray("events").get(0).getAsJsonObject().get("className").isJsonNull());
    }

    @Test
    public void eventsAreWrittenInTimeOrder() throws IOException {
        TelemetrySession session = new TelemetrySession("s", 0L);
        session.update(new TelemetryEvent(EventType.PROGRAM_RUN, 2000L, "B"));
        session.update(new TelemetryEvent(EventType.EDIT_BURST, 1000L, "A"));
        JsonArray events = write(session, 3000L).getAsJsonArray("events");
        List<String> types = new ArrayList<String>();
        for (int i = 0; i < events.size(); i++) {
            types.add(events.get(i).getAsJsonObject().get("type").getAsString());
        }
        assertEquals(Arrays.asList("EDIT_BURST", "PROGRAM_RUN"), types);
    }

    @Test
    public void noTemporaryFileIsLeftBehind() throws IOException {
        for (File file : dir.toFile().listFiles()) {
            assertTrue(!file.getName().endsWith(".tmp"));
        }
        assertEquals("json", generator.fileExtension());
    }

    @Test
    public void missingFoldersAreCreated() throws IOException {
        Path nested = dir.resolve("a/b/report.json");
        generator.generate(SampleSessions.typicalSession(), SampleSessions.at(510), nested);
        assertTrue(Files.exists(nested));
    }

    private JsonObject write(TelemetrySession session, long end) throws IOException {
        Path file = generator.generate(session, end, dir.resolve("report.json"));
        return JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Template, Week 6 lecture notes.
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Template Method Design Pattern. https://sourcemaking.com/design_patterns/template_method
Google. Gson User Guide. https://github.com/google/gson/blob/main/UserGuide.md
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
*/
