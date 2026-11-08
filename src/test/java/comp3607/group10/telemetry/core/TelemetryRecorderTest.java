package comp3607.group10.telemetry.core;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import comp3607.group10.telemetry.TestData;
import comp3607.group10.telemetry.model.ClassSnapshot;
import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class TelemetryRecorderTest {
    private TelemetrySession session;
    private TelemetryRecorder recorder;

    @BeforeEach
    public void setUp() {
        TelemetryHub hub = new TelemetryHub();
        session = new TelemetrySession("student-x", 0L);
        hub.register(session);
        recorder = new TelemetryRecorder(hub, session, new ClassTracker(), new EditCoalescer(2000));
    }

    @Test
    public void compileStartsStoreTheTimeSinceThePreviousCompile() {
        recorder.record(new TelemetryEvent(EventType.COMPILE_STARTED, 10000L, "A"));
        recorder.record(new TelemetryEvent(EventType.COMPILE_STARTED, 25000L, "A"));
        List<TelemetryEvent> events = session.getEvents();
        assertNull(events.get(0).getDetail(DetailKeys.MS_SINCE_PREVIOUS_COMPILE));
        assertEquals("15000", events.get(1).getDetail(DetailKeys.MS_SINCE_PREVIOUS_COMPILE));
    }

    @Test
    public void anOpenEditBurstIsWrittenBeforeTheNextEvent() {
        recorder.edit("A", 1, 0, 1000L);
        recorder.edit("A", 1, 0, 1200L);
        assertTrue(session.getEvents().isEmpty());
        recorder.record(new TelemetryEvent(EventType.COMPILE_STARTED, 5000L, "A"));
        assertEquals(Arrays.asList(EventType.EDIT_BURST, EventType.COMPILE_STARTED), types());
    }

    @Test
    public void editsToAnotherClassCloseTheOldBurst() {
        recorder.edit("A", 1, 0, 1000L);
        recorder.edit("B", 1, 0, 1100L);
        assertEquals(Arrays.asList(EventType.EDIT_BURST), types());
    }

    @Test
    public void classStateChangesBecomeLifecycleEvents() {
        recorder.baseline(Arrays.asList(new ClassSnapshot("A", true, false, Arrays.asList("run"))));
        recorder.classStateChanged(new ClassSnapshot("A", false, false, Arrays.asList()), 1L);
        recorder.classStateChanged(new ClassSnapshot("A", true, false, Arrays.asList("run", "stop")), 2L);
        recorder.classStateChanged(new ClassSnapshot("B", false, false, Arrays.asList()), 3L);
        assertEquals(Arrays.asList(EventType.CLASS_MODIFIED, EventType.METHOD_DEFINED, EventType.CLASS_CREATED), types());
    }

    @Test
    public void renameAndDeleteAreRecorded() {
        recorder.classRenamed(new TelemetryEvent(EventType.CLASS_RENAMED, 1L, "B", TestData.details(DetailKeys.OLD_NAME, "A")));
        recorder.classDeleted(new TelemetryEvent(EventType.CLASS_DELETED, 2L, "B"));
        assertEquals(Arrays.asList(EventType.CLASS_RENAMED, EventType.CLASS_DELETED), types());
    }

    @Test
    public void endSessionHappensOnlyOnce() {
        recorder.endSession(new TelemetryEvent(EventType.SESSION_ENDED, 9000L, null));
        recorder.endSession(new TelemetryEvent(EventType.SESSION_ENDED, 9500L, null));
        assertEquals(1, session.getEvents().size());
        assertEquals(9000L, (long) session.getEndMillis());
        assertTrue(session.isEnded());
        assertEquals(session, recorder.getSession());
    }

    private List<EventType> types() {
        return TestData.typesOf(session.getEvents());
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
*/
