package comp3607.group10.telemetry.report;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import comp3607.group10.telemetry.SampleSessions;
import comp3607.group10.telemetry.TestData;
import comp3607.group10.telemetry.analytics.StatisticsCalculator;
import comp3607.group10.telemetry.core.TelemetrySession;
import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class ReportExporterTest {
    Path dir;

    @BeforeEach
    public void createTemporaryFolder() throws Exception {
        dir = Files.createTempDirectory("telemetry-test");
    }

    @Test
    public void writesOneFilePerFormatWithMatchingNames() throws IOException {
        StatisticsCalculator calculator = new StatisticsCalculator();
        ReportExporter exporter = new ReportExporter(Arrays.asList(new JsonReportGenerator(calculator),
                new PdfReportGenerator(calculator, ZoneOffset.UTC, "test")), dir, ZoneOffset.UTC);

        List<Path> files = exporter.export(SampleSessions.typicalSession(), SampleSessions.at(510));

        assertEquals(2, files.size());
        assertEquals("Shapes-telemetry-20261026-120830.json", files.get(0).getFileName().toString());
        assertEquals("Shapes-telemetry-20261026-120830.pdf", files.get(1).getFileName().toString());
        for (Path file : files) {
            assertTrue(Files.exists(file));
        }
        assertEquals(dir, exporter.getOutputDirectory());
    }

    @Test
    public void reportsGoIntoTheOpenedProjectFolder() throws IOException {
        Path project = Files.createDirectories(dir.resolve("UseCase2"));
        TelemetrySession session = SampleSessions.typicalSession();
        session.setProjectFolder(project);
        StatisticsCalculator calculator = new StatisticsCalculator();
        ReportExporter exporter = new ReportExporter(Arrays.asList(new JsonReportGenerator(calculator),
                new PdfReportGenerator(calculator, ZoneOffset.UTC, "test")), dir, ZoneOffset.UTC);

        List<Path> files = exporter.export(session, SampleSessions.at(510));

        assertEquals(project, files.get(0).getParent());
        assertEquals(project, files.get(1).getParent());
    }

    @Test
    public void sessionWithoutAProjectUsesTheDefaultFolderAndTime() {
        ReportExporter exporter = new ReportExporter(new ArrayList<ReportGenerator>(), dir, ZoneOffset.UTC);
        TelemetrySession session = new TelemetrySession("student-x", 0L);
        assertEquals(dir, exporter.folderFor(session));
        assertEquals("telemetry-19700101-000000", exporter.baseName(session, 0L));
    }

    @Test
    public void lastOpenedProjectNamesTheFileAndUnsafeCharactersAreReplaced() {
        List<TelemetryEvent> events = new ArrayList<TelemetryEvent>();
        events.add(new TelemetryEvent(EventType.PROJECT_OPENED, 1L, null, TestData.details(DetailKeys.PROJECT, "First")));
        events.add(new TelemetryEvent(EventType.PROJECT_CLOSED, 2L, null, TestData.details(DetailKeys.PROJECT, "Other")));
        events.add(new TelemetryEvent(EventType.PROJECT_OPENED, 3L, null, TestData.details(DetailKeys.PROJECT, "My Project/2")));
        events.add(new TelemetryEvent(EventType.PROJECT_OPENED, 4L, null, TestData.details(DetailKeys.PROJECT, " ")));
        assertEquals("My Project/2", ReportExporter.lastProjectName(events));
        assertEquals("My_Project_2", ReportExporter.safeFileName("My Project/2"));
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
