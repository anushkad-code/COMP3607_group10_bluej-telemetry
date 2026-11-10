package comp3607.group10.telemetry;

import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import comp3607.group10.telemetry.analytics.StatisticsCalculator;
import comp3607.group10.telemetry.command.CommandQueue;
import comp3607.group10.telemetry.command.EndSessionCommand;
import comp3607.group10.telemetry.command.ExportReportsCommand;
import comp3607.group10.telemetry.command.RecordEventCommand;
import comp3607.group10.telemetry.command.ReportsWrittenListener;
import comp3607.group10.telemetry.command.TelemetryInvoker;
import comp3607.group10.telemetry.core.AnonymousIdProvider;
import comp3607.group10.telemetry.core.ClassTracker;
import comp3607.group10.telemetry.core.Clock;
import comp3607.group10.telemetry.core.EditCoalescer;
import comp3607.group10.telemetry.core.ErrorHandler;
import comp3607.group10.telemetry.core.ErrorCodeClassifier;
import comp3607.group10.telemetry.core.TelemetryEventFactory;
import comp3607.group10.telemetry.core.TelemetryHub;
import comp3607.group10.telemetry.core.TelemetryRecorder;
import comp3607.group10.telemetry.core.TelemetrySession;
import comp3607.group10.telemetry.core.SystemClock;
import comp3607.group10.telemetry.report.BackupObserver;
import comp3607.group10.telemetry.report.BackupStore;
import comp3607.group10.telemetry.report.JsonReportGenerator;
import comp3607.group10.telemetry.report.PdfReportGenerator;
import comp3607.group10.telemetry.report.ReportExporter;
import comp3607.group10.telemetry.report.ReportGenerator;

public class TelemetryService {
    public static final String VERSION = "1.0.0";
    private static final Logger LOG = Logger.getLogger("bluej-telemetry");

    private final Clock clock;
    private final ZoneId zone;
    private final TelemetryEventFactory factory;
    private final TelemetryRecorder recorder;
    private final TelemetryInvoker invoker;
    private final ReportExporter exporter;
    private final BackupStore backupStore;
    private final TelemetryHub hub = new TelemetryHub();

    public TelemetryService(Path outputDirectory, String studentIdSeed, Clock clock, ZoneId zone, long backupIntervalMillis) {
        this.clock = clock;
        this.zone = zone;
        StatisticsCalculator calculator = new StatisticsCalculator();
        JsonReportGenerator json = new JsonReportGenerator(calculator);
        List<ReportGenerator> generators = new ArrayList<ReportGenerator>();
        generators.add(json);
        generators.add(new PdfReportGenerator(calculator, zone, VERSION));
        TelemetrySession session = new TelemetrySession(AnonymousIdProvider.fromSeed(studentIdSeed), clock.nowMillis());

        this.factory = new TelemetryEventFactory(clock, new ErrorCodeClassifier());
        this.recorder = new TelemetryRecorder(hub, session, new ClassTracker(), new EditCoalescer());
        ErrorHandler logger = new LogErrorHandler();
        this.invoker = new TelemetryInvoker(logger);
        this.exporter = new ReportExporter(generators, outputDirectory, zone);
        this.backupStore = new BackupStore(outputDirectory, json);

        hub.register(session);
        hub.register(new BackupObserver(backupStore, session, clock, backupIntervalMillis, logger));
    }

    public TelemetryService(Path outputDirectory, String studentIdSeed) {
        this(outputDirectory, studentIdSeed, new SystemClock(), ZoneId.systemDefault(),
                BackupObserver.DEFAULT_INTERVAL_MILLIS);
    }

    public Path start() {
        Path recovered = recoverLeftoverBackup();
        invoker.start();
        invoker.submit(new RecordEventCommand(recorder, factory.sessionStarted()));
        return recovered;
    }

    public boolean exportNow(ReportsWrittenListener onExported) {
        return invoker.submit(new ExportReportsCommand(exporter, recorder.getSession(), clock, onExported));
    }

    public boolean shutdown(long timeout, TimeUnit unit, ReportsWrittenListener onExported) {
        invoker.submit(new EndSessionCommand(recorder, factory.sessionEnded(), exporter, backupStore, onExported));
        try {
            return invoker.shutdown(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public CommandQueue getCommandQueue() {
        return invoker;
    }

    public TelemetryRecorder getRecorder() {
        return recorder;
    }

    public TelemetryEventFactory getEventFactory() {
        return factory;
    }

    public Clock getClock() {
        return clock;
    }

    public Path getOutputDirectory() {
        return exporter.getOutputDirectory();
    }

    TelemetryHub getHub() {
        return hub;
    }

    private Path recoverLeftoverBackup() {
        try {
            return backupStore.recoverLeftover(clock.nowMillis(), zone);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Telemetry task failed", e);
            return null;
        }
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: SOLID, Week 3 lecture notes.
COMP 3607 (n.d.). OOP Design Principles: SOLID - Dependency Inversion, Week 4 lecture notes.
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Exceptions (Catching and Handling Exceptions). https://docs.oracle.com/javase/tutorial/essential/exceptions/handling.html
*/
