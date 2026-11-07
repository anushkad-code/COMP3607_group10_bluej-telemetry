package comp3607.group10.telemetry.bluej;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import comp3607.group10.telemetry.TelemetryService;

public class ExportMenuAction implements EventHandler<ActionEvent> {
    private final TelemetryService service;

    public ExportMenuAction(TelemetryService service) {
        this.service = service;
    }

    @Override
    public void handle(ActionEvent event) {
        service.exportNow(new ExportedFilesMessage());
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
OpenJFX. JavaFX 21 API. https://openjfx.io/javadoc/21/
*/
