package comp3607.group10.telemetry.analytics;

import comp3607.group10.telemetry.model.EventType;

public class TimelineEntry {
    private final long timestampMillis;
    private final EventType type;
    private final String className;
    private final String methodName;

    public TimelineEntry(long timestampMillis, EventType type, String className, String methodName) {
        this.timestampMillis = timestampMillis;
        this.type = type;
        this.className = className;
        this.methodName = methodName;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public EventType getType() {
        return type;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String describe() {
        if (methodName == null) {
            return "Class " + className + " created";
        }
        return "Method " + className + "." + methodName + "() defined";
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
*/
