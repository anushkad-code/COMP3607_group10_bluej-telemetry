package comp3607.group10.telemetry.report;

import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import comp3607.group10.telemetry.core.TelemetrySession;

public class ReportExporter {
    private final List<ReportGenerator> generators;
    private final Path outputDirectory;
    private final ZoneId zone;

    public ReportExporter(List<ReportGenerator> generators, Path outputDirectory, ZoneId zone) {
        this.generators = List.copyOf(generators);
        this.outputDirectory = outputDirectory;
        this.zone = zone;
    }

    public List<Path> export(TelemetrySession session, long endMillis) throws IOException {
        String baseName = "telemetry-" + session.getStudentId() + "-" + Timestamps.forFileName(endMillis, zone);
        List<Path> written = new ArrayList<Path>();
        for (ReportGenerator generator : generators) {
            Path target = outputDirectory.resolve(baseName + "." + generator.fileExtension());
            written.add(generator.generate(session, endMillis, target));
        }
        return written;
    }

    public Path getOutputDirectory() {
        return outputDirectory;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Template, Week 6 lecture notes.
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
SourceMaking. Template Method Design Pattern. https://sourcemaking.com/design_patterns/template_method
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
