package comp3607.group10.telemetry.core;

public class SystemClock implements Clock {
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
*/
