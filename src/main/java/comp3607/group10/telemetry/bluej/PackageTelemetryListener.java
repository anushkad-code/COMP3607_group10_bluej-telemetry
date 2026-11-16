package comp3607.group10.telemetry.bluej;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import bluej.extensions2.BClass;
import bluej.extensions2.BPackage;
import bluej.extensions2.ExtensionException;
import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.event.PackageListener;

import comp3607.group10.telemetry.command.BaselineClassesCommand;
import comp3607.group10.telemetry.command.ProjectOpenedCommand;
import comp3607.group10.telemetry.command.RecordEventCommand;
import comp3607.group10.telemetry.model.ClassSnapshot;

/**
Listens for BlueJ projects opening and closing. When a project opens it records the project
folder, so the reports can be saved there, and takes a baseline of the classes already in it.
Part of the Command pattern: this class is a client.

@author Anushka Dwarika
*/
public class PackageTelemetryListener implements PackageListener {
    /** The shared objects every listener needs. */
    private final ListenerContext context;
    /** Attaches edit listeners to class editors. */
    private final EditorAttacher editors;

    /**
    Creates the listener.

    @param context the shared objects every listener needs
    @param editors attaches edit listeners to class editors
    */
    PackageTelemetryListener(ListenerContext context, EditorAttacher editors) {
        this.context = context;
        this.editors = editors;
    }

    /**
    Records a project opening, its folder and the classes it already has.

    @param event the telemetry event
    */
    @Override
    public void packageOpened(PackageEvent event) {
        BPackage bPackage = event.getPackage();
        List<ClassSnapshot> existing = new ArrayList<ClassSnapshot>();
        for (BClass bClass : classesOf(bPackage)) {
            existing.add(ClassSnapshotReader.read(bClass, isCompiled(bClass), false));
            editors.attach(bClass);
        }
        context.submit(new BaselineClassesCommand(context.getRecorder(), existing));
        context.submit(new ProjectOpenedCommand(context.getRecorder(), context.getFactory().projectOpened(projectName(bPackage)), projectFolder(bPackage)));
    }

    /**
    Records a project closing.

    @param event the telemetry event
    */
    @Override
    public void packageClosing(PackageEvent event) {
        context.submit(new RecordEventCommand(context.getRecorder(),
                context.getFactory().projectClosed(projectName(event.getPackage()))));
    }

    /**
    Gives the project's name.

    @param bPackage the BlueJ package
    @return the name, or "unknown" if it cannot be read
    */
    static String projectName(BPackage bPackage) {
        try {
            return bPackage.getProject().getName();
        } catch (ExtensionException e) {
            return "unknown";
        }
    }

    /**
    Gives the project's folder.

    @param bPackage the BlueJ package
    @return the folder, or null if it cannot be read
    */
    static Path projectFolder(BPackage bPackage) {
        try {
            File folder = bPackage.getProject().getDir();
            if (folder == null) {
                return null;
            }
            return folder.toPath();
        } catch (ExtensionException e) {
            return null;
        }
    }

    /**
    Gives the classes in a package.

    @param bPackage the BlueJ package
    @return the classes, or an empty array if they cannot be read
    */
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

    /**
    Tells whether a class is compiled.

    @param bClass the BlueJ class
    @return true if compiled, false if not or if it cannot be read
    */
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
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
