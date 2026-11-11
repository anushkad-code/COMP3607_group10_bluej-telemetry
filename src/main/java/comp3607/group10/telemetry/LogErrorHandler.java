package comp3607.group10.telemetry;

import java.util.logging.Level;
import java.util.logging.Logger;

import comp3607.group10.telemetry.core.ErrorHandler;

public class LogErrorHandler implements ErrorHandler {
    private static final Logger LOG = Logger.getLogger("bluej-telemetry");

    @Override
    public void handle(Exception e) {
        LOG.log(Level.WARNING, "Telemetry task failed", e);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
