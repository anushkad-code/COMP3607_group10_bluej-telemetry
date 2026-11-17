package comp3607.group10.telemetry.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;

import comp3607.group10.telemetry.core.TelemetrySession;

/**
Writes, deletes and recovers the backup file telemetry-backup.json.

@author Reinaldo Singh
*/
public class BackupStore {
    /** The name of the backup file. */
    static final String BACKUP_FILE_NAME = "telemetry-backup.json";

    /** The folder the backup is kept in. */
    private final Path directory;
    /** Writes the session as JSON. */
    private final JsonReportGenerator json;

    /**
    Creates the backup store.

    @param directory the folder the backup is kept in
    @param json writes the session as JSON
    */
    public BackupStore(Path directory, JsonReportGenerator json) {
        this.directory = directory;
        this.json = json;
    }

    /**
    Gives the path of the backup file.

    @return the backup file
    */
    public Path backupFile() {
        return directory.resolve(BACKUP_FILE_NAME);
    }

    /**
    Saves the session as the backup file.

    @param session the session being reported on
    @param nowMillis the current time in epoch milliseconds
    @throws IOException if the file cannot be read or written
    */
    public void save(TelemetrySession session, long nowMillis) throws IOException {
        json.generate(session, nowMillis, backupFile());
    }

    /**
    Deletes the backup file after a clean shutdown.

    @throws IOException if the file cannot be read or written
    */
    public void discard() throws IOException {
        Files.deleteIfExists(backupFile());
    }

    /**
    Renames a backup left by a crash to telemetry-recovered-date-time.json, so it is kept.

    @param nowMillis the current time in epoch milliseconds
    @param zone the time zone used for readable times and file names
    @return the recovered file, or null if there was no backup
    @throws IOException if the file cannot be read or written
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
