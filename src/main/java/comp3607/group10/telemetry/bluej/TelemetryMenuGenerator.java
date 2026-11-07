package comp3607.group10.telemetry.bluej;

import bluej.extensions2.BPackage;
import bluej.extensions2.MenuGenerator;
import javafx.scene.control.MenuItem;

import comp3607.group10.telemetry.TelemetryService;

public class TelemetryMenuGenerator extends MenuGenerator {
    static final String MENU_LABEL = "Export Telemetry Report";

    private final TelemetryService service;

    public TelemetryMenuGenerator(TelemetryService service) {
        this.service = service;
    }

    @Override
    public MenuItem getToolsMenuItem(BPackage bPackage) {
        MenuItem item = new MenuItem(MENU_LABEL);
        item.setOnAction(new ExportMenuAction(service));
        return item;
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
OpenJFX. JavaFX 21 API. https://openjfx.io/javadoc/21/
*/
