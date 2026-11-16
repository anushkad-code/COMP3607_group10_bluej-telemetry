package comp3607.group10.telemetry.core;

/**
Gives the current time. The tests use a manual clock so they can control time exactly.

@author Haeley Parbhoo
*/
public interface Clock {
    /**
    Gives the current time.

    @return epoch milliseconds
    */
    long nowMillis();
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: SOLID, Week 3 lecture notes.
COMP 3607 (n.d.). OOP Design Principles: SOLID - Dependency Inversion, Week 4 lecture notes.
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
