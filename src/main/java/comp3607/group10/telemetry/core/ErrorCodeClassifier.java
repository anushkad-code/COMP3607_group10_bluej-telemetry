package comp3607.group10.telemetry.core;

import java.util.ArrayList;
import java.util.List;

/**
Turns a compiler message into an error code, for example "cannot find symbol" into
CANNOT_FIND_SYMBOL, so errors can be grouped by type. New rules can be added without changing the
class.

@author Haeley Parbhoo
*/
public class ErrorCodeClassifier {
    /** The code used when no rule matches. */
    public static final String UNKNOWN = "OTHER";

    /** The phrases to look for, in order. */
    private final List<String> phrases = new ArrayList<String>();
    /** The code for each phrase. */
    private final List<String> codes = new ArrayList<String>();

    /**
    Creates the classifier with the rules for common Java errors.
    */
    public ErrorCodeClassifier() {
        addRule("cannot find symbol", "CANNOT_FIND_SYMBOL");
        addRule("';' expected", "MISSING_SEMICOLON");
        addRule("incompatible types", "INCOMPATIBLE_TYPES");
        addRule("missing return statement", "MISSING_RETURN");
        addRule("might not have been initialized", "UNINITIALISED_VARIABLE");
        addRule("unreachable statement", "UNREACHABLE_STATEMENT");
        addRule("unreported exception", "UNREPORTED_EXCEPTION");
        addRule("already defined", "DUPLICATE_DEFINITION");
        addRule("cannot be applied to", "WRONG_ARGUMENTS");
        addRule("non-static", "STATIC_CONTEXT");
        addRule("class, interface, enum, or record expected", "MISPLACED_CODE");
        addRule("reached end of file while parsing", "UNBALANCED_BRACES");
        addRule("illegal start of", "ILLEGAL_START");
        addRule("expected", "SYNTAX_EXPECTED");
    }

    /**
    Adds a rule. Rules are checked in the order they were added.

    @param phrase text to look for in the compiler message
    @param code the error code to use when the phrase is found
    */
    public void addRule(String phrase, String code) {
        phrases.add(phrase.toLowerCase());
        codes.add(code);
    }

    /**
    Finds the code for a compiler message. Case is ignored.

    @param message the message
    @return the error code, or OTHER if no rule matches
    */
    public String classify(String message) {
        if (message == null || message.trim().isEmpty()) {
            return UNKNOWN;
        }
        String lower = message.toLowerCase();
        for (int i = 0; i < phrases.size(); i++) {
            if (lower.contains(phrases.get(i))) {
                return codes.get(i);
            }
        }
        return UNKNOWN;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: SOLID, Week 3 lecture notes.
COMP 3607 (n.d.). OOP Design Principles: SOLID - Dependency Inversion, Week 4 lecture notes.
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
OpenJDK. javac compiler error messages (compiler.properties). https://github.com/openjdk/jdk/blob/master/src/jdk.compiler/share/classes/com/sun/tools/javac/resources/compiler.properties
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
