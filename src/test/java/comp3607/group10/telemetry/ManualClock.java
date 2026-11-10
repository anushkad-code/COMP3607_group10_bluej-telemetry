package comp3607.group10.telemetry;

import comp3607.group10.telemetry.core.Clock;

public class ManualClock implements Clock {
    private long now;

    public ManualClock(long startMillis) {
        this.now = startMillis;
    }

    @Override
    public long nowMillis() {
        return now;
    }

    public void advanceSeconds(long seconds) {
        now += seconds * 1000;
    }

    public void advanceMillis(long millis) {
        now += millis;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
*/
