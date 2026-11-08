package comp3607.group10.telemetry.core;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import comp3607.group10.telemetry.ManualClock;
import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.RunOutcome;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class TelemetryEventFactoryTest {
    private ManualClock clock;
    private TelemetryEventFactory factory;

    @BeforeEach
    public void setUp() {
        clock = new ManualClock(5000L);
        factory = new TelemetryEventFactory(clock, new ErrorCodeClassifier());
    }

    @Test
    public void compileErrorKeepsMessageFileLineAndCode() {
        TelemetryEvent event = factory.compileError("Circle.java", 7, "';' expected");
        assertEquals(EventType.COMPILE_ERROR, event.getType());
        assertEquals(5000L, event.getTimestampMillis());
        assertEquals("Circle", event.getClassName());
        assertEquals("Circle.java", event.getDetail(DetailKeys.FILE));
        assertEquals("7", event.getDetail(DetailKeys.LINE));
        assertEquals("';' expected", event.getDetail(DetailKeys.MESSAGE));
        assertEquals("MISSING_SEMICOLON", event.getDetail(DetailKeys.ERROR_CODE));
    }

    @Test
    public void compileErrorWithoutMessageIsStillRecorded() {
        TelemetryEvent event = factory.compileError("A.java", 1, null);
        assertEquals("", event.getDetail(DetailKeys.MESSAGE));
        assertEquals(ErrorCodeClassifier.UNKNOWN, event.getDetail(DetailKeys.ERROR_CODE));
    }

    @Test
    public void singleFileCompileNamesTheClass() {
        TelemetryEvent event = factory.compileStarted(Arrays.asList("Square.java"));
        assertEquals(EventType.COMPILE_STARTED, event.getType());
        assertEquals("Square", event.getClassName());
        assertEquals("1", event.getDetail(DetailKeys.FILE_COUNT));
    }

    @Test
    public void multiFileCompileHasNoSingleClass() {
        TelemetryEvent event = factory.compileSucceeded(Arrays.asList("A.java", "B.java"));
        assertNull(event.getClassName());
        assertEquals("2", event.getDetail(DetailKeys.FILE_COUNT));
        assertEquals("A.java,B.java", event.getDetail(DetailKeys.FILE));
        assertEquals(EventType.COMPILE_FAILED, factory.compileFailed(Arrays.asList("A.java")).getType());
    }

    @Test
    public void normalRunIsAProgramRunWithSummary() {
        TelemetryEvent event = factory.programRun("Circle", "main", RunOutcome.NORMAL);
        assertEquals(EventType.PROGRAM_RUN, event.getType());
        assertEquals("NORMAL", event.getDetail(DetailKeys.EXIT_TYPE));
        assertEquals("main on Circle finished normally", event.getDetail(DetailKeys.OUTPUT_SUMMARY));
    }

    @Test
    public void exceptionRunIsARuntimeException() {
        TelemetryEvent event = factory.programRun("Circle", null, RunOutcome.EXCEPTION);
        assertEquals(EventType.RUNTIME_EXCEPTION, event.getType());
        assertEquals("constructor", event.getDetail(DetailKeys.METHOD));
    }

    @Test
    public void projectAndSessionEvents() {
        assertEquals("Shapes", factory.projectOpened("Shapes").getDetail(DetailKeys.PROJECT));
        assertEquals(EventType.PROJECT_CLOSED, factory.projectClosed("Shapes").getType());
        assertEquals(EventType.SESSION_STARTED, factory.sessionStarted().getType());
        clock.advanceSeconds(1);
        assertEquals(6000L, factory.sessionEnded().getTimestampMillis());
    }

    @Test
    public void classRenameAndDelete() {
        TelemetryEvent renamed = factory.classRenamed("Square", "Rect");
        assertEquals("Square", renamed.getClassName());
        assertEquals("Rect", renamed.getDetail(DetailKeys.OLD_NAME));
        assertEquals(EventType.CLASS_DELETED, factory.classDeleted("Square").getType());
    }

    @Test
    public void classNameIsTakenFromAnyFilePath() {
        assertEquals("Circle", TelemetryEventFactory.toClassName("C:\\work\\Circle.java"));
        assertEquals("Circle", TelemetryEventFactory.toClassName("/home/u/Circle.java"));
        assertEquals("README", TelemetryEventFactory.toClassName("README"));
        assertNull(TelemetryEventFactory.toClassName(null));
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
*/
