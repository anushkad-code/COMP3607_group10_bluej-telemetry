package comp3607.group10.telemetry.bluej;

import java.util.ArrayList;
import java.util.List;

import bluej.extensions2.BClass;
import bluej.extensions2.editor.JavaEditor;

class EditorAttacher {
    private final ListenerContext context;
    private final List<String> attached = new ArrayList<String>();

    EditorAttacher(ListenerContext context) {
        this.context = context;
    }

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

    synchronized void renamed(String oldName, String newName) {
        if (attached.remove(oldName)) {
            attached.add(newName);
        }
    }

    synchronized void forget(String className) {
        attached.remove(className);
    }

    synchronized boolean isAttached(String className) {
        return attached.contains(className);
    }

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
*/
