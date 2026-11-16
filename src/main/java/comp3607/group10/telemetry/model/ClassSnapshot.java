package comp3607.group10.telemetry.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
The state of one class at one moment: its name, whether it is compiled or has an error, and its
method names.

@author Anushka Dwarika
*/
public class ClassSnapshot {
    /** The class name. */
    private final String className;
    /** Whether the class is compiled. */
    private final boolean compiled;
    /** Whether the class has a compile error. */
    private final boolean hasError;
    /** The names of the class's methods. */
    private final List<String> methodNames;

    /**
    Creates a snapshot. The method names are copied.

    @param className the name of the class
    @param compiled whether the class is compiled
    @param hasError whether the class has a compile error
    @param methodNames the names of the methods in the class
    */
    public ClassSnapshot(String className, boolean compiled, boolean hasError, List<String> methodNames) {
        this.className = className;
        this.compiled = compiled;
        this.hasError = hasError;
        this.methodNames = Collections.unmodifiableList(new ArrayList<String>(methodNames));
    }

    /**
    Gives the class name.

    @return the class name
    */
    public String getClassName() {
        return className;
    }

    /**
    Tells whether the class is compiled.

    @return true if compiled
    */
    public boolean isCompiled() {
        return compiled;
    }

    /**
    Tells whether the class has a compile error.

    @return true if it has an error
    */
    public boolean hasError() {
        return hasError;
    }

    /**
    Gives the method names.

    @return the method names
    */
    public List<String> getMethodNames() {
        return methodNames;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
