package comp3607.group10.telemetry.core;

import java.util.LinkedHashSet;

/**
What the ClassTracker remembers about one class.

@author Haeley Parbhoo
*/
public class TrackedClass {
    /** The class's method names. */
    private final LinkedHashSet<String> methods = new LinkedHashSet<String>();
    /** Whether the class has changed since its last compile. */
    private boolean modified;

    /**
    Gives the method names.

    @return the method names
    */
    public LinkedHashSet<String> getMethods() {
        return methods;
    }

    /**
    Tells whether the class has changed since its last compile.

    @return true if modified
    */
    public boolean isModified() {
        return modified;
    }

    /**
    Sets whether the class has changed since its last compile.

    @param modified true if it has changed
    */
    public void setModified(boolean modified) {
        this.modified = modified;
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
