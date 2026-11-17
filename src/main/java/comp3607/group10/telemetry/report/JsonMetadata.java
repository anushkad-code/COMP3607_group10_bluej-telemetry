package comp3607.group10.telemetry.report;

/**
The shape of the metadata block in the JSON file. Gson turns it into JSON, using these field
names as the keys.

@author Reinaldo Singh
*/
public class JsonMetadata {
    /** The anonymous student ID. */
    String studentID;
    /** When the session started, in ISO 8601 format. */
    String sessionStart;
    /** When the session ended, in ISO 8601 format. */
    String sessionEnd;
    /** The length of the session in seconds. */
    long sessionDurationSec;
    /** The total number of compilations. */
    int totalCompilations;
    /** The number of successful compilations. */
    int successfulCompilations;
    /** The number of failed compilations. */
    int failedCompilations;
    /** The number of program runs. */
    int totalRuns;
    /** The number of runs that ended with an exception. */
    int runtimeExceptions;
    /** Active coding time in seconds. */
    long activeTimeSec;
    /** Idle time in seconds. */
    long idleTimeSec;
    /** The number of edits. */
    int totalEdits;
    /** Characters typed per minute while editing. */
    double typingCadenceCpm;
    /** The average time between compiles in seconds. */
    double averageCompileIntervalSec;
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
Google. Gson User Guide. https://github.com/google/gson/blob/main/UserGuide.md
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
