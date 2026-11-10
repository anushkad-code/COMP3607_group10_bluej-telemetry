package comp3607.group10.telemetry.report;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class PdfCanvas implements AutoCloseable {
    static final float MARGIN = 48;
    static final float ROW_HEIGHT = 15;
    private static final float TEXT_SIZE = 9.5f;
    private static final PDRectangle PAGE_SIZE = PDRectangle.A4;
    private static final Color INK = new Color(40, 32, 26);
    private static final Color HEADING = new Color(168, 70, 8);
    private static final Color ORANGE = new Color(232, 120, 22);
    private static final Color SOFT_TEXT = new Color(95, 80, 66);
    private static final Color RULE = new Color(226, 196, 140);
    private static final Color HEADER_FILL = new Color(255, 236, 170);

    private final PDDocument document = new PDDocument();
    private final PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
    private final PDType1Font bold = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);
    private final PDType1Font italic = new PDType1Font(Standard14Fonts.FontName.TIMES_ITALIC);
    private final int maxPages;
    private PDPageContentStream stream;
    private float y;

    public PdfCanvas(int maxPages) throws IOException {
        this.maxPages = maxPages;
        document.getDocumentCatalog().setLanguage("en");
        newPage();
    }

    public void setDocumentTitle(String title, String author) {
        PDDocumentInformation information = new PDDocumentInformation();
        information.setTitle(title);
        information.setAuthor(author);
        document.setDocumentInformation(information);
    }

    public float contentWidth() {
        return PAGE_SIZE.getWidth() - 2 * MARGIN;
    }

    public float left() {
        return MARGIN;
    }

    public float top() {
        return y;
    }

    public int pageCount() {
        return document.getNumberOfPages();
    }

    public float remainingHeight() {
        return y - MARGIN;
    }

    public int rowsThatFit(float spaceToKeep) {
        int rows = (int) ((remainingHeight() - spaceToKeep) / ROW_HEIGHT);
        if (rows < 0) {
            return 0;
        }
        return rows;
    }

    public boolean ensureSpace(float height) throws IOException {
        if (remainingHeight() >= height) {
            return true;
        }
        if (pageCount() >= maxPages) {
            return false;
        }
        newPage();
        return true;
    }

    public void moveDown(float height) {
        y = y - height;
    }

    public void header(String title, String subtitle, String details) throws IOException {
        float centre = MARGIN + contentWidth() / 2;
        writeCentred(title.toUpperCase(), bold, 22, centre, y - 22, HEADING);
        y -= 38;
        writeCentred(subtitle, italic, 11, centre, y, SOFT_TEXT);
        y -= 16;
        writeCentred(fit(details, regular, TEXT_SIZE, contentWidth()), regular, TEXT_SIZE, centre, y, INK);
        y -= 12;
        divider();
    }

    public void section(String title) throws IOException {
        y -= 16;
        stream.beginText();
        stream.setFont(bold, 11.5f);
        stream.setNonStrokingColor(HEADING);
        stream.setCharacterSpacing(0.8f);
        stream.newLineAtOffset(MARGIN, y - 11.5f);
        stream.showText(sanitise(title.toUpperCase()));
        stream.setCharacterSpacing(0);
        stream.endText();
        y -= 22;
    }

    public void divider() throws IOException {
        y -= 6;
        line(MARGIN, y, MARGIN + contentWidth(), y, RULE, 0.75f);
        stream.setNonStrokingColor(ORANGE);
        stream.addRect(MARGIN, y - 1.5f, 48, 3);
        stream.fill();
        y -= 4;
    }

    public float caption(float x, float top, String text) throws IOException {
        write(text, italic, TEXT_SIZE, x, top - TEXT_SIZE, SOFT_TEXT);
        return 14;
    }

    public void note(String text) throws IOException {
        y -= caption(MARGIN, y, fit(text, italic, TEXT_SIZE, contentWidth()));
    }

    public float table(float x, float top, float width, List<String> headers, List<List<String>> rows, float[] columnShares) throws IOException {
        float[] widths = new float[columnShares.length];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = columnShares[i] * width;
        }
        float rowTop = top;
        tableRow(x, rowTop, width, headers, widths, bold, true);
        rowTop -= ROW_HEIGHT;
        for (List<String> row : rows) {
            tableRow(x, rowTop, width, row, widths, regular, false);
            rowTop -= ROW_HEIGHT;
        }
        return top - rowTop;
    }

    public float bars(float x, float top, float width, List<String> labels, List<Integer> values, Color barColor) throws IOException {
        float labelWidth = width * 0.42f;
        float maxBar = width - labelWidth - 26;
        int max = 0;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) > max) {
                max = values.get(i);
            }
        }
        float rowTop = top - 4;
        for (int i = 0; i < labels.size(); i++) {
            float barWidth = 0;
            if (max > 0) {
                barWidth = maxBar * values.get(i) / max;
            }
            write(fit(labels.get(i), regular, TEXT_SIZE, labelWidth - 6), regular, TEXT_SIZE, x, rowTop - 10.5f, INK);
            stream.setNonStrokingColor(barColor);
            stream.addRect(x + labelWidth, rowTop - 12, Math.max(barWidth, 1), 9);
            stream.fill();
            write(String.valueOf(values.get(i)), bold, TEXT_SIZE, x + labelWidth + barWidth + 4, rowTop - 10.5f, INK);
            rowTop -= ROW_HEIGHT;
        }
        line(x + labelWidth, top - 4, x + labelWidth, rowTop, INK, 0.6f);
        return top - rowTop;
    }

    public float pie(float x, float top, float radius, List<String> labels, List<Double> values, List<Color> colors) throws IOException {
        double total = 0;
        for (int i = 0; i < values.size(); i++) {
            total = total + values.get(i);
        }
        float cx = x + radius;
        float cy = top - radius - 4;
        double angle = Math.PI / 2;
        if (total > 0) {
            for (int i = 0; i < values.size(); i++) {
                double sweep = 2 * Math.PI * values.get(i) / total;
                if (sweep > 0) {
                    wedge(cx, cy, radius, angle, sweep, colors.get(i));
                    angle = angle - sweep;
                }
            }
        }
        stream.setStrokingColor(INK);
        stream.setLineWidth(0.6f);
        circleOutline(cx, cy, radius);
        float legendX = cx + radius + 16;
        float legendTop = cy + labels.size() * ROW_HEIGHT / 2;
        for (int i = 0; i < labels.size(); i++) {
            stream.setNonStrokingColor(colors.get(i));
            stream.addRect(legendX, legendTop - 11, 10, 10);
            stream.fill();
            stream.setStrokingColor(INK);
            stream.setLineWidth(0.5f);
            stream.addRect(legendX, legendTop - 11, 10, 10);
            stream.stroke();
            write(labels.get(i), regular, TEXT_SIZE, legendX + 16, legendTop - 9.5f, INK);
            legendTop = legendTop - ROW_HEIGHT;
        }
        return 2 * radius + 8;
    }

    public void footerOnEveryPage(String text) throws IOException {
        closeStream();
        int pageNumber = 1;
        for (PDPage page : document.getPages()) {
            PDPageContentStream footer = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
            try {
                String pageText = "Page " + pageNumber + " of " + pageCount();
                float baseline = MARGIN / 2;
                footer.setStrokingColor(RULE);
                footer.setLineWidth(0.75f);
                footer.moveTo(MARGIN, baseline + 12);
                footer.lineTo(MARGIN + contentWidth(), baseline + 12);
                footer.stroke();
                footer.beginText();
                footer.setFont(italic, 8.5f);
                footer.setNonStrokingColor(SOFT_TEXT);
                footer.newLineAtOffset(MARGIN, baseline);
                footer.showText(sanitise(text));
                footer.endText();
                footer.beginText();
                footer.setFont(italic, 8.5f);
                footer.newLineAtOffset(MARGIN + contentWidth() - textWidth(pageText, italic, 8.5f), baseline);
                footer.showText(pageText);
                footer.endText();
                footer.close();
            } catch (Exception e) {
                footer.close();
                throw new IOException("Could not draw the page footer: " + e.getMessage(), e);
            }
            pageNumber++;
        }
    }

    public void save(Path file) throws IOException {
        closeStream();
        document.save(file.toFile());
    }

    @Override
    public void close() throws IOException {
        closeStream();
        document.close();
    }

    static String sanitise(String text) {
        if (text == null) {
            return "";
        }
        String out = "";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= ' ' && c <= '~') {
                out = out + c;
            } else {
                out = out + replacementFor(c);
            }
        }
        return out;
    }

    private static char replacementFor(char c) {
        if (c == '\t' || c == '\n' || c == '\r') {
            return ' ';
        }
        return '?';
    }

    String fit(String text, PDType1Font font, float size, float width) throws IOException {
        String clean = sanitise(text);
        if (textWidth(clean, font, size) <= width) {
            return clean;
        }
        String shortened = clean;
        while (!shortened.isEmpty() && textWidth(shortened + "...", font, size) > width) {
            shortened = shortened.substring(0, shortened.length() - 1);
        }
        return shortened + "...";
    }

    private void tableRow(float x, float top, float width, List<String> cells, float[] widths, PDType1Font font, boolean header) throws IOException {
        if (header) {
            stream.setNonStrokingColor(HEADER_FILL);
            stream.addRect(x, top - ROW_HEIGHT, width, ROW_HEIGHT);
            stream.fill();
        }
        float cellX = x;
        for (int i = 0; i < widths.length; i++) {
            String cell = "";
            if (i < cells.size()) {
                cell = cells.get(i);
            }
            write(fit(cell, font, TEXT_SIZE, widths[i] - 8), font, TEXT_SIZE, cellX + 4, top - 10.5f, INK);
            cellX += widths[i];
        }
        Color lineColour = RULE;
        float lineWidth = 0.5f;
        if (header) {
            lineColour = ORANGE;
            lineWidth = 1f;
        }
        line(x, top - ROW_HEIGHT, x + width, top - ROW_HEIGHT, lineColour, lineWidth);
    }

    private void wedge(float cx, float cy, float r, double start, double sweep, Color color) throws IOException {
        stream.setNonStrokingColor(color);
        stream.moveTo(cx, cy);
        int steps = Math.max(2, (int) Math.ceil(sweep / (Math.PI / 90)));
        for (int i = 0; i <= steps; i++) {
            double a = start - sweep * i / steps;
            stream.lineTo(cx + (float) (r * Math.cos(a)), cy + (float) (r * Math.sin(a)));
        }
        stream.closePath();
        stream.fill();
    }

    private void circleOutline(float cx, float cy, float r) throws IOException {
        stream.moveTo(cx + r, cy);
        for (int i = 1; i <= 180; i++) {
            double a = 2 * Math.PI * i / 180;
            stream.lineTo(cx + (float) (r * Math.cos(a)), cy + (float) (r * Math.sin(a)));
        }
        stream.stroke();
    }

    private void line(float x1, float y1, float x2, float y2, Color colour, float width) throws IOException {
        stream.setStrokingColor(colour);
        stream.setLineWidth(width);
        stream.moveTo(x1, y1);
        stream.lineTo(x2, y2);
        stream.stroke();
    }

    private void writeCentred(String text, PDType1Font font, float size, float centre, float baseline, Color color) throws IOException {
        write(text, font, size, centre - textWidth(sanitise(text), font, size) / 2, baseline, color);
    }

    private void write(String text, PDType1Font font, float size, float x, float baseline, Color color) throws IOException {
        stream.beginText();
        stream.setFont(font, size);
        stream.setNonStrokingColor(color);
        stream.newLineAtOffset(x, baseline);
        stream.showText(sanitise(text));
        stream.endText();
    }

    private static float textWidth(String text, PDType1Font font, float size) throws IOException {
        return font.getStringWidth(text) / 1000 * size;
    }

    private void newPage() throws IOException {
        closeStream();
        PDPage page = new PDPage(PAGE_SIZE);
        document.addPage(page);
        stream = new PDPageContentStream(document, page);
        y = PAGE_SIZE.getHeight() - MARGIN;
    }

    private void closeStream() throws IOException {
        if (stream != null) {
            stream.close();
            stream = null;
        }
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Design Principles: SOLID, Week 3 lecture notes.
COMP 3607 (n.d.). OOP Design Principles: SOLID - Dependency Inversion, Week 4 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
The Apache Software Foundation. Apache PDFBox 3.0. https://pdfbox.apache.org/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
CV Genius (n.d.). Computer Science CV Example (layout model for the report). https://cvgenius.com/
W3C (2018). Web Content Accessibility Guidelines (WCAG) 2.1: 1.4.1 Use of Color and 1.4.3 Contrast (Minimum). https://www.w3.org/TR/WCAG21/
Oracle. The Java Tutorials: Exceptions (Catching and Handling Exceptions). https://docs.oracle.com/javase/tutorial/essential/exceptions/handling.html
*/
