package comp3607.group10.telemetry.model;

import java.util.Comparator;

public class EventTimeComparator implements Comparator<TelemetryEvent> {
    @Override
    public int compare(TelemetryEvent a, TelemetryEvent b) {
        return Long.compare(a.getTimestampMillis(), b.getTimestampMillis());
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
