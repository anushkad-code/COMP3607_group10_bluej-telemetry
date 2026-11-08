package comp3607.group10.telemetry.core;

import java.util.List;
import java.util.ArrayList;

import comp3607.group10.telemetry.model.TelemetryEvent;

public class TelemetryHub implements TelemetrySubject {
    private final List<TelemetryObserver> observers = new ArrayList<TelemetryObserver>();

    @Override
    public void register(TelemetryObserver observer) {
        if (observer == null) {
            throw new NullPointerException("observer");
        }
        observers.add(observer);
    }

    @Override
    public void unregister(TelemetryObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(TelemetryEvent event) {
        for (TelemetryObserver observer : observers) {
            observer.update(event);
        }
    }

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
*/
