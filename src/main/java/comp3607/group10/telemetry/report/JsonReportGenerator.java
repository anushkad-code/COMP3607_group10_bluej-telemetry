package comp3607.group10.telemetry.report;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import comp3607.group10.telemetry.analytics.SessionStatistics;
import comp3607.group10.telemetry.analytics.StatisticsCalculator;
import comp3607.group10.telemetry.model.EventTimeComparator;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class JsonReportGenerator extends AbstractReportGenerator<JsonObject> {
    public static final int SCHEMA_VERSION = 1;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    public JsonReportGenerator(StatisticsCalculator calculator) {
        super(calculator);
    }

    @Override
    public String fileExtension() {
        return "json";
    }

    @Override
    protected JsonObject createDocument(SessionStatistics statistics) {
        JsonObject root = new JsonObject();
        root.addProperty("schemaVersion", SCHEMA_VERSION);
        return root;
    }

    @Override
    protected void addSummary(JsonObject document, SessionStatistics statistics) {
        document.add("metadata", gson.toJsonTree(toMetadata(statistics)));
    }

    @Override
    protected void addDetails(JsonObject document, SessionStatistics statistics, List<TelemetryEvent> events) {
        JsonArray array = new JsonArray();
        List<TelemetryEvent> sorted = new ArrayList<TelemetryEvent>(events);
        Collections.sort(sorted, new EventTimeComparator());
        for (TelemetryEvent event : sorted) {
            array.add(gson.toJsonTree(toJsonEvent(event)));
        }
        document.add("events", array);
    }

    @Override
    protected void save(JsonObject document, Path file) throws IOException {
        Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
        try {
            gson.toJson(document, writer);
            writer.close();
        } catch (Exception e) {
            writer.close();
            throw new IOException("Could not write the JSON file " + file + ": " + e.getMessage(), e);
        }
    }

    static JsonMetadata toMetadata(SessionStatistics s) {
        JsonMetadata m = new JsonMetadata();
        m.studentID = s.getStudentId();
        m.sessionStart = Timestamps.iso(s.getSessionStartMillis());
        m.sessionEnd = Timestamps.iso(s.getSessionEndMillis());
        m.sessionDurationSec = s.getSessionDurationSec();
        m.totalCompilations = s.getCompiles().getTotal();
        m.successfulCompilations = s.getCompiles().getSuccessful();
        m.failedCompilations = s.getCompiles().getFailed();
        m.totalRuns = s.getRuns().getTotalRuns();
        m.runtimeExceptions = s.getRuns().getRuntimeExceptions();
        m.activeTimeSec = s.getActivity().getActiveTimeSec();
        m.idleTimeSec = s.getActivity().getIdleTimeSec();
        m.totalEdits = s.getActivity().getTotalEdits();
        m.typingCadenceCpm = round(s.getActivity().getTypingCadenceCpm());
        m.averageCompileIntervalSec = round(s.getCompiles().getAverageIntervalSec());
        return m;
    }

    static JsonEvent toJsonEvent(TelemetryEvent event) {
        JsonEvent e = new JsonEvent();
        e.type = event.getType().name();
        e.timestamp = Timestamps.iso(event.getTimestampMillis());
        e.className = event.getClassName();
        e.details = event.getDetails();
        return e;
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Template, Week 6 lecture notes.
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Template Method Design Pattern. https://sourcemaking.com/design_patterns/template_method
Google. Gson User Guide. https://github.com/google/gson/blob/main/UserGuide.md
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Oracle. The Java Tutorials: Exceptions (Catching and Handling Exceptions). https://docs.oracle.com/javase/tutorial/essential/exceptions/handling.html
*/
