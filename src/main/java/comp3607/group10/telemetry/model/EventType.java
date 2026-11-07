package comp3607.group10.telemetry.model;

public enum EventType {
    SESSION_STARTED,
    SESSION_ENDED,
    PROJECT_OPENED,
    PROJECT_CLOSED,
    CLASS_CREATED,
    CLASS_MODIFIED,
    CLASS_RENAMED,
    CLASS_DELETED,
    METHOD_DEFINED,
    EDIT_BURST,
    COMPILE_STARTED,
    COMPILE_ERROR,
    COMPILE_SUCCEEDED,
    COMPILE_FAILED,
    PROGRAM_RUN,
    RUNTIME_EXCEPTION
}

/*
SOURCES USED:
Oracle. The Java Tutorials: Enum Types. https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html
*/
