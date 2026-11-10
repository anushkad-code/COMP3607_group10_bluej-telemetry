package comp3607.group10.telemetry.report;

public class JsonMetadata {
    String studentID;
    String sessionStart;
    String sessionEnd;
    long sessionDurationSec;
    int totalCompilations;
    int successfulCompilations;
    int failedCompilations;
    int totalRuns;
    int runtimeExceptions;
    long activeTimeSec;
    long idleTimeSec;
    int totalEdits;
    double typingCadenceCpm;
    double averageCompileIntervalSec;
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
Google. Gson User Guide. https://github.com/google/gson/blob/main/UserGuide.md
*/
