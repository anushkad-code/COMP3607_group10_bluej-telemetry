package comp3607.group10.telemetry.core;

/**
The real clock, using the computer's time.

@author Haeley Parbhoo
*/
public class SystemClock implements Clock {
    /**
    Gives the computer's current time.

    @return epoch milliseconds
    */
    @Override
    public long nowMillis() {
        return System.currentTimeMillis();
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: SOLID, Week 3 lecture notes.
COMP 3607 (n.d.). OOP Design Principles: SOLID - Dependency Inversion, Week 4 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
