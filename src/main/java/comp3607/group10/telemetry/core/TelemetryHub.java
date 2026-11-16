package comp3607.group10.telemetry.core;

import java.util.List;
import java.util.ArrayList;

import comp3607.group10.telemetry.model.TelemetryEvent;

/**
The concrete subject of the Observer pattern. Every recorded event passes through the hub, which
notifies each registered observer.

@author Haeley Parbhoo
*/
public class TelemetryHub implements TelemetrySubject {
    /** The registered observers, in the order they registered. */
    private final List<TelemetryObserver> observers = new ArrayList<TelemetryObserver>();

    /**
    Registers an observer.

    @param observer the observer
    */
    @Override
    public void register(TelemetryObserver observer) {
        if (observer == null) {
            throw new NullPointerException("observer");
        }
        observers.add(observer);
    }

    /**
    Unregisters an observer.

    @param observer the observer
    */
    @Override
    public void unregister(TelemetryObserver observer) {
        observers.remove(observer);
    }

    /**
    Sends the event to every registered observer, in order.

    @param event the telemetry event
    */
    @Override
    public void notifyObservers(TelemetryEvent event) {
        for (TelemetryObserver observer : observers) {
            observer.update(event);
        }
    }

    /**
    Gives the number of registered observers.

    @return the count
    */
    public int observerCount() {
        return observers.size();
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Observer Design Pattern. https://sourcemaking.com/design_patterns/observer
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
