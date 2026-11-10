package comp3607.group10.telemetry;

import java.util.Map;

import comp3607.group10.telemetry.core.TelemetrySession;
import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class SampleSessions {
    public static final long START = 1793016000000L;

    private SampleSessions() {
    }

    public static long at(long seconds) {
        return START + seconds * 1000;
    }

    public static TelemetrySession typicalSession() {
        TelemetrySession session = new TelemetrySession("student-1234abcd", START);
        add(session, EventType.SESSION_STARTED, 0, null, TestData.details());
        add(session, EventType.PROJECT_OPENED, 5, "Shapes", TestData.details(DetailKeys.PROJECT, "Shapes"));
        add(session, EventType.CLASS_CREATED, 10, "Circle", TestData.details());
        add(session, EventType.EDIT_BURST, 20, "Circle", TestData.details(DetailKeys.KEYSTROKES, "40", DetailKeys.CHARS_INSERTED, "60", DetailKeys.CHARS_DELETED, "5", DetailKeys.DURATION_MS, "30000"));
        add(session, EventType.COMPILE_STARTED, 60, "Circle", TestData.details());
        add(session, EventType.COMPILE_ERROR, 61, "Circle", TestData.details(DetailKeys.ERROR_CODE, "MISSING_SEMICOLON", DetailKeys.MESSAGE, "';' expected", DetailKeys.LINE, "7", DetailKeys.FILE, "Circle.java"));
        add(session, EventType.COMPILE_ERROR, 61, "Circle", TestData.details(DetailKeys.ERROR_CODE, "CANNOT_FIND_SYMBOL", DetailKeys.MESSAGE, "cannot find symbol - variable radius", DetailKeys.LINE, "9", DetailKeys.FILE, "Circle.java"));
        add(session, EventType.COMPILE_FAILED, 62, "Circle", TestData.details());
        add(session, EventType.COMPILE_STARTED, 100, "Circle", TestData.details());
        add(session, EventType.COMPILE_SUCCEEDED, 101, "Circle", TestData.details());
        add(session, EventType.METHOD_DEFINED, 101, "Circle", TestData.details(DetailKeys.METHOD, "area"));
        add(session, EventType.PROGRAM_RUN, 120, "Circle", TestData.details(DetailKeys.EXIT_TYPE, "NORMAL"));

        add(session, EventType.CLASS_CREATED, 420, "Square", TestData.details());
        add(session, EventType.COMPILE_STARTED, 450, "Square", TestData.details());
        add(session, EventType.COMPILE_SUCCEEDED, 451, "Square", TestData.details());
        add(session, EventType.RUNTIME_EXCEPTION, 480, "Square", TestData.details(DetailKeys.EXIT_TYPE, "EXCEPTION"));
        add(session, EventType.SESSION_ENDED, 510, null, TestData.details());
        session.end(at(510));
        return session;
    }

    private static void add(TelemetrySession session, EventType type, long second, String className, Map<String, String> details) {
        session.update(new TelemetryEvent(type, at(second), className, details));
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
*/
