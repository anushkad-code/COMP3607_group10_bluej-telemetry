package comp3607.group10.telemetry.report;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import comp3607.group10.telemetry.analytics.ActivityStats;
import comp3607.group10.telemetry.analytics.ErrorFrequency;
import comp3607.group10.telemetry.analytics.SessionStatistics;
import comp3607.group10.telemetry.analytics.StatisticsCalculator;
import comp3607.group10.telemetry.analytics.TimelineEntry;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class PdfReportGenerator extends AbstractReportGenerator<PdfCanvas> {
    public static final int MAX_PAGES = 1;
    static final int MAX_ERROR_ROWS = 5;

    private static final Color ACTIVE = new Color(214, 96, 20);
    private static final Color IDLE = new Color(250, 204, 60);
    private static final Color ERROR_BAR = new Color(230, 126, 34);

    private final ZoneId zone;
    private final String pluginVersion;

    public PdfReportGenerator(StatisticsCalculator calculator, ZoneId zone, String pluginVersion) {
        super(calculator);
        this.zone = zone;
        this.pluginVersion = pluginVersion;
    }

    @Override
    public String fileExtension() {
        return "pdf";
    }

    @Override
    protected PdfCanvas createDocument(SessionStatistics statistics) throws IOException {
        PdfCanvas pdf = new PdfCanvas(MAX_PAGES);
        pdf.setDocumentTitle("BlueJ Coding Session Report", statistics.getStudentId());
        return pdf;
    }

    @Override
    protected void addSummary(PdfCanvas pdf, SessionStatistics s) throws IOException {
        pdf.header("BlueJ Coding Session Report", projectLine(s.getProjectNames()), "Student ID: " + s.getStudentId() + "   |   " + Timestamps.readable(s.getSessionStartMillis(), zone) + " to " + Timestamps.readable(s.getSessionEndMillis(), zone) + "   |   Duration: " + minutes(s.getSessionDurationSec()));

        ActivityStats activity = s.getActivity();
        pdf.section("Active Coding Time vs. Idle Time");
        List<String> labels = new ArrayList<String>();
        labels.add("Active (" + percent(activity.getActiveShare()) + ")");
        labels.add("Idle (" + percent(idleShare(activity)) + ")");
        List<Double> values = new ArrayList<Double>();
        values.add((double) activity.getActiveTimeSec());
        values.add((double) activity.getIdleTimeSec());
        List<Color> colours = new ArrayList<Color>();
        colours.add(ACTIVE);
        colours.add(IDLE);
        List<List<String>> rows = new ArrayList<List<String>>();
        rows.add(row("Active coding time", minutes(activity.getActiveTimeSec()), percent(activity.getActiveShare())));
        rows.add(row("Idle time", minutes(activity.getIdleTimeSec()), percent(idleShare(activity))));
        rows.add(row("Total", minutes(activity.getActiveTimeSec() + activity.getIdleTimeSec()), "100%"));
        float half = (pdf.contentWidth() - 24) / 2;
        float top = pdf.top();
        float pieHeight = pdf.pie(pdf.left() + 10, top, 34, labels, values, colours);
        float tableHeight = pdf.table(pdf.left() + half + 24, top, half, row("Measure", "Time", "Share"), rows, new float[] {0.46f, 0.32f, 0.22f});
        pdf.moveDown(Math.max(pieHeight, tableHeight));
        pdf.divider();
    }

    @Override
    protected void addDetails(PdfCanvas pdf, SessionStatistics s, List<TelemetryEvent> events) throws IOException {
        float half = (pdf.contentWidth() - 24) / 2;
        pdf.section("Frequency Breakdown of Compilation Errors");
        if (s.getErrorFrequency().isEmpty()) {
            pdf.note("No compilation errors were recorded in this session.");
        } else {
            int total = s.getTotalCompileErrors();
            List<String> names = new ArrayList<String>();
            List<Integer> counts = new ArrayList<Integer>();
            List<List<String>> rows = new ArrayList<List<String>>();
            for (ErrorFrequency error : s.getErrorFrequency()) {
                if (names.size() < MAX_ERROR_ROWS) {
                    names.add(readableName(error.getErrorCode()));
                    counts.add(error.getCount());
                    rows.add(row(readableName(error.getErrorCode()), String.valueOf(error.getCount()), share(error.getCount(), total)));
                }
            }
            float top = pdf.top();
            float tableHeight = pdf.table(pdf.left(), top, half, row("Error type", "Count", "Share"), rows, new float[] {0.58f, 0.18f, 0.24f});
            float barHeight = pdf.bars(pdf.left() + half + 24, top, half, names, counts, ERROR_BAR);
            pdf.moveDown(Math.max(tableHeight, barHeight));
            if (s.getErrorFrequency().size() > MAX_ERROR_ROWS) {
                pdf.moveDown(4);
                pdf.note("Showing the " + MAX_ERROR_ROWS + " most frequent error types; all " + total + " errors are in the JSON file.");
            }
        }
        pdf.divider();

        pdf.section("Progression Timeline of Class Creations and Method Definitions");
        addTimeline(pdf, s.getTimeline(), half);
    }

    @Override
    protected void addFooter(PdfCanvas pdf, SessionStatistics s) throws IOException {
        pdf.footerOnEveryPage("BlueJ Telemetry Extension " + pluginVersion);
    }

    @Override
    protected void save(PdfCanvas pdf, Path file) throws IOException {
        pdf.save(file);
    }

    @Override
    protected void close(PdfCanvas pdf) throws IOException {
        pdf.close();
    }

    private void addTimeline(PdfCanvas pdf, List<TimelineEntry> timeline, float half) throws IOException {
        if (timeline.isEmpty()) {
            pdf.note("No classes or methods were created in this session.");
            return;
        }
        int classes = 0;
        int methods = 0;
        for (TimelineEntry entry : timeline) {
            if (entry.getMethodName() == null) {
                classes++;
            } else {
                methods++;
            }
        }
        List<String> labels = new ArrayList<String>();
        labels.add("Classes created");
        labels.add("Methods defined");
        List<Integer> counts = new ArrayList<Integer>();
        counts.add(classes);
        counts.add(methods);
        int roomForRows = pdf.rowsThatFit(PdfCanvas.ROW_HEIGHT + 20);
        List<List<String>> rows = new ArrayList<List<String>>();
        for (TimelineEntry entry : timeline) {
            if (rows.size() < roomForRows) {
                rows.add(row(Timestamps.readable(entry.getTimestampMillis(), zone), entry.describe()));
            }
        }
        float wide = pdf.contentWidth() * 0.62f;
        float top = pdf.top();
        float tableHeight = pdf.table(pdf.left(), top, wide, row("Time", "Event"), rows, new float[] {0.40f, 0.60f});
        float barHeight = pdf.bars(pdf.left() + wide + 20, top, pdf.contentWidth() - wide - 20, labels, counts, ACTIVE);
        pdf.moveDown(Math.max(tableHeight, barHeight));
        if (timeline.size() > rows.size()) {
            pdf.moveDown(4);
            pdf.note("A further " + (timeline.size() - rows.size()) + " entries are listed in the JSON file.");
        }
    }

    static String projectLine(List<String> projectNames) {
        if (projectNames.isEmpty()) {
            return "BlueJ project: not recorded";
        }
        String label = "BlueJ project: ";
        if (projectNames.size() > 1) {
            label = "BlueJ projects: ";
        }
        String names = "";
        for (int i = 0; i < projectNames.size(); i++) {
            if (i > 0) {
                names = names + ", ";
            }
            names = names + projectNames.get(i);
        }
        return label + names;
    }

    static String share(int count, int total) {
        if (total == 0) {
            return "0%";
        }
        return percent((double) count / total);
    }

    static String readableName(String errorCode) {
        String lower = errorCode.replace('_', ' ').toLowerCase();
        if (lower.isEmpty()) {
            return lower;
        }
        return lower.substring(0, 1).toUpperCase() + lower.substring(1);
    }

    static double idleShare(ActivityStats activity) {
        long total = activity.getActiveTimeSec() + activity.getIdleTimeSec();
        if (total == 0) {
            return 0;
        }
        return (double) activity.getIdleTimeSec() / total;
    }

    static String percent(double fraction) {
        return Math.round(fraction * 100) + "%";
    }

    static String minutes(long seconds) {
        long secondsLeft = seconds % 60;
        String padding = "";
        if (secondsLeft < 10) {
            padding = "0";
        }
        return (seconds / 60) + " min " + padding + secondsLeft + " s";
    }

    private static List<String> row(String first, String second) {
        List<String> row = new ArrayList<String>();
        row.add(first);
        row.add(second);
        return row;
    }

    private static List<String> row(String first, String second, String third) {
        List<String> row = row(first, second);
        row.add(third);
        return row;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Template, Week 6 lecture notes.
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Template Method Design Pattern. https://sourcemaking.com/design_patterns/template_method
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
The Apache Software Foundation. Apache PDFBox 3.0. https://pdfbox.apache.org/
CV Genius (n.d.). Computer Science CV Example (layout model for the report). https://cvgenius.com/
W3C (2018). Web Content Accessibility Guidelines (WCAG) 2.1: 1.4.1 Use of Color and 1.4.3 Contrast (Minimum). https://www.w3.org/TR/WCAG21/
*/
