package comp3607.group10.telemetry.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comp3607.group10.telemetry.model.TelemetryEvent;

public class TelemetrySession implements TelemetryObserver {
    private final String studentId;
    private final long startMillis;
    private Long endMillis;
    private final List<TelemetryEvent> events = new ArrayList<TelemetryEvent>();

    public TelemetrySession(String studentId, long startMillis) {
        this.studentId = studentId;
        this.startMillis = startMillis;
    }

    @Override
    public void update(TelemetryEvent event) {
        events.add(event);
    }

    public void end(long endMillis) {
        this.endMillis = endMillis;
    }

    public boolean isEnded() {
        return endMillis != null;
    }

    public String getStudentId() {
        return studentId;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public Long getEndMillis() {
        return endMillis;
    }

    public List<TelemetryEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Observer Design Pattern. https://sourcemaking.com/design_patterns/observer
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
