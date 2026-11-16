package comp3607.group10.telemetry.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp3607.group10.telemetry.model.ClassSnapshot;
import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

/**
Remembers each class's methods, so a class creation or a new method is reported only once.

@author Haeley Parbhoo
*/
public class ClassTracker {
    /** The tracked classes by name. */
    private final Map<String, TrackedClass> classes = new HashMap<String, TrackedClass>();

    /**
    Remembers a class that already existed when the project opened, without reporting it.

    @param snapshot the state of the class at this moment
    */
    public void baseline(ClassSnapshot snapshot) {
        TrackedClass tracked = classes.get(snapshot.getClassName());
        if (tracked == null) {
            tracked = new TrackedClass();
            classes.put(snapshot.getClassName(), tracked);
        }
        tracked.getMethods().addAll(snapshot.getMethodNames());
        tracked.setModified(!snapshot.isCompiled());
    }

    /**
    Works out which events a class's new state causes: a creation the first time it is seen, a
    modification when it becomes uncompiled, and a method definition for each new method after a
    successful compile.

    @param snapshot the state of the class at this moment
    @param timestampMillis when it happened, in epoch milliseconds
    @return the events to record, possibly none
    */
    public List<TelemetryEvent> stateChanged(ClassSnapshot snapshot, long timestampMillis) {
        List<TelemetryEvent> events = new ArrayList<TelemetryEvent>();
        String name = snapshot.getClassName();
        TrackedClass tracked = classes.get(name);
        if (tracked == null) {
            tracked = new TrackedClass();
            classes.put(name, tracked);
            events.add(new TelemetryEvent(EventType.CLASS_CREATED, timestampMillis, name));
        } else if (!snapshot.isCompiled() && !snapshot.hasError() && !tracked.isModified()) {
            events.add(new TelemetryEvent(EventType.CLASS_MODIFIED, timestampMillis, name));
        }
        tracked.setModified(!snapshot.isCompiled());
        if (snapshot.isCompiled()) {
            events.addAll(newMethods(tracked, snapshot, timestampMillis));
        }
        return events;
    }

    /**
    Moves a class's history to its new name.

    @param newName the new class name
    @param oldName the old class name
    */
    public void renamed(String newName, String oldName) {
        TrackedClass tracked = classes.remove(oldName);
        if (tracked == null) {
            tracked = new TrackedClass();
        }
        classes.put(newName, tracked);
    }

    /**
    Forgets a removed class.

    @param className the name of the class
    */
    public void removed(String className) {
        classes.remove(className);
    }

    /**
    Tells whether a class is being tracked.

    @param className the name of the class
    @return true if known
    */
    public boolean isKnown(String className) {
        return classes.containsKey(className);
    }

    /**
    Finds the methods that were not there before and forgets methods that were removed.

    @param tracked what is already known about the class
    @param snapshot the state of the class at this moment
    @param timestampMillis when it happened, in epoch milliseconds
    @return a method defined event for each new method
    */
    private List<TelemetryEvent> newMethods(TrackedClass tracked, ClassSnapshot snapshot, long timestampMillis) {
        List<TelemetryEvent> events = new ArrayList<TelemetryEvent>();
        for (String method : snapshot.getMethodNames()) {
            if (tracked.getMethods().add(method)) {
                Map<String, String> details = new HashMap<String, String>();
                details.put(DetailKeys.METHOD, method);
                events.add(new TelemetryEvent(EventType.METHOD_DEFINED, timestampMillis, snapshot.getClassName(), details));
            }
        }
        tracked.getMethods().retainAll(snapshot.getMethodNames());
        return events;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
