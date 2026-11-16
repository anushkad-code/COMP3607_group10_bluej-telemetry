package comp3607.group10.telemetry.analytics;

/**
Compilation counts for one session.

@author Haeley Parbhoo
*/
public class CompileStats {
    /** The number of successful compilations. */
    private final int successful;
    /** The number of failed compilations. */
    private final int failed;
    /** The average time between compiles in seconds. */
    private final double averageIntervalSec;

    /**
    Creates the compilation figures.

    @param successful the number of successful compilations
    @param failed the number of failed compilations
    @param averageIntervalSec the average time between compiles in seconds
    */
    public CompileStats(int successful, int failed, double averageIntervalSec) {
        this.successful = successful;
        this.failed = failed;
        this.averageIntervalSec = averageIntervalSec;
    }

    /**
    Gives the number of successful compilations.

    @return the successful count
    */
    public int getSuccessful() {
        return successful;
    }

    /**
    Gives the number of failed compilations.

    @return the failed count
    */
    public int getFailed() {
        return failed;
    }

    /**
    Gives the average time between compiles.

    @return the average interval in seconds
    */
    public double getAverageIntervalSec() {
        return averageIntervalSec;
    }

    /**
    Gives the total number of compilations.

    @return successful plus failed
    */
    public int getTotal() {
        return successful + failed;
    }

    /**
    Gives the share of compilations that succeeded.

    @return a value from 0 to 1, or 0 when nothing was compiled
    */
    public double getSuccessRate() {
        if (getTotal() == 0) {
            return 0.0;
        }
        return (double) successful / getTotal();
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
