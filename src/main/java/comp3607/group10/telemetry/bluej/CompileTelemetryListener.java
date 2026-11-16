package comp3607.group10.telemetry.bluej;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bluej.extensions2.event.CompileEvent;
import bluej.extensions2.event.CompileListener;

import comp3607.group10.telemetry.command.RecordEventCommand;
import comp3607.group10.telemetry.model.TelemetryEvent;

/**
Listens for compilations the student starts and records their start, each error, and success or
failure. Background compiles and warnings are ignored. Part of the Command pattern: this class is
a client.

@author Anushka Dwarika
*/
public class CompileTelemetryListener implements CompileListener {
    /** The shared objects every listener needs. */
    private final ListenerContext context;

    /**
    Creates the listener.

    @param context the shared objects every listener needs
    */
    public CompileTelemetryListener(ListenerContext context) {
        this.context = context;
    }

    /**
    Records the start of a compilation.

    @param event the telemetry event
    */
    @Override
    public void compileStarted(CompileEvent event) {
        if (event.isUserGeneratedCompilation()) {
            record(context.getFactory().compileStarted(fileNames(event)));
        }
    }

    /**
    Records one compilation error with its file, line and message.

    @param event the telemetry event
    */
    @Override
    public void compileError(CompileEvent event) {
        if (event.isUserGeneratedCompilation()) {
            record(context.getFactory().compileError(firstFileName(event),
                    event.getErrorLineNumber(), event.getErrorMessage()));
        }
    }

    /**
    Ignores compiler warnings, which are out of scope.

    @param event the telemetry event
    */
    @Override
    public void compileWarning(CompileEvent event) {
    }

    /**
    Records a successful compilation.

    @param event the telemetry event
    */
    @Override
    public void compileSucceeded(CompileEvent event) {
        if (event.isUserGeneratedCompilation()) {
            record(context.getFactory().compileSucceeded(fileNames(event)));
        }
    }

    /**
    Records a failed compilation.

    @param event the telemetry event
    */
    @Override
    public void compileFailed(CompileEvent event) {
        if (event.isUserGeneratedCompilation()) {
            record(context.getFactory().compileFailed(fileNames(event)));
        }
    }

    /**
    Wraps the event in a command and submits it.

    @param event the telemetry event
    */
    private void record(TelemetryEvent event) {
        context.submit(new RecordEventCommand(context.getRecorder(), event));
    }

    /**
    Gives the names of the files being compiled, without their folders, so no personal paths are
    stored.

    @param event the telemetry event
    @return the file names
    */
    static List<String> fileNames(CompileEvent event) {
        File[] files = event.getFiles();
        List<String> names = new ArrayList<String>();
        if (files == null) {
            return names;
        }
        for (File file : files) {
            names.add(file.getName());
        }
        return names;
    }

    /**
    Gives the name of the first file in the compilation.

    @param event the telemetry event
    @return the file name, or null if there are no files
    */
    private static String firstFileName(CompileEvent event) {
        List<String> names = fileNames(event);
        if (names.isEmpty()) {
            return null;
        }
        return names.get(0);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
BlueJ. Writing Extensions for BlueJ 5+. https://www.bluej.org/extensions/writingextensions2.html
BlueJ / King's College London. BlueJ source code, Extensions API package bluej.extensions2. https://github.com/k-pet-group/BlueJ-Greenfoot
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
