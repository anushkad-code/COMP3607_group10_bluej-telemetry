package comp3607.group10.telemetry.analytics;

import java.util.Comparator;

/**
Sorts error frequencies from most to least frequent, then by code name.

@author Haeley Parbhoo
*/
public class ErrorFrequencyComparator implements Comparator<ErrorFrequency> {
    /**
    Compares two error frequencies.

    @param a the first item
    @param b the second item
    @return a negative number if a comes first, positive if b comes first, 0 if equal
    */
    @Override
    public int compare(ErrorFrequency a, ErrorFrequency b) {
        if (a.getCount() != b.getCount()) {
            return Integer.compare(b.getCount(), a.getCount());
        }
        return a.getErrorCode().compareTo(b.getErrorCode());
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
