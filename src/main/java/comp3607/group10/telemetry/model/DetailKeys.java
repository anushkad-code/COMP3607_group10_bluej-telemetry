package comp3607.group10.telemetry.model;

/**
The names used for the extra values (details) stored with each event, so every class spells them
the same way.

@author Anushka Dwarika
*/
public class DetailKeys {
    /** The project name. */
    public static final String PROJECT = "project";
    /** The source file name, or a comma-separated list of names. */
    public static final String FILE = "file";
    /** The line number of a compile error. */
    public static final String LINE = "line";
    /** The compiler's error message. */
    public static final String MESSAGE = "message";
    /** The error code, such as MISSING_SEMICOLON. */
    public static final String ERROR_CODE = "errorCode";
    /** A class's name before it was renamed. */
    public static final String OLD_NAME = "oldName";
    /** The name of a method that was defined or run. */
    public static final String METHOD = "method";
    /** How a run ended: NORMAL, EXCEPTION, TERMINATED or UNKNOWN. */
    public static final String EXIT_TYPE = "exitType";
    /** A short summary of a run, such as "divide on Calculator ended with a runtime exception". */
    public static final String OUTPUT_SUMMARY = "outputSummary";
    /** The number of edits in an edit burst. */
    public static final String KEYSTROKES = "keystrokes";
    /** The number of characters inserted. */
    public static final String CHARS_INSERTED = "charsInserted";
    /** The number of characters deleted. */
    public static final String CHARS_DELETED = "charsDeleted";
    /** How long an edit burst lasted, in milliseconds. */
    public static final String DURATION_MS = "durationMs";
    /** Milliseconds since the previous compile started. */
    public static final String MS_SINCE_PREVIOUS_COMPILE = "msSincePreviousCompile";
    /** How many files were compiled. */
    public static final String FILE_COUNT = "fileCount";

    /**
    Not used: this class only holds constants.
    */
    private DetailKeys() {
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
