package comp3607.group10.telemetry;

import java.util.ArrayList;
import java.util.List;

import comp3607.group10.telemetry.core.TelemetryObserver;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class RecordingObserver implements TelemetryObserver {
    public final List<TelemetryEvent> received = new ArrayList<TelemetryEvent>();

    @Override
    public void update(TelemetryEvent event) {
        received.add(event);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
SourceMaking. Observer Design Pattern. https://sourcemaking.com/design_patterns/observer
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
*/
