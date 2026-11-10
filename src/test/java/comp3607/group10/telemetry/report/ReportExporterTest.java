package comp3607.group10.telemetry.report;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import comp3607.group10.telemetry.SampleSessions;
import comp3607.group10.telemetry.analytics.StatisticsCalculator;

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
        assertEquals("telemetry-student-1234abcd-20261026-120830.json", files.get(0).getFileName().toString());
        assertEquals("telemetry-student-1234abcd-20261026-120830.pdf", files.get(1).getFileName().toString());
        for (Path file : files) {
            assertTrue(Files.exists(file));
        }
        assertEquals(dir, exporter.getOutputDirectory());
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
