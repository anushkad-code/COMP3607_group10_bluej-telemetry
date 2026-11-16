package comp3607.group10.telemetry.model;

import java.util.Comparator;

/**
Sorts events by their timestamp, earliest first.

@author Anushka Dwarika
*/
public class EventTimeComparator implements Comparator<TelemetryEvent> {
    /**
    Compares two events by time.

    @param a the first item
    @param b the second item
    @return a negative number if a is earlier, positive if b is earlier, 0 if at the same time
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
