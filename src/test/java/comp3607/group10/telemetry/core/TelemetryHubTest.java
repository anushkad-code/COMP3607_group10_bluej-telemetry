package comp3607.group10.telemetry.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import comp3607.group10.telemetry.RecordingObserver;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class TelemetryHubTest {
    @Test
    public void everyRegisteredObserverIsNotifiedInOrder() {
        TelemetryHub hub = new TelemetryHub();
        RecordingObserver first = new RecordingObserver();
        RecordingObserver second = new RecordingObserver();
        hub.register(first);
        hub.register(second);
        hub.notifyObservers(new TelemetryEvent(EventType.CLASS_CREATED, 1L, "A"));
        assertEquals(1, first.received.size());
        assertEquals(1, second.received.size());
        assertEquals(2, hub.observerCount());
    }

    @Test
    public void unregisteredObserversStopReceivingEvents() {
        TelemetryHub hub = new TelemetryHub();
        RecordingObserver observer = new RecordingObserver();
        hub.register(observer);
        hub.notifyObservers(new TelemetryEvent(EventType.CLASS_CREATED, 1L, "A"));
        hub.unregister(observer);
        hub.notifyObservers(new TelemetryEvent(EventType.CLASS_CREATED, 2L, "B"));
        assertEquals(1, observer.received.size());
        assertEquals(0, hub.observerCount());
    }

    @Test
    public void nullObserverIsRejected() {
        try {
            new TelemetryHub().register(null);
            assertTrue(false, "a null observer should be rejected");
        } catch (NullPointerException expected) {
            assertNotNull(expected);
        }
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Observer Design Pattern. https://sourcemaking.com/design_patterns/observer
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. The Java Tutorials: Exceptions (Catching and Handling Exceptions). https://docs.oracle.com/javase/tutorial/essential/exceptions/handling.html
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
*/
