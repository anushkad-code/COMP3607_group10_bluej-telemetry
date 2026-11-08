package comp3607.group10.telemetry.analytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SessionStatistics {
    private final String studentId;
    private final long sessionStartMillis;
    private final long sessionEndMillis;
    private final CompileStats compiles;
    private final RunStats runs;
    private final ActivityStats activity;
    private final List<ErrorFrequency> errorFrequency;
    private final List<TimelineEntry> timeline;
    private final List<String> projectNames;

    public SessionStatistics(String studentId, long sessionStartMillis, long sessionEndMillis,CompileStats compiles, RunStats runs, ActivityStats activity, List<ErrorFrequency> errorFrequency, List<TimelineEntry> timeline, List<String> projectNames) {
        this.studentId = studentId;
        this.sessionStartMillis = sessionStartMillis;
        this.sessionEndMillis = sessionEndMillis;
        this.compiles = compiles;
        this.runs = runs;
        this.activity = activity;
        this.errorFrequency = Collections.unmodifiableList(new ArrayList<ErrorFrequency>(errorFrequency));
        this.timeline = Collections.unmodifiableList(new ArrayList<TimelineEntry>(timeline));
        this.projectNames = Collections.unmodifiableList(new ArrayList<String>(projectNames));
    }

    public List<String> getProjectNames() {
        return projectNames;
    }

    public String getStudentId() {
        return studentId;
    }

    public long getSessionStartMillis() {
        return sessionStartMillis;
    }

    public long getSessionEndMillis() {
        return sessionEndMillis;
    }

    public CompileStats getCompiles() {
        return compiles;
    }

    public RunStats getRuns() {
        return runs;
    }

    public ActivityStats getActivity() {
        return activity;
    }

    public List<ErrorFrequency> getErrorFrequency() {
        return errorFrequency;
    }

    public List<TimelineEntry> getTimeline() {
        return timeline;
    }

    public long getSessionDurationSec() {
        return Math.max(0, sessionEndMillis - sessionStartMillis) / 1000;
    }

    public boolean hasRuns() {
        return runs.getTotalRuns() > 0;
    }

    public double getCompileToRunRatio() {
        if (runs.getTotalRuns() == 0) {
            return 0.0;
        }
        return (double) compiles.getTotal() / runs.getTotalRuns();
    }

    public int getTotalCompileErrors() {
        int total = 0;
        for (ErrorFrequency error : errorFrequency) {
            total = total + error.getCount();
        }
        return total;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
