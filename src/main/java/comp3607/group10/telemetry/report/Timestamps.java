package comp3607.group10.telemetry.report;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Timestamps {
    private static final DateTimeFormatter ISO_UTC =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter FILE_NAME = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final DateTimeFormatter READABLE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Timestamps() {
    }

    public static String iso(long epochMillis) {
        return ISO_UTC.format(Instant.ofEpochMilli(epochMillis));
    }

    public static String forFileName(long epochMillis, ZoneId zone) {
        return FILE_NAME.withZone(zone).format(Instant.ofEpochMilli(epochMillis));
    }

    public static String readable(long epochMillis, ZoneId zone) {
        return READABLE.withZone(zone).format(Instant.ofEpochMilli(epochMillis));
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: Introduction to Refactoring, Week 4 lecture notes.
COMP 3607 (n.d.). Refactoring: Code Smells, Week 9 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Fowler, M. (2018). Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley.
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
