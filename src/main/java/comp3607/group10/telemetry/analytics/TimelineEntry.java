package comp3607.group10.telemetry.analytics;

import comp3607.group10.telemetry.model.EventType;

/**
One class created or method defined entry in the progression timeline.

@author Haeley Parbhoo
*/
public class TimelineEntry {
    /** When it happened, in epoch milliseconds. */
    private final long timestampMillis;
    /** CLASS_CREATED or METHOD_DEFINED. */
    private final EventType type;
    /** The class involved. */
    private final String className;
    /** The method involved, or null for a class entry. */
    private final String methodName;

    /**
    Creates a timeline entry.

    @param timestampMillis when it happened, in epoch milliseconds
    @param type the kind of event
    @param className the name of the class
    @param methodName the method name, or null for a class entry
    */
    public TimelineEntry(long timestampMillis, EventType type, String className, String methodName) {
        this.timestampMillis = timestampMillis;
        this.type = type;
        this.className = className;
        this.methodName = methodName;
    }

    /**
    Gives when it happened.

    @return epoch milliseconds
    */
    public long getTimestampMillis() {
        return timestampMillis;
    }

    /**
    Gives the kind of entry.

    @return CLASS_CREATED or METHOD_DEFINED
    */
    public EventType getType() {
        return type;
    }

    /**
    Gives the class involved.

    @return the class name
    */
    public String getClassName() {
        return className;
    }

    /**
    Gives the method involved.

    @return the method name, or null for a class entry
    */
    public String getMethodName() {
        return methodName;
    }

    /**
    Gives a short description for the PDF, such as "Method Circle.area() defined".

    @return the description
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
