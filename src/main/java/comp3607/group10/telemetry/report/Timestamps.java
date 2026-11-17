package comp3607.group10.telemetry.report;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
Formats times for the JSON file, file names and the PDF.

@author Reinaldo Singh
*/
public class Timestamps {
    /** ISO 8601 in UTC with milliseconds, for the JSON file. */
    private static final DateTimeFormatter ISO_UTC =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
    /** Date and time without spaces, for file names. */
    private static final DateTimeFormatter FILE_NAME = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    /** Date and time that people can read, for the PDF. */
    private static final DateTimeFormatter READABLE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
    Not used: this class only has static methods.
    */
    private Timestamps() {
    }

    /**
    Formats a time for the JSON file.

    @param epochMillis the time in epoch milliseconds
    @return for example 2026-11-15T15:00:00.000Z
    */
    public static String iso(long epochMillis) {
        return ISO_UTC.format(Instant.ofEpochMilli(epochMillis));
    }

    /**
    Formats a time for a file name.

    @param epochMillis the time in epoch milliseconds
    @param zone the time zone used for readable times and file names
    @return for example 20261115-110231
    */
    public static String forFileName(long epochMillis, ZoneId zone) {
        return FILE_NAME.withZone(zone).format(Instant.ofEpochMilli(epochMillis));
    }

    /**
    Formats a time for people to read.

    @param epochMillis the time in epoch milliseconds
    @param zone the time zone used for readable times and file names
    @return for example 2026-11-15 11:02:31
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
