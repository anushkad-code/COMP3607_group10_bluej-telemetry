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

public abstract class AbstractReportGenerator<D> implements ReportGenerator {
    private final StatisticsCalculator calculator;

    protected AbstractReportGenerator(StatisticsCalculator calculator) {
        this.calculator = calculator;
    }

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

    @Override
    public abstract String fileExtension();

    protected abstract D createDocument(SessionStatistics statistics) throws IOException;

    protected abstract void addSummary(D document, SessionStatistics statistics) throws IOException;

    protected abstract void addDetails(D document, SessionStatistics statistics, List<TelemetryEvent> events)
            throws IOException;

    protected void addFooter(D document, SessionStatistics statistics) throws IOException {
    }

    protected abstract void save(D document, Path file) throws IOException;

    protected void close(D document) throws IOException {
    }

    private static void createParentDirectories(Path target) throws IOException {
        Path parent = target.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

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
*/
