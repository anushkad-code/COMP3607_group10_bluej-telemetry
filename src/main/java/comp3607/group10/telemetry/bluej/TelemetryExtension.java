package comp3607.group10.telemetry.bluej;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import bluej.extensions2.BlueJ;
import bluej.extensions2.Extension;

import comp3607.group10.telemetry.TelemetryService;

public class TelemetryExtension extends Extension {
    private static final Logger LOG = Logger.getLogger("bluej-telemetry");
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 10;
    static final String REPORT_FOLDER_NAME = "BlueJ Telemetry Reports";

    private BlueJ bluej;
    private TelemetryService service;
    private CompileTelemetryListener compileListener;
    private ClassTelemetryListener classListener;
    private InvocationTelemetryListener invocationListener;
    private PackageTelemetryListener packageListener;

    @Override
    public boolean isCompatible() {
        return getExtensionsAPIVersionMajor() > 3
                || (getExtensionsAPIVersionMajor() == 3 && getExtensionsAPIVersionMinor() >= 3);
    }

    @Override
    public void startup(BlueJ bluej) {
        this.bluej = bluej;

        Path outputDirectory = Paths.get(System.getProperty("user.home"), "Documents", REPORT_FOLDER_NAME);
        service = new TelemetryService(outputDirectory, bluej.getDataCollectionUniqueID());
        Path recovered = service.start();
        if (recovered != null) {
            LOG.info("Recovered telemetry from an unexpected shutdown: " + recovered);
        }

        ListenerContext context = new ListenerContext(service.getCommandQueue(), service.getRecorder(),
                service.getEventFactory(), service.getClock());
        EditorAttacher editors = new EditorAttacher(context);
        compileListener = new CompileTelemetryListener(context);
        classListener = new ClassTelemetryListener(context, editors);
        invocationListener = new InvocationTelemetryListener(context);
        packageListener = new PackageTelemetryListener(context, editors);

        bluej.addCompileListener(compileListener);
        bluej.addClassListener(classListener);
        bluej.addInvocationFinishedListener(invocationListener);
        bluej.addPackageListener(packageListener);
        bluej.setMenuGenerator(new TelemetryMenuGenerator(service));
    }

    @Override
    public void terminate() {
        if (service == null) {
            return;
        }
        bluej.removeCompileListener(compileListener);
        bluej.removeClassListener(classListener);
        bluej.removeInvocationFinishedListener(invocationListener);
        bluej.removePackageListener(packageListener);
        boolean finished = service.shutdown(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS, new LogReportsWritten());
        if (!finished) {
            LOG.warning("Telemetry reports did not finish in time; the backup file in "
                    + service.getOutputDirectory() + " can be used to recover the session.");
        }
        service = null;
    }

    @Override
    public String getName() {
        return "BlueJ Telemetry";
    }

    @Override
    public String getVersion() {
        return TelemetryService.VERSION;
    }

    @Override
    public String getDescription() {
        return "Records compile, run and editing activity (metadata only) and exports JSON and PDF session reports.";
    }

    @Override
    public URL getURL() {
        return null;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
BlueJ. Writing Extensions for BlueJ 5+. https://www.bluej.org/extensions/writingextensions2.html
BlueJ / King's College London. BlueJ source code, Extensions API package bluej.extensions2. https://github.com/k-pet-group/BlueJ-Greenfoot
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
