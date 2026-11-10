package comp3607.group10.telemetry.report;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import comp3607.group10.telemetry.TestData;
import comp3607.group10.telemetry.SampleSessions;
import comp3607.group10.telemetry.analytics.StatisticsCalculator;
import comp3607.group10.telemetry.core.TelemetrySession;
import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class PdfReportGeneratorTest {
    Path dir;

    private PdfReportGenerator generator;

    @BeforeEach
    public void setUp() throws Exception {
        dir = Files.createTempDirectory("telemetry-test");
        generator = new PdfReportGenerator(new StatisticsCalculator(), ZoneOffset.UTC, "1.0.0");
    }

    @Test
    public void typicalReportContainsEverySection() throws IOException {
        String text = render(SampleSessions.typicalSession(), SampleSessions.at(510));
        assertTrue(text.contains("BLUEJ CODING SESSION REPORT"));
        assertTrue(text.contains("student-1234abcd"));
        assertTrue(text.contains("BlueJ project: Shapes"));
        assertTrue(text.contains("ACTIVE CODING TIME VS. IDLE TIME"));
        assertTrue(text.contains("Active coding time 3 min 30 s 41%"));
        assertTrue(text.contains("Idle time 5 min 00 s 59%"));
        assertTrue(text.contains("FREQUENCY BREAKDOWN OF COMPILATION ERRORS"));
        assertTrue(text.contains("Missing semicolon"));
        assertTrue(text.contains("50%"));
        assertTrue(text.contains("PROGRESSION TIMELINE OF CLASS CREATIONS AND METHOD DEFINITIONS"));
        assertTrue(text.contains("Method Circle.area() defined"));
        assertTrue(text.contains("Methods defined"));
        assertTrue(!text.contains("Session Summary"));
        assertTrue(text.contains("Page 1 of"));
    }

    @Test
    public void reportNeverExceedsTwoPagesEvenForAHugeSession() throws IOException {
        TelemetrySession session = new TelemetrySession("s", 0L);
        for (int i = 0; i < 500; i++) {
            session.update(new TelemetryEvent(EventType.CLASS_CREATED, i * 1000L, "Class" + i));
            session.update(new TelemetryEvent(EventType.COMPILE_ERROR, i * 1000L, "Class" + i, TestData.details(DetailKeys.ERROR_CODE, "CODE_" + (i % 20), DetailKeys.MESSAGE, "message " + i)));
        }
        Path file = generator.generate(session, 600000L, dir.resolve("big.pdf"));
        PDDocument pdf = Loader.loadPDF(file.toFile());
        int pages = pdf.getNumberOfPages();
        String text = new PDFTextStripper().getText(pdf);
        pdf.close();
        assertTrue(pages <= PdfReportGenerator.MAX_PAGES);
        assertTrue(text.contains("entries are listed in the JSON file"));
    }

    @Test
    public void emptySessionStillProducesAReport() throws IOException {
        String text = render(new TelemetrySession("s", 0L), 0L);
        assertTrue(text.contains("No compilation errors were recorded"));
        assertTrue(text.contains("No classes or methods were created"));
        assertTrue(text.contains("Idle time 0 min 00 s 0%"));
    }

    @Test
    public void unusualCharactersInMessagesDoNotBreakThePdf() throws IOException {
        TelemetrySession session = new TelemetrySession("s", 0L);
        session.update(new TelemetryEvent(EventType.CLASS_CREATED, 1L, "Café\tShop"));
        assertTrue(render(session, 10L).contains("Class Caf? Shop created"));
    }

    @Test
    public void formattingHelpers() {
        assertEquals("2 min 05 s", PdfReportGenerator.minutes(125));
        assertEquals("50%", PdfReportGenerator.percent(0.5));
        assertEquals("Missing semicolon", PdfReportGenerator.readableName("MISSING_SEMICOLON"));
        assertEquals("", PdfReportGenerator.readableName(""));
        assertEquals("25%", PdfReportGenerator.share(1, 4));
        assertEquals("0%", PdfReportGenerator.share(0, 0));
        assertEquals("BlueJ project: not recorded", PdfReportGenerator.projectLine(new ArrayList<String>()));
        assertEquals("BlueJ projects: Shapes, Bank", PdfReportGenerator.projectLine(Arrays.asList("Shapes", "Bank")));
        assertEquals("pdf", generator.fileExtension());
    }

    private String render(TelemetrySession session, long end) throws IOException {
        Path file = generator.generate(session, end, dir.resolve("report.pdf"));
        PDDocument pdf = Loader.loadPDF(file.toFile());
        int pages = pdf.getNumberOfPages();
        String text = new PDFTextStripper().getText(pdf);
        pdf.close();
        assertTrue(pages <= PdfReportGenerator.MAX_PAGES);
        return text;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Template, Week 6 lecture notes.
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Template Method Design Pattern. https://sourcemaking.com/design_patterns/template_method
The Apache Software Foundation. Apache PDFBox 3.0. https://pdfbox.apache.org/
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
*/
