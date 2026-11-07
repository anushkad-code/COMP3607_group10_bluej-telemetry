package comp3607.group10.telemetry.command;

import comp3607.group10.telemetry.core.Clock;
import comp3607.group10.telemetry.core.TelemetrySession;
import comp3607.group10.telemetry.report.ReportExporter;

public class ExportReportsCommand implements Command {
    private final ReportExporter exporter;
    private final TelemetrySession session;
    private final Clock clock;
    private final ReportsWrittenListener onExported;

    public ExportReportsCommand(ReportExporter exporter, TelemetrySession session, Clock clock, ReportsWrittenListener onExported) {
        this.exporter = exporter;
        this.session = session;
        this.clock = clock;
        this.onExported = onExported;
    }

    @Override
    public void execute() throws Exception {
        onExported.reportsWritten(exporter.export(session, clock.nowMillis()));
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
*/
