package comp3607.group10.telemetry.report;

import java.util.Map;

/**
The shape of one event in the JSON file. Gson turns it into JSON.

@author Reinaldo Singh
*/
public class JsonEvent {
    /** The event type, such as COMPILE_ERROR. */
    String type;
    /** The time in ISO 8601 format with milliseconds. */
    String timestamp;
    /** The class involved, or null. */
    String className;
    /** The event's extra details. */
    Map<String, String> details;
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
Google. Gson User Guide. https://github.com/google/gson/blob/main/UserGuide.md
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
