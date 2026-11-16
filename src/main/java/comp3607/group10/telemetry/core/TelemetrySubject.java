package comp3607.group10.telemetry.core;

import comp3607.group10.telemetry.model.TelemetryEvent;

/**
The subject interface of the Observer pattern.

@author Haeley Parbhoo
*/
public interface TelemetrySubject {
    /**
    Registers an observer.

    @param observer the observer
    */
    void register(TelemetryObserver observer);

    /**
    Unregisters an observer.

    @param observer the observer
    */
    void unregister(TelemetryObserver observer);

    /**
    Sends an event to every registered observer.

    @param event the telemetry event
    */
    void notifyObservers(TelemetryEvent event);
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Observer Design Pattern. https://sourcemaking.com/design_patterns/observer
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
