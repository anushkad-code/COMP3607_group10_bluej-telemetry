package comp3607.group10.telemetry.analytics;

public class ErrorFrequency {
    private final String errorCode;
    private final int count;
    private final String exampleMessage;

    public ErrorFrequency(String errorCode, int count, String exampleMessage) {
        this.errorCode = errorCode;
        this.count = count;
        this.exampleMessage = exampleMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getCount() {
        return count;
    }

    public String getExampleMessage() {
        return exampleMessage;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
*/
