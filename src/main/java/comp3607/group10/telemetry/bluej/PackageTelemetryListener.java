package comp3607.group10.telemetry.bluej;

import java.util.ArrayList;
import java.util.List;

import bluej.extensions2.BClass;
import bluej.extensions2.BPackage;
import bluej.extensions2.ExtensionException;
import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.event.PackageListener;

import comp3607.group10.telemetry.command.BaselineClassesCommand;
import comp3607.group10.telemetry.command.RecordEventCommand;
import comp3607.group10.telemetry.model.ClassSnapshot;

public class PackageTelemetryListener implements PackageListener {
    private final ListenerContext context;
    private final EditorAttacher editors;

    PackageTelemetryListener(ListenerContext context, EditorAttacher editors) {
        this.context = context;
        this.editors = editors;
    }

    @Override
    public void packageOpened(PackageEvent event) {
        BPackage bPackage = event.getPackage();
        List<ClassSnapshot> existing = new ArrayList<ClassSnapshot>();
        for (BClass bClass : classesOf(bPackage)) {
            existing.add(ClassSnapshotReader.read(bClass, isCompiled(bClass), false));
            editors.attach(bClass);
        }
        context.submit(new BaselineClassesCommand(context.getRecorder(), existing));
        context.submit(new RecordEventCommand(context.getRecorder(), context.getFactory().projectOpened(projectName(bPackage))));
    }

    @Override
    public void packageClosing(PackageEvent event) {
        context.submit(new RecordEventCommand(context.getRecorder(),
                context.getFactory().projectClosed(projectName(event.getPackage()))));
    }

    static String projectName(BPackage bPackage) {
        try {
            return bPackage.getProject().getName();
        } catch (ExtensionException e) {
            return "unknown";
        }
    }

    private static BClass[] classesOf(BPackage bPackage) {
        try {
            BClass[] classes = bPackage.getClasses();
            if (classes == null) {
                return new BClass[0];
            }
            return classes;
        } catch (ExtensionException e) {
            return new BClass[0];
        }
    }

    private static boolean isCompiled(BClass bClass) {
        try {
            return bClass.isCompiled();
        } catch (ExtensionException e) {
            return false;
        }
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
Oracle. The Java Tutorials: Exceptions (Catching and Handling Exceptions). https://docs.oracle.com/javase/tutorial/essential/exceptions/handling.html
*/
