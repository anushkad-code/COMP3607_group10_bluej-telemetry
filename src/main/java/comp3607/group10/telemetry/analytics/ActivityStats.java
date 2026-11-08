package comp3607.group10.telemetry.analytics;

public class ActivityStats {
    private final long activeTimeSec;
    private final long idleTimeSec;
    private final int totalEdits;
    private final double typingCadenceCpm;

    public ActivityStats(long activeTimeSec, long idleTimeSec, int totalEdits, double typingCadenceCpm) {
        this.activeTimeSec = activeTimeSec;
        this.idleTimeSec = idleTimeSec;
        this.totalEdits = totalEdits;
        this.typingCadenceCpm = typingCadenceCpm;
    }

    public long getActiveTimeSec() {
        return activeTimeSec;
    }

    public long getIdleTimeSec() {
        return idleTimeSec;
    }

    public int getTotalEdits() {
        return totalEdits;
    }

    public double getTypingCadenceCpm() {
        return typingCadenceCpm;
    }

    public double getActiveShare() {
        long total = activeTimeSec + idleTimeSec;
        if (total == 0) {
            return 0.0;
        }
        return (double) activeTimeSec / total;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
*/
