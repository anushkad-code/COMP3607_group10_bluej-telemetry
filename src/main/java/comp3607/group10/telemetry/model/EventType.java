package comp3607.group10.telemetry.model;

/**
Every kind of telemetry event the extension records.

@author Anushka Dwarika
*/
public enum EventType {
    /** BlueJ started and recording began. */
    SESSION_STARTED,
    /** BlueJ closed and recording ended. */
    SESSION_ENDED,
    /** A project was opened. */
    PROJECT_OPENED,
    /** A project was closed. */
    PROJECT_CLOSED,
    /** A new class appeared. */
    CLASS_CREATED,
    /** A class was changed after its last compile. */
    CLASS_MODIFIED,
    /** A class was renamed. */
    CLASS_RENAMED,
    /** A class was deleted. */
    CLASS_DELETED,
    /** A new method appeared after a successful compile. */
    METHOD_DEFINED,
    /** A group of edits to one class close together. */
    EDIT_BURST,
    /** The student started a compilation. */
    COMPILE_STARTED,
    /** One compilation error. */
    COMPILE_ERROR,
    /** A compilation succeeded. */
    COMPILE_SUCCEEDED,
    /** A compilation failed. */
    COMPILE_FAILED,
    /** A method or constructor was run and did not throw an exception. */
    PROGRAM_RUN,
    /** A run ended with a runtime exception. */
    RUNTIME_EXCEPTION
}

/*
SOURCES USED:
Oracle. The Java Tutorials: Enum Types. https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
