package comp3607.group10.telemetry.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ErrorCodeClassifierTest {
    private ErrorCodeClassifier classifier;

    @BeforeEach
    public void setUp() {
        classifier = new ErrorCodeClassifier();
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "cannot find symbol - variable radius | CANNOT_FIND_SYMBOL",
            "';' expected | MISSING_SEMICOLON",
            "incompatible types: String cannot be converted to int | INCOMPATIBLE_TYPES",
            "missing return statement | MISSING_RETURN",
            "variable x might not have been initialized | UNINITIALISED_VARIABLE",
            "reached end of file while parsing | UNBALANCED_BRACES",
            "')' expected | SYNTAX_EXPECTED",
            "Something completely new | OTHER"
    })
    public void mapsCompilerMessagesToCodes(String message, String expectedCode) {
        assertEquals(expectedCode, classifier.classify(message));
    }

    @Test
    public void matchingIgnoresCase() {
        assertEquals("CANNOT_FIND_SYMBOL", classifier.classify("Cannot Find Symbol"));
    }

    @Test
    public void emptyOrMissingMessagesAreOther() {
        assertEquals(ErrorCodeClassifier.UNKNOWN, classifier.classify(null));
        assertEquals(ErrorCodeClassifier.UNKNOWN, classifier.classify("   "));
    }

    @Test
    public void newRulesCanBeAddedWithoutChangingTheClass() {
        classifier.addRule("Something completely new", "NEW_CODE");
        assertEquals("NEW_CODE", classifier.classify("something completely new here"));
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
*/
