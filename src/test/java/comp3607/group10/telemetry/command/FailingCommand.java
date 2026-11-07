package comp3607.group10.telemetry.command;

public class FailingCommand implements Command {
    @Override
    public void execute() {
        throw new IllegalStateException("disk full");
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
*/
