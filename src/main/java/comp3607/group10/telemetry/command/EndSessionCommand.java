package comp3607.group10.telemetry.command;

import java.nio.file.Path;
import java.util.List;

import comp3607.group10.telemetry.core.TelemetryRecorder;
import comp3607.group10.telemetry.model.TelemetryEvent;
import comp3607.group10.telemetry.report.BackupStore;
import comp3607.group10.telemetry.report.ReportExporter;

public class EndSessionCommand implements Command {
    private final TelemetryRecorder recorder;
    private final TelemetryEvent endEvent;
    private final ReportExporter exporter;
    private final BackupStore backupStore;
    private final ReportsWrittenListener onExported;

    public EndSessionCommand(TelemetryRecorder recorder, TelemetryEvent endEvent, ReportExporter exporter, BackupStore backupStore, ReportsWrittenListener onExported) {
        this.recorder = recorder;
        this.endEvent = endEvent;
        this.exporter = exporter;
        this.backupStore = backupStore;
        this.onExported = onExported;
    }

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
*/
