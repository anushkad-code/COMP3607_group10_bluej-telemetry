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

/**
Runs every report generator and decides where the files go and what they are called. Files are
saved in the BlueJ project folder and named after the project and the time the session ended.

@author Reinaldo Singh
*/
public class ReportExporter {
    /** The report generators to run. */
    private final List<ReportGenerator> generators;
    /** The folder used when no project folder is known. */
    private final Path outputDirectory;
    /** The time zone used in file names. */
    private final ZoneId zone;

    /**
    Creates the exporter.

    @param generators the report generators to run
    @param outputDirectory the folder used when no project folder is known
    @param zone the time zone used for readable times and file names
    */
    public ReportExporter(List<ReportGenerator> generators, Path outputDirectory, ZoneId zone) {
        this.generators = new ArrayList<ReportGenerator>(generators);
        this.outputDirectory = outputDirectory;
        this.zone = zone;
    }

    /**
    Writes every report for the session.

    @param session the session being reported on
    @param endMillis the end time of the session in epoch milliseconds
    @return the files that were written
    @throws IOException if the file cannot be read or written
    */
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

    /**
    Gives the folder used when no project folder is known.

    @return the default folder
    */
    public Path getOutputDirectory() {
        return outputDirectory;
    }

    /**
    Picks the folder for the reports.

    @param session the session being reported on
    @return the project folder, or the default folder if none is known
    */
    Path folderFor(TelemetrySession session) {
        if (session.getProjectFolder() != null) {
            return session.getProjectFolder();
        }
        return outputDirectory;
    }

    /**
    Makes the file name without its extension, for example UseCase2-telemetry-20261115-110231.

    @param session the session being reported on
    @param endMillis the end time of the session in epoch milliseconds
    @return the base file name
    */
    String baseName(TelemetrySession session, long endMillis) {
        String time = Timestamps.forFileName(endMillis, zone);
        String project = lastProjectName(session.getEvents());
        if (project == null) {
            return "telemetry-" + time;
        }
        return safeFileName(project) + "-telemetry-" + time;
    }

    /**
    Finds the name of the last project opened.

    @param events the events recorded in the session
    @return the project name, or null if none was opened
    */
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

    /**
    Replaces characters that are not allowed in file names with underscores.

    @param name the name to make safe
    @return the safe name
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
