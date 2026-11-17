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

/**
Creates every part of the extension and connects them. It starts the background worker, recovers
a backup left by a crash, and handles manual export and shutdown. BlueJ code talks to this class
only.

@author Reinaldo Singh
*/
public class TelemetryService {
    /** The version of the extension, shown in BlueJ and on the PDF. */
    public static final String VERSION = "1.0.0";
    /** The logger shared by the extension. */
    private static final Logger LOG = Logger.getLogger("bluej-telemetry");

    /** Gives the current time. */
    private final Clock clock;
    /** The time zone used for readable times and file names. */
    private final ZoneId zone;
    /** Creates telemetry events. */
    private final TelemetryEventFactory factory;
    /** Receives the work from the commands. */
    private final TelemetryRecorder recorder;
    /** Runs the commands on the background thread. */
    private final TelemetryInvoker invoker;
    /** Writes the JSON and PDF reports. */
    private final ReportExporter exporter;
    /** Reads and writes the backup file. */
    private final BackupStore backupStore;
    /** The Observer subject that every recorded event goes through. */
    private final TelemetryHub hub = new TelemetryHub();

    /**
    Creates the service with every dependency given, which lets the tests control time and
    folders.

    @param outputDirectory the folder for the backup, and for reports when no project folder is known
    @param studentIdSeed the value the anonymous student ID is made from
    @param clock gives the current time
    @param zone the time zone used for readable times and file names
    @param backupIntervalMillis how often to back up, in milliseconds
    */
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

    /**
    Creates the service with the real clock, the computer's time zone and a backup every two
    minutes.

    @param outputDirectory the folder for the backup, and for reports when no project folder is known
    @param studentIdSeed the value the anonymous student ID is made from
    */
    public TelemetryService(Path outputDirectory, String studentIdSeed) {
        this(outputDirectory, studentIdSeed, new SystemClock(), ZoneId.systemDefault(),
                BackupObserver.DEFAULT_INTERVAL_MILLIS);
    }

    /**
    Recovers any backup left by a crash, starts the worker thread and records the session start.

    @return the recovered file, or null if there was nothing to recover
    */
    public Path start() {
        Path recovered = recoverLeftoverBackup();
        invoker.start();
        invoker.submit(new RecordEventCommand(recorder, factory.sessionStarted()));
        return recovered;
    }

    /**
    Asks the worker to write the reports now, without ending the session.

    @param onExported told which files were written
    @return true if the request was queued
    */
    public boolean exportNow(ReportsWrittenListener onExported) {
        return invoker.submit(new ExportReportsCommand(exporter, recorder.getSession(), clock, onExported));
    }

    /**
    Ends the session, writes the final reports and stops the worker thread.

    @param timeout the longest time to wait
    @param unit the unit of the timeout
    @param onExported told which files were written
    @return true if everything finished before the timeout
    */
    public boolean shutdown(long timeout, TimeUnit unit, ReportsWrittenListener onExported) {
        invoker.submit(new EndSessionCommand(recorder, factory.sessionEnded(), exporter, backupStore, onExported));
        try {
            return invoker.shutdown(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
    Gives the queue that BlueJ listeners submit commands to.

    @return the command queue
    */
    public CommandQueue getCommandQueue() {
        return invoker;
    }

    /**
    Gives the recorder that commands send their work to.

    @return the recorder
    */
    public TelemetryRecorder getRecorder() {
        return recorder;
    }

    /**
    Gives the factory that listeners use to create events.

    @return the event factory
    */
    public TelemetryEventFactory getEventFactory() {
        return factory;
    }

    /**
    Gives the clock used for every timestamp.

    @return the clock
    */
    public Clock getClock() {
        return clock;
    }

    /**
    Gives the folder used for the backup and for reports when no project folder is known.

    @return the default output folder
    */
    public Path getOutputDirectory() {
        return exporter.getOutputDirectory();
    }

    /**
    Gives the Observer subject, used by the tests.

    @return the hub
    */
    TelemetryHub getHub() {
        return hub;
    }

    /**
    Renames a backup left by a crash so its data is kept.

    @return the recovered file, or null
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
