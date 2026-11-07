package comp3607.group10.telemetry.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class TelemetryEvent {
    private final EventType type;
    private final long timestampMillis;
    private final String className;
    private final Map<String, String> details;

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

    public TelemetryEvent(EventType type, long timestampMillis, String className) {
        this(type, timestampMillis, className, null);
    }

    public EventType getType() {
        return type;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public String getClassName() {
        return className;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public String getDetail(String key) {
        return details.get(key);
    }

    public TelemetryEvent withDetail(String key, String value) {
        Map<String, String> copy = new LinkedHashMap<String, String>(details);
        copy.put(key, value);
        return new TelemetryEvent(type, timestampMillis, className, copy);
    }

    public boolean is(EventType other) {
        return type == other;
    }

    @Override
    public String toString() {
        return type + "@" + timestampMillis + " " + className + " " + details;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
