package comp3607.group10.telemetry.analytics;

public class CompileStats {
    private final int successful;
    private final int failed;
    private final double averageIntervalSec;

    public CompileStats(int successful, int failed, double averageIntervalSec) {
        this.successful = successful;
        this.failed = failed;
        this.averageIntervalSec = averageIntervalSec;
    }

    public int getSuccessful() {
        return successful;
    }

    public int getFailed() {
        return failed;
    }

    public double getAverageIntervalSec() {
        return averageIntervalSec;
    }

    public int getTotal() {
        return successful + failed;
    }

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
*/
