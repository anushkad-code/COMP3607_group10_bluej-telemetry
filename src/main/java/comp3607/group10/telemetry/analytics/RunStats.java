package comp3607.group10.telemetry.analytics;

public class RunStats {
    private final int totalRuns;
    private final int runtimeExceptions;

    public RunStats(int totalRuns, int runtimeExceptions) {
        this.totalRuns = totalRuns;
        this.runtimeExceptions = runtimeExceptions;
    }

    public int getTotalRuns() {
        return totalRuns;
    }

    public int getRuntimeExceptions() {
        return runtimeExceptions;
    }

    public int getNormalRuns() {
        return totalRuns - runtimeExceptions;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
*/
