package comp3607.group10.telemetry.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassSnapshot {
    private final String className;
    private final boolean compiled;
    private final boolean hasError;
    private final List<String> methodNames;

    public ClassSnapshot(String className, boolean compiled, boolean hasError, List<String> methodNames) {
        this.className = className;
        this.compiled = compiled;
        this.hasError = hasError;
        this.methodNames = Collections.unmodifiableList(new ArrayList<String>(methodNames));
    }

    public String getClassName() {
        return className;
    }

    public boolean isCompiled() {
        return compiled;
    }

    public boolean hasError() {
        return hasError;
    }

    public List<String> getMethodNames() {
        return methodNames;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
