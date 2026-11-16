package comp3607.group10.telemetry.command;

import comp3607.group10.telemetry.core.Clock;
import comp3607.group10.telemetry.core.TelemetrySession;
import comp3607.group10.telemetry.report.ReportExporter;

/**
Command that writes the reports in the middle of a session. Recording carries on afterwards.

@author Anushka Dwarika
*/
public class ExportReportsCommand implements Command {
    /** Writes the reports. */
    private final ReportExporter exporter;
    /** The session to report on. */
    private final TelemetrySession session;
    /** Gives the time of the export. */
    private final Clock clock;
    /** Told which files were written. */
    private final ReportsWrittenListener onExported;

    /**
    Creates the command.

    @param exporter writes the JSON and PDF reports
    @param session the session being reported on
    @param clock gives the current time
    @param onExported told which files were written
    */
    public ExportReportsCommand(ReportExporter exporter, TelemetrySession session, Clock clock, ReportsWrittenListener onExported) {
        this.exporter = exporter;
        this.session = session;
        this.clock = clock;
        this.onExported = onExported;
    }

    /**
    Writes the reports and reports the files.

    @throws Exception if the work fails
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
