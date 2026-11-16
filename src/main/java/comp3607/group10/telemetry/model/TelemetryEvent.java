package comp3607.group10.telemetry.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
One telemetry event: its type, a millisecond timestamp, the class involved and a map of extra
details. Events cannot be changed after they are created.

@author Anushka Dwarika
*/
public class TelemetryEvent {
    /** The kind of event. */
    private final EventType type;
    /** When it happened, in epoch milliseconds. */
    private final long timestampMillis;
    /** The class involved, or null. */
    private final String className;
    /** Extra values, such as the error message. Read-only. */
    private final Map<String, String> details;

    /**
    Creates an event. The details are copied so later changes do not leak in.

    @param type the kind of event
    @param timestampMillis when it happened, in epoch milliseconds
    @param className the class involved, or null
    @param details extra values for the event, such as the error message
    */
    public TelemetryEvent(EventType type, long timestampMillis, String className, Map<String, String> details) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        this.type = type;
        this.timestampMillis = timestampMillis;
        this.className = className;
        Map<String, String> copy = new LinkedHashMap<String, String>();
        if (details != null) {
            copy.putAll(details);
        }
        this.details = Collections.unmodifiableMap(copy);
    }

    /**
    Creates an event with no details.

    @param type the kind of event
    @param timestampMillis when it happened, in epoch milliseconds
    @param className the class involved, or null
    */
    public TelemetryEvent(EventType type, long timestampMillis, String className) {
        this(type, timestampMillis, className, null);
    }

    /**
    Gives the kind of event.

    @return the type
    */
    public EventType getType() {
        return type;
    }

    /**
    Gives when it happened.

    @return epoch milliseconds
    */
    public long getTimestampMillis() {
        return timestampMillis;
    }

    /**
    Gives the class involved.

    @return the class name, or null
    */
    public String getClassName() {
        return className;
    }

    /**
    Gives the extra details.

    @return a read-only map
    */
    public Map<String, String> getDetails() {
        return details;
    }

    /**
    Gives one detail.

    @param key the detail name, one of the DetailKeys constants
    @return the value, or null if it is not set
    */
    public String getDetail(String key) {
        return details.get(key);
    }

    /**
    Gives a copy of this event with one more detail. This event is not changed.

    @param key the detail name, one of the DetailKeys constants
    @param value the detail value
    @return the new event
    */
    public TelemetryEvent withDetail(String key, String value) {
        Map<String, String> copy = new LinkedHashMap<String, String>(details);
        copy.put(key, value);
        return new TelemetryEvent(type, timestampMillis, className, copy);
    }

    /**
    Tells whether this event is of a given type.

    @param other the event type to compare with
    @return true if the types match
    */
    public boolean is(EventType other) {
        return type == other;
    }

    /**
    Gives a short description for debugging.

    @return the description
    */
    @Override
    public String toString() {
        return type + "@" + timestampMillis + " " + className + " " + details;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
