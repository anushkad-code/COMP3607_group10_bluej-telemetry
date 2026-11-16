package comp3607.group10.telemetry.bluej;

import bluej.extensions2.BClass;
import bluej.extensions2.event.ClassEvent;
import bluej.extensions2.event.ClassListener;

import comp3607.group10.telemetry.command.ClassDeletedCommand;
import comp3607.group10.telemetry.command.ClassRenamedCommand;
import comp3607.group10.telemetry.command.ClassStateCommand;

/**
Listens for BlueJ class events (state changes, renames and removals) and turns each one into a
command. Part of the Command pattern: this class is a client.

@author Anushka Dwarika
*/
public class ClassTelemetryListener implements ClassListener {
    /** The shared objects every listener needs. */
    private final ListenerContext context;
    /** Attaches edit listeners to class editors. */
    private final EditorAttacher editors;

    /**
    Creates the listener.

    @param context the shared objects every listener needs
    @param editors attaches edit listeners to class editors
    */
    ClassTelemetryListener(ListenerContext context, EditorAttacher editors) {
        this.context = context;
        this.editors = editors;
    }

    /**
    Records a class's new state, so creations, modifications and new methods can be detected.

    @param event the telemetry event
    */
    @Override
    public void classStateChanged(ClassEvent event) {
        BClass bClass = event.getBClass();
        editors.attach(bClass);
        context.submit(new ClassStateCommand(context.getRecorder(),
                ClassSnapshotReader.read(bClass, event.isClassCompiled(), event.hasError()),
                context.getClock().nowMillis()));
    }

    /**
    Records a class being renamed.

    @param event the telemetry event
    */
    @Override
    public void classNameChanged(ClassEvent event) {
        String newName = event.getBClass().getName();
        editors.renamed(event.getOldName(), newName);
        context.submit(new ClassRenamedCommand(context.getRecorder(),
                context.getFactory().classRenamed(newName, event.getOldName())));
    }

    /**
    Records a class being removed.

    @param event the telemetry event
    */
    @Override
    public void classRemoved(ClassEvent event) {
        String name = event.getBClass().getName();
        editors.forget(name);
        context.submit(new ClassDeletedCommand(context.getRecorder(), context.getFactory().classDeleted(name)));
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
