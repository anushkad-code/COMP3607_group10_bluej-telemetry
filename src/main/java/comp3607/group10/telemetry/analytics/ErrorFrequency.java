package comp3607.group10.telemetry.analytics;

/**
One compilation error code and how often it happened.

@author Haeley Parbhoo
*/
public class ErrorFrequency {
    /** The error code, for example MISSING_SEMICOLON. */
    private final String errorCode;
    /** How many times the error happened. */
    private final int count;
    /** One compiler message with this code. */
    private final String exampleMessage;

    /**
    Creates the error frequency.

    @param errorCode the error code
    @param count how many times it happened
    @param exampleMessage one compiler message with this code
    */
    public ErrorFrequency(String errorCode, int count, String exampleMessage) {
        this.errorCode = errorCode;
        this.count = count;
        this.exampleMessage = exampleMessage;
    }

    /**
    Gives the error code.

    @return the error code
    */
    public String getErrorCode() {
        return errorCode;
    }

    /**
    Gives how many times the error happened.

    @return the count
    */
    public int getCount() {
        return count;
    }

    /**
    Gives one compiler message with this code.

    @return an example message
    */
    public String getExampleMessage() {
        return exampleMessage;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
