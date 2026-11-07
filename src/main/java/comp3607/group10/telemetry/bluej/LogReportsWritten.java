package comp3607.group10.telemetry.bluej;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import comp3607.group10.telemetry.command.ReportsWrittenListener;

public class LogReportsWritten implements ReportsWrittenListener {
    private static final Logger LOG = Logger.getLogger("bluej-telemetry");

    @Override
    public void reportsWritten(List<Path> files) {
        LOG.info("Telemetry reports written: " + files);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
SourceMaking. Observer Design Pattern. https://sourcemaking.com/design_patterns/observer
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
