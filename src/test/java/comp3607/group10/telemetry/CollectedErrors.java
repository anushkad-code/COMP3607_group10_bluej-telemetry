package comp3607.group10.telemetry;

import java.util.ArrayList;
import java.util.List;

import comp3607.group10.telemetry.core.ErrorHandler;

public class CollectedErrors implements ErrorHandler {
    public final List<Exception> errors = new ArrayList<Exception>();

    @Override
    public synchronized void handle(Exception e) {
        errors.add(e);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
*/
