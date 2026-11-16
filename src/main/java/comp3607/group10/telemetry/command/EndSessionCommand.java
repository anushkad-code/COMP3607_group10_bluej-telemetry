package comp3607.group10.telemetry.command;

import java.nio.file.Path;
import java.util.List;

import comp3607.group10.telemetry.core.TelemetryRecorder;
import comp3607.group10.telemetry.model.TelemetryEvent;
import comp3607.group10.telemetry.report.BackupStore;
import comp3607.group10.telemetry.report.ReportExporter;

/**
Command that ends the session: it records the end, writes the final reports and deletes the
backup.

@author Anushka Dwarika
*/
public class EndSessionCommand implements Command {
    /** The recorder that receives the work. */
    private final TelemetryRecorder recorder;
    /** The session ended event. */
    private final TelemetryEvent endEvent;
    /** Writes the final reports. */
    private final ReportExporter exporter;
    /** Deletes the backup after a clean finish. */
    private final BackupStore backupStore;
    /** Told which files were written. */
    private final ReportsWrittenListener onExported;

    /**
    Creates the command.

    @param recorder the recorder that receives the work
    @param endEvent the session ended event
    @param exporter writes the JSON and PDF reports
    @param backupStore reads and writes the backup file
    @param onExported told which files were written
    */
    public EndSessionCommand(TelemetryRecorder recorder, TelemetryEvent endEvent, ReportExporter exporter, BackupStore backupStore, ReportsWrittenListener onExported) {
        this.recorder = recorder;
        this.endEvent = endEvent;
        this.exporter = exporter;
        this.backupStore = backupStore;
        this.onExported = onExported;
    }

    /**
    Ends the session, writes the reports, deletes the backup and reports the files.

    @throws Exception if the work fails
    */
    @Override
    public void execute() throws Exception {
        recorder.endSession(endEvent);
        List<Path> written = exporter.export(recorder.getSession(), endEvent.getTimestampMillis());
        backupStore.discard();
        onExported.reportsWritten(written);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
