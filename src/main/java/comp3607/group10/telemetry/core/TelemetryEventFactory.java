package comp3607.group10.telemetry.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.RunOutcome;
import comp3607.group10.telemetry.model.TelemetryEvent;

/**
Creates every kind of telemetry event with the right type, timestamp and details.

@author Haeley Parbhoo
*/
public class TelemetryEventFactory {
    /** Gives the timestamp for each event. */
    private final Clock clock;
    /** Turns compiler messages into error codes. */
    private final ErrorCodeClassifier classifier;

    /**
    Creates the factory.

    @param clock gives the current time
    @param classifier turns compiler messages into error codes
    */
    public TelemetryEventFactory(Clock clock, ErrorCodeClassifier classifier) {
        this.clock = clock;
        this.classifier = classifier;
    }

    /**
    Creates a session started event.

    @return the event
    */
    public TelemetryEvent sessionStarted() {
        return new TelemetryEvent(EventType.SESSION_STARTED, clock.nowMillis(), null);
    }

    /**
    Creates a session ended event.

    @return the event
    */
    public TelemetryEvent sessionEnded() {
        return new TelemetryEvent(EventType.SESSION_ENDED, clock.nowMillis(), null);
    }

    /**
    Creates a project opened event.

    @param projectName the name of the BlueJ project
    @return the event
    */
    public TelemetryEvent projectOpened(String projectName) {
        return projectEvent(EventType.PROJECT_OPENED, projectName);
    }

    /**
    Creates a project closed event.

    @param projectName the name of the BlueJ project
    @return the event
    */
    public TelemetryEvent projectClosed(String projectName) {
        return projectEvent(EventType.PROJECT_CLOSED, projectName);
    }

    /**
    Creates a compile started event.

    @param fileNames the names of the source files being compiled
    @return the event
    */
    public TelemetryEvent compileStarted(List<String> fileNames) {
        return compileEvent(EventType.COMPILE_STARTED, fileNames);
    }

    /**
    Creates a compile succeeded event.

    @param fileNames the names of the source files being compiled
    @return the event
    */
    public TelemetryEvent compileSucceeded(List<String> fileNames) {
        return compileEvent(EventType.COMPILE_SUCCEEDED, fileNames);
    }

    /**
    Creates a compile failed event.

    @param fileNames the names of the source files being compiled
    @return the event
    */
    public TelemetryEvent compileFailed(List<String> fileNames) {
        return compileEvent(EventType.COMPILE_FAILED, fileNames);
    }

    /**
    Creates a compile error event with its message, error code, file and line.

    @param fileName the name of the source file
    @param lineNumber the line the error is on
    @param message the compiler's error message
    @return the event
    */
    public TelemetryEvent compileError(String fileName, int lineNumber, String message) {
        Map<String, String> details = new LinkedHashMap<String, String>();
        details.put(DetailKeys.FILE, fileName);
        details.put(DetailKeys.LINE, Integer.toString(lineNumber));
        if (message == null) {
            message = "";
        }
        details.put(DetailKeys.MESSAGE, message);
        details.put(DetailKeys.ERROR_CODE, classifier.classify(message));
        return new TelemetryEvent(EventType.COMPILE_ERROR, clock.nowMillis(), toClassName(fileName), details);
    }

    /**
    Creates a program run event, or a runtime exception event if the run threw an exception, with
    a short summary of the run.

    @param className the name of the class
    @param methodName the method that was run, or null for a constructor
    @param outcome how the run ended
    @return the event
    */
    public TelemetryEvent programRun(String className, String methodName, RunOutcome outcome) {
        String target = methodName;
        if (methodName == null) {
            target = "constructor";
        }
        Map<String, String> details = new LinkedHashMap<String, String>();
        details.put(DetailKeys.METHOD, target);
        details.put(DetailKeys.EXIT_TYPE, outcome.name());
        details.put(DetailKeys.OUTPUT_SUMMARY, target + " on " + className + " " + outcome.getDescription());
        EventType type = EventType.PROGRAM_RUN;
        if (outcome == RunOutcome.EXCEPTION) {
            type = EventType.RUNTIME_EXCEPTION;
        }
        return new TelemetryEvent(type, clock.nowMillis(), className, details);
    }

    /**
    Creates a class renamed event.

    @param newName the new class name
    @param oldName the old class name
    @return the event
    */
    public TelemetryEvent classRenamed(String newName, String oldName) {
        Map<String, String> details = new LinkedHashMap<String, String>();
        if (oldName == null) {
            oldName = "";
        }
        details.put(DetailKeys.OLD_NAME, oldName);
        return new TelemetryEvent(EventType.CLASS_RENAMED, clock.nowMillis(), newName, details);
    }

    /**
    Creates a class deleted event.

    @param className the name of the class
    @return the event
    */
    public TelemetryEvent classDeleted(String className) {
        return new TelemetryEvent(EventType.CLASS_DELETED, clock.nowMillis(), className);
    }

    /**
    Turns a file name into a class name, for example Circle.java into Circle.

    @param fileName the name of the source file
    @return the class name, or null if there is no file name
    */
    static String toClassName(String fileName) {
        if (fileName == null) {
            return null;
        }
        String name = fileName.replace('\\', '/');
        name = name.substring(name.lastIndexOf('/') + 1);
        int dot = name.lastIndexOf('.');
        if (dot > 0) {
            return name.substring(0, dot);
        }
        return name;
    }

    /**
    Creates a project event.

    @param type the kind of event
    @param projectName the name of the BlueJ project
    @return the new project event
    */
    private TelemetryEvent projectEvent(EventType type, String projectName) {
        Map<String, String> details = new LinkedHashMap<String, String>();
        details.put(DetailKeys.PROJECT, projectName);
        return new TelemetryEvent(type, clock.nowMillis(), projectName, details);
    }

    /**
    Creates a compilation event with the file names and the class name if only one file was
    compiled.

    @param type the kind of event
    @param fileNames the names of the source files being compiled
    @return the new compile event
    */
    private TelemetryEvent compileEvent(EventType type, List<String> fileNames) {
        String className = null;
        if (fileNames.size() == 1) {
            className = toClassName(fileNames.get(0));
        }
        Map<String, String> details = new LinkedHashMap<String, String>();
        details.put(DetailKeys.FILE_COUNT, Integer.toString(fileNames.size()));
        String files = "";
        for (int i = 0; i < fileNames.size(); i++) {
            if (i > 0) {
                files = files + ",";
            }
            files = files + fileNames.get(i);
        }
        details.put(DetailKeys.FILE, files);
        return new TelemetryEvent(type, clock.nowMillis(), className, details);
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
