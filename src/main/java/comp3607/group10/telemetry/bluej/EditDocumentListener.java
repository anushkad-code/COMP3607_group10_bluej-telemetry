package comp3607.group10.telemetry.bluej;

import bluej.extensions2.BClass;
import bluej.extensions2.editor.DocumentListener;

import comp3607.group10.telemetry.command.EditCommand;

/**
Listens to one class editor and records how many characters were inserted and deleted. The text
itself is never stored.

@author Anushka Dwarika
*/
public class EditDocumentListener implements DocumentListener {
    /** The shared objects every listener needs. */
    private final ListenerContext context;
    /** The class whose editor this listener watches. */
    private final BClass bClass;

    /**
    Creates a listener for one class editor.

    @param context the shared objects every listener needs
    @param bClass the BlueJ class
    */
    public EditDocumentListener(ListenerContext context, BClass bClass) {
        this.context = context;
        this.bClass = bClass;
    }

    /**
    Records an edit as a command.

    @param start where in the document the change began
    @param replaced the text that was removed
    @param replacement the text that was inserted
    @param linesRemoved how many lines were removed
    @param linesAdded how many lines were added
    */
    @Override
    public void textReplaced(int start, String replaced, String replacement, int linesRemoved, int linesAdded) {
        context.submit(new EditCommand(context.getRecorder(), bClass.getName(), replacement.length(), replaced.length(), context.getClock().nowMillis()));
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
BlueJ. Writing Extensions for BlueJ 5+. https://www.bluej.org/extensions/writingextensions2.html
BlueJ / King's College London. BlueJ source code, Extensions API package bluej.extensions2. https://github.com/k-pet-group/BlueJ-Greenfoot
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
