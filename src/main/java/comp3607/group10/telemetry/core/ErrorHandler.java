package comp3607.group10.telemetry.core;

/**
Reports errors from background work.

@author Haeley Parbhoo
*/
public interface ErrorHandler {
    /**
    Reports an error.

    @param e the exception
    */
    void handle(Exception e);
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: SOLID, Week 3 lecture notes.
COMP 3607 (n.d.). OOP Design Principles: SOLID - Dependency Inversion, Week 4 lecture notes.
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
