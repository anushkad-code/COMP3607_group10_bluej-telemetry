package comp3607.group10.telemetry.bluej;

import java.util.ArrayList;
import java.util.List;

import bluej.extensions2.BClass;
import bluej.extensions2.editor.JavaEditor;

/**
Attaches exactly one EditDocumentListener to each class editor, and remembers which classes
already have one.

@author Anushka Dwarika
*/
class EditorAttacher {
    /** The shared objects every listener needs. */
    private final ListenerContext context;
    /** The names of the classes whose editors already have a listener. */
    private final List<String> attached = new ArrayList<String>();

    /**
    Creates the attacher.

    @param context the shared objects every listener needs
    */
    EditorAttacher(ListenerContext context) {
        this.context = context;
    }

    /**
    Attaches an edit listener to the class's editor, unless it already has one.

    @param bClass the BlueJ class
    */
    synchronized void attach(BClass bClass) {
        String name = bClass.getName();
        if (attached.contains(name)) {
            return;
        }
        JavaEditor editor = editorOf(bClass);
        if (editor != null) {
            attached.add(name);
            editor.addDocumentListener(new EditDocumentListener(context, bClass));
        }
    }

    /**
    Updates the remembered name after a class is renamed.

    @param oldName the old class name
    @param newName the new class name
    */
    synchronized void renamed(String oldName, String newName) {
        if (attached.remove(oldName)) {
            attached.add(newName);
        }
    }

    /**
    Forgets a class after it is removed.

    @param className the name of the class
    */
    synchronized void forget(String className) {
        attached.remove(className);
    }

    /**
    Tells whether a class's editor already has a listener.

    @param className the name of the class
    @return true if it is attached
    */
    synchronized boolean isAttached(String className) {
        return attached.contains(className);
    }

    /**
    Gets the Java editor for a class.

    @param bClass the BlueJ class
    @return the editor, or null if the class has none
    */
    private static JavaEditor editorOf(BClass bClass) {
        try {
            return bClass.getJavaEditor();
        } catch (Exception e) {
            return null;
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
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
