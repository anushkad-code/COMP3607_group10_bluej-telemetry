package comp3607.group10.telemetry.analytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
Every figure calculated for one session. The report generators read from this class.

@author Haeley Parbhoo
*/
public class SessionStatistics {
    /** The anonymous student ID. */
    private final String studentId;
    /** When the session started, in epoch milliseconds. */
    private final long sessionStartMillis;
    /** When the session ended, in epoch milliseconds. */
    private final long sessionEndMillis;
    /** The compilation counts. */
    private final CompileStats compiles;
    /** The run counts. */
    private final RunStats runs;
    /** Active and idle time. */
    private final ActivityStats activity;
    /** Error codes with their counts, most frequent first. */
    private final List<ErrorFrequency> errorFrequency;
    /** Class and method timeline in the order recorded. */
    private final List<TimelineEntry> timeline;
    /** The projects opened in the session. */
    private final List<String> projectNames;

    /**
    Creates the statistics.

    @param studentId the anonymous student ID
    @param sessionStartMillis when the session started, in epoch milliseconds
    @param sessionEndMillis when the session ended, in epoch milliseconds
    @param compiles the compilation counts
    @param runs the run counts
    @param activity the active and idle time
    @param errorFrequency the error codes with their counts, most frequent first
    @param timeline the class and method timeline
    @param projectNames the names of the projects opened
    */
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

    /**
    Gives the names of the projects opened in the session.

    @return the project names
    */
    public List<String> getProjectNames() {
        return projectNames;
    }

    /**
    Gives the anonymous student ID.

    @return the student ID
    */
    public String getStudentId() {
        return studentId;
    }

    /**
    Gives when the session started.

    @return epoch milliseconds
    */
    public long getSessionStartMillis() {
        return sessionStartMillis;
    }

    /**
    Gives when the session ended.

    @return epoch milliseconds
    */
    public long getSessionEndMillis() {
        return sessionEndMillis;
    }

    /**
    Gives the compilation counts.

    @return the compile statistics
    */
    public CompileStats getCompiles() {
        return compiles;
    }

    /**
    Gives the run counts.

    @return the run statistics
    */
    public RunStats getRuns() {
        return runs;
    }

    /**
    Gives the active and idle time.

    @return the activity statistics
    */
    public ActivityStats getActivity() {
        return activity;
    }

    /**
    Gives the error codes with their counts.

    @return most frequent first
    */
    public List<ErrorFrequency> getErrorFrequency() {
        return errorFrequency;
    }

    /**
    Gives the class and method timeline.

    @return entries in the order recorded
    */
    public List<TimelineEntry> getTimeline() {
        return timeline;
    }

    /**
    Gives the length of the session.

    @return the duration in seconds
    */
    public long getSessionDurationSec() {
        return Math.max(0, sessionEndMillis - sessionStartMillis) / 1000;
    }

    /**
    Tells whether the program was run at least once.

    @return true if there was a run
    */
    public boolean hasRuns() {
        return runs.getTotalRuns() > 0;
    }

    /**
    Gives how many compilations there were for each run.

    @return compilations divided by runs, or 0 with no runs
    */
    public double getCompileToRunRatio() {
        if (runs.getTotalRuns() == 0) {
            return 0.0;
        }
        return (double) compiles.getTotal() / runs.getTotalRuns();
    }

    /**
    Gives the total number of compilation errors.

    @return the sum of all error counts
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
