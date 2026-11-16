package comp3607.group10.telemetry.report;

import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import comp3607.group10.telemetry.core.TelemetrySession;
import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class ReportExporter {
    private final List<ReportGenerator> generators;
    private final Path outputDirectory;
    private final ZoneId zone;

    public ReportExporter(List<ReportGenerator> generators, Path outputDirectory, ZoneId zone) {
        this.generators = new ArrayList<ReportGenerator>(generators);
        this.outputDirectory = outputDirectory;
        this.zone = zone;
    }

    public List<Path> export(TelemetrySession session, long endMillis) throws IOException {
        String baseName = baseName(session, endMillis);
        Path folder = folderFor(session);
        List<Path> written = new ArrayList<Path>();
        for (ReportGenerator generator : generators) {
            Path target = folder.resolve(baseName + "." + generator.fileExtension());
            written.add(generator.generate(session, endMillis, target));
        }
        return written;
    }

    public Path getOutputDirectory() {
        return outputDirectory;
    }

    Path folderFor(TelemetrySession session) {
        if (session.getProjectFolder() != null) {
            return session.getProjectFolder();
        }
        return outputDirectory;
    }

    String baseName(TelemetrySession session, long endMillis) {
        String time = Timestamps.forFileName(endMillis, zone);
        String project = lastProjectName(session.getEvents());
        if (project == null) {
            return "telemetry-" + time;
        }
        return safeFileName(project) + "-telemetry-" + time;
    }

    static String lastProjectName(List<TelemetryEvent> events) {
        String name = null;
        for (TelemetryEvent event : events) {
            String project = event.getDetail(DetailKeys.PROJECT);
            if (event.is(EventType.PROJECT_OPENED) && project != null && !project.trim().isEmpty()) {
                name = project;
            }
        }
        return name;
    }

    static String safeFileName(String name) {
        String safe = "";
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '-' || c == '_' || c == '.') {
                safe = safe + c;
            } else {
                safe = safe + "_";
            }
        }
        return safe;
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
