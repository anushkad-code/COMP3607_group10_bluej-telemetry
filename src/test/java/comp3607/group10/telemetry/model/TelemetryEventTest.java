package comp3607.group10.telemetry.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class TelemetryEventTest {
    @Test
    public void detailsAreCopiedSoLaterChangesDoNotLeakIn() {
        Map<String, String> details = new HashMap<String, String>();
        details.put("line", "3");
        TelemetryEvent event = new TelemetryEvent(EventType.COMPILE_ERROR, 10L, "Foo", details);
        details.put("line", "99");
        assertEquals("3", event.getDetail("line"));
    }

    @Test
    public void detailsCannotBeModified() {
        TelemetryEvent event = new TelemetryEvent(EventType.CLASS_CREATED, 10L, "Foo");
        try {
            event.getDetails().put("x", "y");
            assertTrue(false, "the details should be read-only");
        } catch (UnsupportedOperationException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void withDetailReturnsANewEventAndKeepsTheOriginal() {
        TelemetryEvent original = new TelemetryEvent(EventType.COMPILE_STARTED, 10L, "Foo");
        TelemetryEvent copy = original.withDetail("k", "v");
        assertNull(original.getDetail("k"));
        assertEquals("v", copy.getDetail("k"));
        assertEquals(original.getTimestampMillis(), copy.getTimestampMillis());
        assertEquals(EventType.COMPILE_STARTED, copy.getType());
    }

    @Test
    public void typeIsRequired() {
        try {
            new TelemetryEvent(null, 1L, "Foo");
            assertTrue(false, "an event without a type should be rejected");
        } catch (NullPointerException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void isComparesTheType() {
        TelemetryEvent event = new TelemetryEvent(EventType.PROGRAM_RUN, 1L, "Foo", null);
        assertTrue(event.is(EventType.PROGRAM_RUN));
        assertTrue(!event.is(EventType.RUNTIME_EXCEPTION));
        assertTrue(event.getDetails().isEmpty());
        assertTrue(event.toString().contains("PROGRAM_RUN"));
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. The Java Tutorials: Exceptions (Catching and Handling Exceptions). https://docs.oracle.com/javase/tutorial/essential/exceptions/handling.html
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
*/
