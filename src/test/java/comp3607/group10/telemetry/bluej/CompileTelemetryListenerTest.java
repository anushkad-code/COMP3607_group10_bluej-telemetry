package comp3607.group10.telemetry.bluej;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bluej.extensions2.event.CompileEvent;

import comp3607.group10.telemetry.TestData;
import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class CompileTelemetryListenerTest {
    private static final File[] CIRCLE = {new File("/home/jane/project/Circle.java")};

    private ListenerTestSupport support;
    private CompileTelemetryListener listener;

    @BeforeEach
    public void setUp() {
        support = new ListenerTestSupport();
        listener = new CompileTelemetryListener(support.context);
    }

    @Test
    public void aFailedUserCompileIsRecordedWithItsError() throws Exception {
        listener.compileStarted(event(CompileEvent.EventType.COMPILE_START_EVENT, true));
        CompileEvent error = event(CompileEvent.EventType.COMPILE_ERROR_EVENT, true);
        error.setErrorLineNumber(12);
        error.setErrorMessage("cannot find symbol - variable radius");
        listener.compileError(error);
        listener.compileFailed(event(CompileEvent.EventType.COMPILE_FAILED_EVENT, true));

        List<TelemetryEvent> events = support.runQueued();
        assertEquals(Arrays.asList(EventType.COMPILE_STARTED, EventType.COMPILE_ERROR, EventType.COMPILE_FAILED),
                TestData.typesOf(events));
        TelemetryEvent recorded = events.get(1);
        assertEquals("Circle", recorded.getClassName());
        assertEquals("12", recorded.getDetail(DetailKeys.LINE));
        assertEquals("CANNOT_FIND_SYMBOL", recorded.getDetail(DetailKeys.ERROR_CODE));
    }

    @Test
    public void onlyTheFileNameIsKeptNotThePath() throws Exception {
        listener.compileSucceeded(event(CompileEvent.EventType.COMPILE_DONE_EVENT, true));
        TelemetryEvent recorded = support.runQueued().get(0);
        assertEquals("Circle.java", recorded.getDetail(DetailKeys.FILE));
        for (String value : recorded.getDetails().values()) {
            assertTrue(!value.contains("jane"));
        }
    }

    @Test
    public void backgroundCompilesAndWarningsAreIgnored() throws Exception {
        listener.compileStarted(event(CompileEvent.EventType.COMPILE_START_EVENT, false));
        listener.compileFailed(event(CompileEvent.EventType.COMPILE_FAILED_EVENT, false));
        listener.compileWarning(event(CompileEvent.EventType.COMPILE_WARNING_EVENT, true));
        assertTrue(support.queued.isEmpty());
    }

    @Test
    public void anEventWithoutFilesStillWorks() throws Exception {
        CompileEvent error = new CompileEvent(CompileEvent.EventType.COMPILE_ERROR_EVENT, true, null);
        listener.compileError(error);
        TelemetryEvent recorded = support.runQueued().get(0);
        assertEquals(EventType.COMPILE_ERROR, recorded.getType());
    }

    private static CompileEvent event(CompileEvent.EventType type, boolean userGenerated) {
        return new CompileEvent(type, userGenerated, CIRCLE);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
BlueJ. Writing Extensions for BlueJ 5+. https://www.bluej.org/extensions/writingextensions2.html
BlueJ / King's College London. BlueJ source code, Extensions API package bluej.extensions2. https://github.com/k-pet-group/BlueJ-Greenfoot
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
