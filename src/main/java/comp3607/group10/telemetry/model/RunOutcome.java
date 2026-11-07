package comp3607.group10.telemetry.model;

public enum RunOutcome {
    NORMAL("finished normally"),
    EXCEPTION("ended with a runtime exception"),
    TERMINATED("was terminated by the user"),
    UNKNOWN("ended in an unknown state");

    private final String description;

    RunOutcome(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

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
*/
