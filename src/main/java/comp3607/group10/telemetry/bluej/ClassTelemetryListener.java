package comp3607.group10.telemetry.bluej;

import bluej.extensions2.BClass;
import bluej.extensions2.event.ClassEvent;
import bluej.extensions2.event.ClassListener;

import comp3607.group10.telemetry.command.ClassDeletedCommand;
import comp3607.group10.telemetry.command.ClassRenamedCommand;
import comp3607.group10.telemetry.command.ClassStateCommand;

public class ClassTelemetryListener implements ClassListener {
    private final ListenerContext context;
    private final EditorAttacher editors;

    ClassTelemetryListener(ListenerContext context, EditorAttacher editors) {
        this.context = context;
        this.editors = editors;
    }

    @Override
    public void classStateChanged(ClassEvent event) {
        BClass bClass = event.getBClass();
        editors.attach(bClass);
        context.submit(new ClassStateCommand(context.getRecorder(),
                ClassSnapshotReader.read(bClass, event.isClassCompiled(), event.hasError()),
                context.getClock().nowMillis()));
    }

    @Override
    public void classNameChanged(ClassEvent event) {
        String newName = event.getBClass().getName();
        editors.renamed(event.getOldName(), newName);
        context.submit(new ClassRenamedCommand(context.getRecorder(),
                context.getFactory().classRenamed(newName, event.getOldName())));
    }

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
*/
