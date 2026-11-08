package comp3607.group10.telemetry.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.RunOutcome;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class TelemetryEventFactory {
    private final Clock clock;
    private final ErrorCodeClassifier classifier;

    public TelemetryEventFactory(Clock clock, ErrorCodeClassifier classifier) {
        this.clock = clock;
        this.classifier = classifier;
    }

    public TelemetryEvent sessionStarted() {
        return new TelemetryEvent(EventType.SESSION_STARTED, clock.nowMillis(), null);
    }

    public TelemetryEvent sessionEnded() {
        return new TelemetryEvent(EventType.SESSION_ENDED, clock.nowMillis(), null);
    }

    public TelemetryEvent projectOpened(String projectName) {
        return projectEvent(EventType.PROJECT_OPENED, projectName);
    }

    public TelemetryEvent projectClosed(String projectName) {
        return projectEvent(EventType.PROJECT_CLOSED, projectName);
    }

    public TelemetryEvent compileStarted(List<String> fileNames) {
        return compileEvent(EventType.COMPILE_STARTED, fileNames);
    }

    public TelemetryEvent compileSucceeded(List<String> fileNames) {
        return compileEvent(EventType.COMPILE_SUCCEEDED, fileNames);
    }

    public TelemetryEvent compileFailed(List<String> fileNames) {
        return compileEvent(EventType.COMPILE_FAILED, fileNames);
    }

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

    public TelemetryEvent classRenamed(String newName, String oldName) {
        Map<String, String> details = new LinkedHashMap<String, String>();
        if (oldName == null) {
            oldName = "";
        }
        details.put(DetailKeys.OLD_NAME, oldName);
        return new TelemetryEvent(EventType.CLASS_RENAMED, clock.nowMillis(), newName, details);
    }

    public TelemetryEvent classDeleted(String className) {
        return new TelemetryEvent(EventType.CLASS_DELETED, clock.nowMillis(), className);
    }

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

    private TelemetryEvent projectEvent(EventType type, String projectName) {
        Map<String, String> details = new LinkedHashMap<String, String>();
        details.put(DetailKeys.PROJECT, projectName);
        return new TelemetryEvent(type, clock.nowMillis(), projectName, details);
    }

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
*/
