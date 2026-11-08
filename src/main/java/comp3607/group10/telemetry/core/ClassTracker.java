package comp3607.group10.telemetry.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp3607.group10.telemetry.model.ClassSnapshot;
import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class ClassTracker {
    private final Map<String, TrackedClass> classes = new HashMap<String, TrackedClass>();

    public void baseline(ClassSnapshot snapshot) {
        TrackedClass tracked = classes.get(snapshot.getClassName());
        if (tracked == null) {
            tracked = new TrackedClass();
            classes.put(snapshot.getClassName(), tracked);
        }
        tracked.getMethods().addAll(snapshot.getMethodNames());
        tracked.setModified(!snapshot.isCompiled());
    }

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

    public void renamed(String newName, String oldName) {
        TrackedClass tracked = classes.remove(oldName);
        if (tracked == null) {
            tracked = new TrackedClass();
        }
        classes.put(newName, tracked);
    }

    public void removed(String className) {
        classes.remove(className);
    }

    public boolean isKnown(String className) {
        return classes.containsKey(className);
    }

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
*/
