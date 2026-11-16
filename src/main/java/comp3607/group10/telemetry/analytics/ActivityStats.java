package comp3607.group10.telemetry.analytics;

/**
Active coding time, idle time and editing figures for one session.

@author Haeley Parbhoo
*/
public class ActivityStats {
    /** Active coding time in seconds. */
    private final long activeTimeSec;
    /** Idle time in seconds. */
    private final long idleTimeSec;
    /** The number of edits (single changes to the text). */
    private final int totalEdits;
    /** Characters typed per minute while editing. */
    private final double typingCadenceCpm;

    /**
    Creates the activity figures.

    @param activeTimeSec active coding time in seconds
    @param idleTimeSec idle time in seconds
    @param totalEdits the number of edits
    @param typingCadenceCpm characters typed per minute while editing
    */
    public ActivityStats(long activeTimeSec, long idleTimeSec, int totalEdits, double typingCadenceCpm) {
        this.activeTimeSec = activeTimeSec;
        this.idleTimeSec = idleTimeSec;
        this.totalEdits = totalEdits;
        this.typingCadenceCpm = typingCadenceCpm;
    }

    /**
    Gives the active coding time.

    @return active time in seconds
    */
    public long getActiveTimeSec() {
        return activeTimeSec;
    }

    /**
    Gives the idle time.

    @return idle time in seconds
    */
    public long getIdleTimeSec() {
        return idleTimeSec;
    }

    /**
    Gives the number of edits.

    @return the number of edits
    */
    public int getTotalEdits() {
        return totalEdits;
    }

    /**
    Gives the typing speed.

    @return characters per minute while editing
    */
    public double getTypingCadenceCpm() {
        return typingCadenceCpm;
    }

    /**
    Gives the share of the session that was active.

    @return a value from 0 to 1, or 0 for an empty session
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
