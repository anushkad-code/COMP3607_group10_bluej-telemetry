package comp3607.group10.telemetry.bluej;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import bluej.extensions2.BlueJ;
import bluej.extensions2.Extension;

import comp3607.group10.telemetry.TelemetryService;

/**
The entry point BlueJ loads. It starts the telemetry service when BlueJ starts, registers the
listeners and the Tools menu item, and writes the final reports when BlueJ closes.

@author Anushka Dwarika
*/
public class TelemetryExtension extends Extension {
    /** The logger shared by the extension. */
    private static final Logger LOG = Logger.getLogger("bluej-telemetry");
    /** How long to wait for the final reports when BlueJ closes. */
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 10;
    /** The folder inside Documents used for the backup, and for reports when no project was opened. */
    static final String REPORT_FOLDER_NAME = "BlueJ Telemetry Reports";

    /** The BlueJ application object. */
    private BlueJ bluej;
    /** The telemetry service, or null before startup and after terminate. */
    private TelemetryService service;
    /** Listens for compilations. */
    private CompileTelemetryListener compileListener;
    /** Listens for class changes. */
    private ClassTelemetryListener classListener;
    /** Listens for program runs. */
    private InvocationTelemetryListener invocationListener;
    /** Listens for projects opening and closing. */
    private PackageTelemetryListener packageListener;

    /**
    Checks that BlueJ's Extensions API is version 3.3 or later.

    @return true if this BlueJ can run the extension
    */
    @Override
    public boolean isCompatible() {
        return getExtensionsAPIVersionMajor() > 3
                || (getExtensionsAPIVersionMajor() == 3 && getExtensionsAPIVersionMinor() >= 3);
    }

    /**
    Starts the telemetry service, registers the listeners and adds the Tools menu item.

    @param bluej the BlueJ application object
    */
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

    /**
    Removes the listeners and ends the session, waiting up to 10 seconds for the final reports.
    */
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

    /**
    Gives the name shown in BlueJ's list of installed extensions.

    @return the extension name
    */
    @Override
    public String getName() {
        return "BlueJ Telemetry";
    }

    /**
    Gives the extension version.

    @return the version
    */
    @Override
    public String getVersion() {
        return TelemetryService.VERSION;
    }

    /**
    Gives the description shown in BlueJ.

    @return the description
    */
    @Override
    public String getDescription() {
        return "Records compile, run and editing activity (metadata only) and exports JSON and PDF session reports.";
    }

    /**
    Gives a web page for the extension. There is none.

    @return null
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
