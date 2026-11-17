package comp3607.group10.telemetry.report;

import java.io.IOException;
import java.nio.file.Path;

import comp3607.group10.telemetry.core.TelemetrySession;

/**
Something that writes a report file for a session. ReportExporter depends on this interface, so a
new format can be added without changing it.

@author Reinaldo Singh
*/
public interface ReportGenerator {
    /**
    Writes the report.

    @param session the session being reported on
    @param endMillis the end time of the session in epoch milliseconds
    @param target where the report should be written
    @return the file that was written
    @throws IOException if the file cannot be read or written
    */
    Path generate(TelemetrySession session, long endMillis, Path target) throws IOException;

    /**
    Gives the file extension, without the dot.

    @return the extension
    */
    String fileExtension();
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Design Patterns: Template, Week 6 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
SourceMaking. Template Method Design Pattern. https://sourcemaking.com/design_patterns/template_method
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
