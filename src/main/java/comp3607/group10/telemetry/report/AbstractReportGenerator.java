package comp3607.group10.telemetry.report;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import comp3607.group10.telemetry.analytics.SessionStatistics;
import comp3607.group10.telemetry.analytics.StatisticsCalculator;
import comp3607.group10.telemetry.core.TelemetrySession;
import comp3607.group10.telemetry.model.TelemetryEvent;

/**
The abstract class of the Template Method pattern. Its final generate method fixes the steps
every report follows, and the subclasses fill in the steps that differ.

@param <D> the type of document being built

@author Reinaldo Singh
*/
public abstract class AbstractReportGenerator<D> implements ReportGenerator {
    /** Works out the statistics from the events. */
    private final StatisticsCalculator calculator;

    /**
    Creates the generator.

    @param calculator works out the statistics from the events
    */
    protected AbstractReportGenerator(StatisticsCalculator calculator) {
        this.calculator = calculator;
    }

    /**
    The template method. It calculates the statistics, builds the document with createDocument,
    addSummary, addDetails and addFooter, saves it to a temporary file, and moves it into place,
    so a half-written report is never left behind.

    @param session the session being reported on
    @param endMillis the end time of the session in epoch milliseconds
    @param target where the report should be written
    @return the file that was written
    @throws IOException if the file cannot be read or written
    */
    @Override
    public final Path generate(TelemetrySession session, long endMillis, Path target) throws IOException {
        SessionStatistics statistics = calculator.calculate(session, endMillis);
        D document = createDocument(statistics);
        Path temp = target.resolveSibling(target.getFileName() + ".tmp");
        try {
            addSummary(document, statistics);
            addDetails(document, statistics, session.getEvents());
            addFooter(document, statistics);
            createParentDirectories(target);
            save(document, temp);
            close(document);
            moveIntoPlace(temp, target);
        } catch (Exception e) {
            close(document);
            Files.deleteIfExists(temp);
            throw new IOException("Could not write the report " + target + ": " + e.getMessage(), e);
        }
        return target;
    }

    /**
    Gives the file extension, without the dot.

    @return for example "json" or "pdf"
    */
    @Override
    public abstract String fileExtension();

    /**
    Step: creates an empty document.

    @param statistics the statistics calculated for the session
    @return the new document
    @throws IOException if the file cannot be read or written
    */
    protected abstract D createDocument(SessionStatistics statistics) throws IOException;

    /**
    Step: adds the summary part of the report.

    @param document the document being built
    @param statistics the statistics calculated for the session
    @throws IOException if the file cannot be read or written
    */
    protected abstract void addSummary(D document, SessionStatistics statistics) throws IOException;

    /**
    Step: adds the detailed part of the report.

    @param document the document being built
    @param statistics the statistics calculated for the session
    @param events the events recorded in the session
    @throws IOException if the file cannot be read or written
    */
    protected abstract void addDetails(D document, SessionStatistics statistics, List<TelemetryEvent> events)
            throws IOException;

    /**
    Optional step (hook): adds a footer. Does nothing unless a subclass overrides it.

    @param document the document being built
    @param statistics the statistics calculated for the session
    @throws IOException if the file cannot be read or written
    */
    protected void addFooter(D document, SessionStatistics statistics) throws IOException {
    }

    /**
    Step: saves the document to a file.

    @param document the document being built
    @param file the file to write
    @throws IOException if the file cannot be read or written
    */
    protected abstract void save(D document, Path file) throws IOException;

    /**
    Optional step (hook): frees the document's resources. Does nothing unless a subclass
    overrides it.

    @param document the document being built
    @throws IOException if the file cannot be read or written
    */
    protected void close(D document) throws IOException {
    }

    /**
    Creates the folder the report goes in, if it does not exist.

    @param target where the report should be written
    @throws IOException if the file cannot be read or written
    */
    private static void createParentDirectories(Path target) throws IOException {
        Path parent = target.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    /**
    Moves the finished temporary file into place, replacing any old file.

    @param temp the temporary file that was written
    @param target where the report should be written
    @throws IOException if the file cannot be read or written
    */
    private static void moveIntoPlace(Path temp, Path target) throws IOException {
        try {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Template, Week 6 lecture notes.
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Template Method Design Pattern. https://sourcemaking.com/design_patterns/template_method
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Exceptions (Catching and Handling Exceptions). https://docs.oracle.com/javase/tutorial/essential/exceptions/handling.html
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
