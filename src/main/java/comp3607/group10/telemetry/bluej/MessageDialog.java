package comp3607.group10.telemetry.bluej;

import javafx.scene.control.Alert;

/**
An information dialog that tells the student where the exported reports were saved.

@author Anushka Dwarika
*/
public class MessageDialog implements Runnable {
    /** The text shown in the dialog. */
    private final String message;

    /**
    Creates the dialog.

    @param message the message
    */
    public MessageDialog(String message) {
        this.message = message;
    }

    /**
    Shows the dialog without blocking BlueJ.
    */
    @Override
    public void run() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText("Telemetry report exported");
        alert.show();
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
OpenJFX. JavaFX 21 API. https://openjfx.io/javadoc/21/
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
