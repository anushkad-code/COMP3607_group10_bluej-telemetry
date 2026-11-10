package comp3607.group10.telemetry.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;

import comp3607.group10.telemetry.core.TelemetrySession;

public class BackupStore {
    static final String BACKUP_FILE_NAME = "telemetry-backup.json";

    private final Path directory;
    private final JsonReportGenerator json;

    public BackupStore(Path directory, JsonReportGenerator json) {
        this.directory = directory;
        this.json = json;
    }

    public Path backupFile() {
        return directory.resolve(BACKUP_FILE_NAME);
    }

    public void save(TelemetrySession session, long nowMillis) throws IOException {
        json.generate(session, nowMillis, backupFile());
    }

    public void discard() throws IOException {
        Files.deleteIfExists(backupFile());
    }

    public Path recoverLeftover(long nowMillis, ZoneId zone) throws IOException {
        if (!Files.exists(backupFile())) {
            return null;
        }
        Path recovered = directory.resolve("telemetry-recovered-" + Timestamps.forFileName(nowMillis, zone) + ".json");
        return Files.move(backupFile(), recovered);
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
