package comp3607.group10.telemetry.bluej;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bluej.extensions2.event.CompileEvent;
import bluej.extensions2.event.CompileListener;

import comp3607.group10.telemetry.command.RecordEventCommand;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class CompileTelemetryListener implements CompileListener {
    private final ListenerContext context;

    public CompileTelemetryListener(ListenerContext context) {
        this.context = context;
    }

    @Override
    public void compileStarted(CompileEvent event) {
        if (event.isUserGeneratedCompilation()) {
            record(context.getFactory().compileStarted(fileNames(event)));
        }
    }

    @Override
    public void compileError(CompileEvent event) {
        if (event.isUserGeneratedCompilation()) {
            record(context.getFactory().compileError(firstFileName(event),
                    event.getErrorLineNumber(), event.getErrorMessage()));
        }
    }

    @Override
    public void compileWarning(CompileEvent event) {
    }

    @Override
    public void compileSucceeded(CompileEvent event) {
        if (event.isUserGeneratedCompilation()) {
            record(context.getFactory().compileSucceeded(fileNames(event)));
        }
    }

    @Override
    public void compileFailed(CompileEvent event) {
        if (event.isUserGeneratedCompilation()) {
            record(context.getFactory().compileFailed(fileNames(event)));
        }
    }

    private void record(TelemetryEvent event) {
        context.submit(new RecordEventCommand(context.getRecorder(), event));
    }

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
*/
