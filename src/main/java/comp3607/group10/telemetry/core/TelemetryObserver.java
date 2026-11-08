package comp3607.group10.telemetry.core;

import comp3607.group10.telemetry.model.TelemetryEvent;

public interface TelemetryObserver {
    void update(TelemetryEvent event);
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Observer Design Pattern. https://sourcemaking.com/design_patterns/observer
*/
