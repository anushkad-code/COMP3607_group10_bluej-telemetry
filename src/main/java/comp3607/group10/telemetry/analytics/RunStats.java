package comp3607.group10.telemetry.analytics;

/**
Program run counts for one session.

@author Haeley Parbhoo
*/
public class RunStats {
    /** The number of program runs. */
    private final int totalRuns;
    /** The number of runs that ended with an exception. */
    private final int runtimeExceptions;

    /**
    Creates the run figures.

    @param totalRuns the number of program runs
    @param runtimeExceptions the number of runs that ended with an exception
    */
    public RunStats(int totalRuns, int runtimeExceptions) {
        this.totalRuns = totalRuns;
        this.runtimeExceptions = runtimeExceptions;
    }

    /**
    Gives the number of program runs.

    @return the total runs
    */
    public int getTotalRuns() {
        return totalRuns;
    }

    /**
    Gives the number of runs that ended with an exception.

    @return the runtime exception count
    */
    public int getRuntimeExceptions() {
        return runtimeExceptions;
    }

    /**
    Gives the number of runs that ended without an exception.

    @return total runs minus runtime exceptions
    */
    public int getNormalRuns() {
        return totalRuns - runtimeExceptions;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
