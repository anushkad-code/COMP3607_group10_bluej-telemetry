package comp3607.group10.telemetry.bluej;

import java.nio.file.Path;
import java.util.List;

import javafx.application.Platform;

import comp3607.group10.telemetry.command.ReportsWrittenListener;

public class ExportedFilesMessage implements ReportsWrittenListener {
    @Override
    public void reportsWritten(List<Path> files) {
        String list = "";
        for (Path file : files) {
            list = list + file.toString() + "\n";
        }
        Platform.runLater(new MessageDialog("Reports saved:\n" + list));
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
SourceMaking. Observer Design Pattern. https://sourcemaking.com/design_patterns/observer
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
OpenJFX. JavaFX 21 API. https://openjfx.io/javadoc/21/
*/
