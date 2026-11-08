package comp3607.group10.telemetry.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class EditCoalescerTest {
    private EditCoalescer coalescer;

    @BeforeEach
    public void setUp() {
        coalescer = new EditCoalescer(2000);
    }

    @Test
    public void quickEditsToOneClassBecomeOneBurst() {
        assertNull(coalescer.edit("Circle", 1, 0, 1000));
        assertNull(coalescer.edit("Circle", 1, 0, 1500));
        assertNull(coalescer.edit("Circle", 0, 1, 3000));

        TelemetryEvent burst = coalescer.flush();
        assertNotNull(burst);
        assertEquals(EventType.EDIT_BURST, burst.getType());
        assertEquals(1000, burst.getTimestampMillis());
        assertEquals("3", burst.getDetail(DetailKeys.KEYSTROKES));
        assertEquals("2", burst.getDetail(DetailKeys.CHARS_INSERTED));
        assertEquals("1", burst.getDetail(DetailKeys.CHARS_DELETED));
        assertEquals("2000", burst.getDetail(DetailKeys.DURATION_MS));
        assertTrue(!coalescer.hasOpenBurst());
    }

    @Test
    public void aPauseLongerThanTheGapStartsANewBurst() {
        coalescer.edit("Circle", 1, 0, 1000);
        TelemetryEvent finished = coalescer.edit("Circle", 1, 0, 3001);
        assertNotNull(finished);
        assertEquals("1", finished.getDetail(DetailKeys.KEYSTROKES));
        assertTrue(coalescer.hasOpenBurst());
    }

    @Test
    public void switchingClassStartsANewBurst() {
        coalescer.edit("Circle", 1, 0, 1000);
        assertEquals("Circle", coalescer.edit("Square", 1, 0, 1100).getClassName());
        assertEquals("Square", coalescer.flush().getClassName());
    }

    @Test
    public void flushWithNothingOpenIsEmpty() {
        assertNull(new EditCoalescer().flush());
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
*/
