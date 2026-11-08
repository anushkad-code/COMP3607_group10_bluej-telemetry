package comp3607.group10.telemetry.core;

import java.util.ArrayList;
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

public class ClassTrackerTest {
    private ClassTracker tracker;

    @BeforeEach
    public void setUp() {
        tracker = new ClassTracker();
    }

    @Test
    public void firstSightingOfAClassIsACreation() {
        List<TelemetryEvent> events = tracker.stateChanged(uncompiled("Circle"), 100L);
        assertEquals(1, events.size());
        assertEquals(EventType.CLASS_CREATED, events.get(0).getType());
        assertEquals("Circle", events.get(0).getClassName());
    }

    @Test
    public void baselineClassesAreNotReportedAsNew() {
        tracker.baseline(compiledWith("Circle", Arrays.asList("area")));
        assertTrue(tracker.stateChanged(compiledWith("Circle", Arrays.asList("area")), 100L).isEmpty());
    }

    @Test
    public void becomingUncompiledIsOneModificationUntilTheNextCompile() {
        tracker.baseline(compiled("Circle"));
        assertEquals(EventType.CLASS_MODIFIED, tracker.stateChanged(uncompiled("Circle"), 1L).get(0).getType());
        assertTrue(tracker.stateChanged(uncompiled("Circle"), 2L).isEmpty());
        tracker.stateChanged(compiled("Circle"), 3L);
        assertEquals(1, tracker.stateChanged(uncompiled("Circle"), 4L).size());
    }

    @Test
    public void failedCompileIsNotAModification() {
        tracker.baseline(compiled("Circle"));
        assertTrue(tracker.stateChanged(new ClassSnapshot("Circle", false, true, Arrays.asList()), 1L).isEmpty());
    }

    @Test
    public void newMethodsAppearAfterASuccessfulCompile() {
        tracker.baseline(compiledWith("Circle", Arrays.asList("area")));
        List<TelemetryEvent> events = tracker.stateChanged(compiledWith("Circle", Arrays.asList("area", "perimeter")), 50L);
        assertEquals(1, events.size());
        assertEquals(EventType.METHOD_DEFINED, events.get(0).getType());
        assertEquals("perimeter", events.get(0).getDetail(DetailKeys.METHOD));
    }

    @Test
    public void aDeletedThenReAddedMethodCountsAgain() {
        tracker.baseline(compiledWith("Circle", Arrays.asList("area")));
        tracker.stateChanged(compiled("Circle"), 1L);
        assertEquals(1, tracker.stateChanged(compiledWith("Circle", Arrays.asList("area")), 2L).size());
    }

    @Test
    public void newCompiledClassReportsCreationAndItsMethods() {
        List<TelemetryEvent> events = tracker.stateChanged(compiledWith("Square", Arrays.asList("side")), 9L);
        assertEquals(Arrays.asList(EventType.CLASS_CREATED, EventType.METHOD_DEFINED), TestData.typesOf(events));
    }

    @Test
    public void renameKeepsHistoryAndRemoveForgets() {
        tracker.baseline(compiledWith("Rect", Arrays.asList("area")));
        tracker.renamed("Square", "Rect");
        assertTrue(tracker.isKnown("Square"));
        assertTrue(!tracker.isKnown("Rect"));
        assertTrue(tracker.stateChanged(compiledWith("Square", Arrays.asList("area")), 1L).isEmpty());
        tracker.removed("Square");
        assertTrue(!tracker.isKnown("Square"));
        tracker.renamed("Ghost", "NeverSeen");
        assertTrue(tracker.isKnown("Ghost"));
    }

    private static ClassSnapshot compiled(String name) {
        return new ClassSnapshot(name, true, false, new ArrayList<String>());
    }

    private static ClassSnapshot compiledWith(String name, List<String> methods) {
        return new ClassSnapshot(name, true, false, methods);
    }

    private static ClassSnapshot uncompiled(String name) {
        return new ClassSnapshot(name, false, false, Arrays.asList());
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
*/
