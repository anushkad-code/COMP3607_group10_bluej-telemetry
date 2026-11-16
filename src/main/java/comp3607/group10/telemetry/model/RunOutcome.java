package comp3607.group10.telemetry.model;

/**
How a program run ended.

@author Anushka Dwarika
*/
public enum RunOutcome {
    /** The run finished normally. */
    NORMAL("finished normally"),
    /** The run ended with a runtime exception. */
    EXCEPTION("ended with a runtime exception"),
    /** The student stopped the run. */
    TERMINATED("was terminated by the user"),
    /** BlueJ did not say how the run ended. */
    UNKNOWN("ended in an unknown state");

    /** Words used in the run summary. */
    private final String description;

    /**
    Creates an outcome with its description.

    @param description the words used in the run summary
    */
    RunOutcome(String description) {
        this.description = description;
    }

    /**
    Gives the words used in the run summary.

    @return the description
    */
    public String getDescription() {
        return description;
    }

    /**
    Turns BlueJ's exit type name into an outcome.

    @param exitType the exit type name BlueJ reports
    @return the matching outcome, or UNKNOWN
    */
    public static RunOutcome fromExitType(String exitType) {
        if (exitType.equals("NORMAL_EXIT")) {
            return NORMAL;
        }
        if (exitType.equals("EXCEPTION_EXIT")) {
            return EXCEPTION;
        }
        if (exitType.equals("TERMINATED_EXIT")) {
            return TERMINATED;
        }
        return UNKNOWN;
    }
}

/*
SOURCES USED:
Oracle. The Java Tutorials: Enum Types. https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
