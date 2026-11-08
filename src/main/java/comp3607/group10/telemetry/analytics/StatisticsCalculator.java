package comp3607.group10.telemetry.analytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import comp3607.group10.telemetry.core.TelemetrySession;
import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventTimeComparator;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class StatisticsCalculator {
    public static final long DEFAULT_IDLE_THRESHOLD_MILLIS = 60000;

    private final long idleThresholdMillis;

    public StatisticsCalculator(long idleThresholdMillis) {
        this.idleThresholdMillis = idleThresholdMillis;
    }

    public StatisticsCalculator() {
        this(DEFAULT_IDLE_THRESHOLD_MILLIS);
    }

    public SessionStatistics calculate(TelemetrySession session, long endMillis) {
        List<TelemetryEvent> events = sortedByTime(session.getEvents());
        long start = session.getStartMillis();
        return new SessionStatistics(session.getStudentId(), start, endMillis,
                compileStats(events), runStats(events), activityStats(events, start, endMillis),
                errorFrequency(events), timeline(events), projectNames(events));
    }

    CompileStats compileStats(List<TelemetryEvent> events) {
        return new CompileStats(count(events, EventType.COMPILE_SUCCEEDED), count(events, EventType.COMPILE_FAILED),
                averageCompileIntervalSec(events));
    }

    RunStats runStats(List<TelemetryEvent> events) {
        int exceptions = count(events, EventType.RUNTIME_EXCEPTION);
        return new RunStats(count(events, EventType.PROGRAM_RUN) + exceptions, exceptions);
    }

    ActivityStats activityStats(List<TelemetryEvent> events, long start, long end) {
        long durationMillis = Math.max(0, end - start);
        long activeMillis = Math.min(activeMillis(events, start, end), durationMillis);
        long durationSec = durationMillis / 1000;
        long activeSec = Math.min(Math.round(activeMillis / 1000.0), durationSec);
        List<TelemetryEvent> bursts = eventsOfType(events, EventType.EDIT_BURST);
        return new ActivityStats(activeSec, durationSec - activeSec,
                sumDetail(bursts, DetailKeys.KEYSTROKES), typingCadenceCpm(bursts));
    }

    long activeMillis(List<TelemetryEvent> events, long start, long end) {
        long active = 0;
        long previous = start;
        for (TelemetryEvent event : events) {
            long time = clamp(event.getTimestampMillis(), start, end);
            active = active + activeGap(previous, time);
            previous = Math.max(previous, time);
            long typingUntil = burstEnd(event, end);
            if (typingUntil > previous) {
                active = active + (typingUntil - previous);
                previous = typingUntil;
            }
        }
        return active + activeGap(previous, end);
    }

    double typingCadenceCpm(List<TelemetryEvent> bursts) {
        long typingMillis = sumDetail(bursts, DetailKeys.DURATION_MS);
        if (typingMillis == 0) {
            return 0.0;
        }
        return sumDetail(bursts, DetailKeys.CHARS_INSERTED) / (typingMillis / 60000.0);
    }

    double averageCompileIntervalSec(List<TelemetryEvent> events) {
        List<TelemetryEvent> starts = eventsOfType(events, EventType.COMPILE_STARTED);
        if (starts.size() < 2) {
            return 0.0;
        }
        long first = starts.get(0).getTimestampMillis();
        long last = starts.get(starts.size() - 1).getTimestampMillis();
        return (last - first) / 1000.0 / (starts.size() - 1);
    }

    List<ErrorFrequency> errorFrequency(List<TelemetryEvent> events) {
        Map<String, Integer> counts = new LinkedHashMap<String, Integer>();
        Map<String, String> examples = new LinkedHashMap<String, String>();
        for (TelemetryEvent error : eventsOfType(events, EventType.COMPILE_ERROR)) {
            String code = error.getDetail(DetailKeys.ERROR_CODE);
            if (counts.containsKey(code)) {
                counts.put(code, counts.get(code) + 1);
            } else {
                counts.put(code, 1);
                examples.put(code, error.getDetail(DetailKeys.MESSAGE));
            }
        }
        List<ErrorFrequency> result = new ArrayList<ErrorFrequency>();
        for (String code : counts.keySet()) {
            result.add(new ErrorFrequency(code, counts.get(code), examples.get(code)));
        }
        Collections.sort(result, new ErrorFrequencyComparator());
        return result;
    }

    List<String> projectNames(List<TelemetryEvent> events) {
        List<String> names = new ArrayList<String>();
        for (TelemetryEvent event : events) {
            if (event.getType() == EventType.PROJECT_OPENED) {
                String name = event.getDetail(DetailKeys.PROJECT);
                if (name != null && !names.contains(name)) {
                    names.add(name);
                }
            }
        }
        return names;
    }

    List<TimelineEntry> timeline(List<TelemetryEvent> events) {
        List<TimelineEntry> entries = new ArrayList<TimelineEntry>();
        for (TelemetryEvent event : events) {
            if (event.is(EventType.CLASS_CREATED) || event.is(EventType.METHOD_DEFINED)) {
                entries.add(new TimelineEntry(event.getTimestampMillis(), event.getType(), event.getClassName(),
                        event.getDetail(DetailKeys.METHOD)));
            }
        }
        return entries;
    }

    private long activeGap(long from, long to) {
        long gap = to - from;
        if (gap > 0 && gap <= idleThresholdMillis) {
            return gap;
        }
        return 0;
    }

    private static long burstEnd(TelemetryEvent event, long end) {
        long last = event.getTimestampMillis();
        String duration = event.getDetail(DetailKeys.DURATION_MS);
        if (duration != null) {
            last = last + Long.parseLong(duration);
        }
        return Math.min(last, end);
    }

    private static long clamp(long value, long min, long max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int count(List<TelemetryEvent> events, EventType type) {
        return eventsOfType(events, type).size();
    }

    private static int sumDetail(List<TelemetryEvent> events, String key) {
        int sum = 0;
        for (TelemetryEvent event : events) {
            sum = sum + parseIntOrZero(event.getDetail(key));
        }
        return sum;
    }

    private static int parseIntOrZero(String value) {
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static List<TelemetryEvent> eventsOfType(List<TelemetryEvent> events, EventType type) {
        List<TelemetryEvent> matching = new ArrayList<TelemetryEvent>();
        for (TelemetryEvent event : events) {
            if (event.is(type)) {
                matching.add(event);
            }
        }
        return matching;
    }

    private static List<TelemetryEvent> sortedByTime(List<TelemetryEvent> events) {
        List<TelemetryEvent> sorted = new ArrayList<TelemetryEvent>(events);
        Collections.sort(sorted, new EventTimeComparator());
        return sorted;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: SOLID, Week 3 lecture notes.
COMP 3607 (n.d.). OOP Design Principles: SOLID - Dependency Inversion, Week 4 lecture notes.
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Exceptions (Catching and Handling Exceptions). https://docs.oracle.com/javase/tutorial/essential/exceptions/handling.html
*/
