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

/**
A helper over PDFBox that draws the parts of the report (heading, sections, tables, bar charts,
the pie chart and the footer) and keeps everything within the page limit.

@author Reinaldo Singh
*/
public class PdfCanvas implements AutoCloseable {
    /** The page margin in points. */
    static final float MARGIN = 48;
    /** The height of one table row in points. */
    static final float ROW_HEIGHT = 15;
    /** The size of normal text. */
    private static final float TEXT_SIZE = 9.5f;
    /** The page size: A4. */
    private static final PDRectangle PAGE_SIZE = PDRectangle.A4;
    /** The colour of normal text. */
    private static final Color INK = new Color(40, 32, 26);
    /** The colour of headings. */
    private static final Color HEADING = new Color(168, 70, 8);
    /** The accent colour for the bar under the heading. */
    private static final Color ORANGE = new Color(232, 120, 22);
    /** The colour of captions and notes. */
    private static final Color SOFT_TEXT = new Color(95, 80, 66);
    /** The colour of lines and table borders. */
    private static final Color RULE = new Color(226, 196, 140);
    /** The fill colour of table headings. */
    private static final Color HEADER_FILL = new Color(255, 236, 170);

    /** The PDFBox document being drawn. */
    private final PDDocument document = new PDDocument();
    /** The normal font: Times Roman. */
    private final PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
    /** The bold font: Times Bold. */
    private final PDType1Font bold = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);
    /** The italic font: Times Italic. */
    private final PDType1Font italic = new PDType1Font(Standard14Fonts.FontName.TIMES_ITALIC);
    /** The most pages the PDF may have. */
    private final int maxPages;
    /** The drawing stream for the current page. */
    private PDPageContentStream stream;
    /** How far down the page drawing has reached. */
    private float y;

    /**
    Creates a document with its first page.

    @param maxPages the most pages the PDF may have
    @throws IOException if the file cannot be read or written
    */
    public PdfCanvas(int maxPages) throws IOException {
        this.maxPages = maxPages;
        document.getDocumentCatalog().setLanguage("en");
        newPage();
    }

    /**
    Sets the title and author shown in the PDF's properties.

    @param title the title
    @param author the author shown in the PDF properties
    */
    public void setDocumentTitle(String title, String author) {
        PDDocumentInformation information = new PDDocumentInformation();
        information.setTitle(title);
        information.setAuthor(author);
        document.setDocumentInformation(information);
    }

    /**
    Gives the width between the margins.

    @return the width in points
    */
    public float contentWidth() {
        return PAGE_SIZE.getWidth() - 2 * MARGIN;
    }

    /**
    Gives the left margin.

    @return the x position in points
    */
    public float left() {
        return MARGIN;
    }

    /**
    Gives how far down the page drawing has reached.

    @return the y position in points
    */
    public float top() {
        return y;
    }

    /**
    Gives the number of pages.

    @return the page count
    */
    public int pageCount() {
        return document.getNumberOfPages();
    }

    /**
    Gives the space left above the bottom margin.

    @return the height in points
    */
    public float remainingHeight() {
        return y - MARGIN;
    }

    /**
    Works out how many table rows fit in the space left.

    @param spaceToKeep space to leave free below, in points
    @return the number of rows, never less than 0
    */
    public int rowsThatFit(float spaceToKeep) {
        int rows = (int) ((remainingHeight() - spaceToKeep) / ROW_HEIGHT);
        if (rows < 0) {
            return 0;
        }
        return rows;
    }

    /**
    Makes sure there is enough space, starting a new page if the page limit allows.

    @param height the height needed, in points
    @return true if there is space, false if the page limit was reached
    @throws IOException if the file cannot be read or written
    */
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

    /**
    Moves the drawing position down.

    @param height the height needed, in points
    */
    public void moveDown(float height) {
        y = y - height;
    }

    /**
    Draws the centred title, subtitle and details line, then a divider.

    @param title the title
    @param subtitle the line under the title
    @param details the line with the student ID and session times
    @throws IOException if the file cannot be read or written
    */
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

    /**
    Draws a section heading in spaced capitals.

    @param title the title
    @throws IOException if the file cannot be read or written
    */
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

    /**
    Draws a thin line with a short orange bar on it.

    @throws IOException if the file cannot be read or written
    */
    public void divider() throws IOException {
        y -= 6;
        line(MARGIN, y, MARGIN + contentWidth(), y, RULE, 0.75f);
        stream.setNonStrokingColor(ORANGE);
        stream.addRect(MARGIN, y - 1.5f, 48, 3);
        stream.fill();
        y -= 4;
    }

    /**
    Draws a caption in italics.

    @param x the left edge, in points
    @param top the top edge, in points
    @param text the text
    @return the height used, in points
    @throws IOException if the file cannot be read or written
    */
    public float caption(float x, float top, String text) throws IOException {
        write(text, italic, TEXT_SIZE, x, top - TEXT_SIZE, SOFT_TEXT);
        return 14;
    }

    /**
    Draws a note in italics across the page.

    @param text the text
    @throws IOException if the file cannot be read or written
    */
    public void note(String text) throws IOException {
        y -= caption(MARGIN, y, fit(text, italic, TEXT_SIZE, contentWidth()));
    }

    /**
    Draws a table with a shaded heading row.

    @param x the left edge, in points
    @param top the top edge, in points
    @param width the width, in points
    @param headers the column headings
    @param rows the table rows
    @param columnShares the share of the width each column gets
    @return the height used, in points
    @throws IOException if the file cannot be read or written
    */
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

    /**
    Draws a horizontal bar chart.

    @param x the left edge, in points
    @param top the top edge, in points
    @param width the width, in points
    @param labels the label for each value
    @param values the values to draw
    @param barColor the colour of the bars
    @return the height used, in points
    @throws IOException if the file cannot be read or written
    */
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

    /**
    Draws a pie chart with a legend.

    @param x the left edge, in points
    @param top the top edge, in points
    @param radius the radius, in points
    @param labels the label for each value
    @param values the values to draw
    @param colors the colour of each slice
    @return the height used, in points
    @throws IOException if the file cannot be read or written
    */
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

    /**
    Draws the footer text and the page number on every page.

    @param text the text
    @throws IOException if the file cannot be read or written
    */
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

    /**
    Saves the PDF.

    @param file the file to write
    @throws IOException if the file cannot be read or written
    */
    public void save(Path file) throws IOException {
        closeStream();
        document.save(file.toFile());
    }

    /**
    Frees the document's resources.

    @throws IOException if the file cannot be read or written
    */
    @Override
    public void close() throws IOException {
        closeStream();
        document.close();
    }

    /**
    Replaces characters the built-in fonts cannot show.

    @param text the text
    @return text that is safe to draw
    */
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

    /**
    Gives the character to use instead of one the fonts cannot show.

    @param c the character
    @return the plain character to print instead
    */
    private static char replacementFor(char c) {
        if (c == '\t' || c == '\n' || c == '\r') {
            return ' ';
        }
        return '?';
    }

    /**
    Shortens text with "..." so it fits the width.

    @param text the text
    @param font the font used
    @param size the text size
    @param width the width, in points
    @return the text, shortened if needed
    @throws IOException if the file cannot be read or written
    */
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

    /**
    Draws one table row.

    @param x the left edge, in points
    @param top the top edge, in points
    @param width the width, in points
    @param cells the text for each cell
    @param widths the width of each column
    @param font the font to use
    @param header true for the header row, which is drawn in bold
    @throws IOException if the file cannot be read or written
    */
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

    /**
    Draws one slice of the pie chart.

    @param cx the x position of the centre
    @param cy the y position of the centre
    @param r the radius
    @param start the start of the session in epoch milliseconds
    @param sweep how far the wedge turns, in degrees
    @param color the colour to use
    @throws IOException if the file cannot be read or written
    */
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

    /**
    Draws the outline of the pie chart.

    @param cx the x position of the centre
    @param cy the y position of the centre
    @param r the radius
    @throws IOException if the file cannot be read or written
    */
    private void circleOutline(float cx, float cy, float r) throws IOException {
        stream.moveTo(cx + r, cy);
        for (int i = 1; i <= 180; i++) {
            double a = 2 * Math.PI * i / 180;
            stream.lineTo(cx + (float) (r * Math.cos(a)), cy + (float) (r * Math.sin(a)));
        }
        stream.stroke();
    }

    /**
    Draws a line.

    @param x1 the x position of the start
    @param y1 the y position of the start
    @param x2 the x position of the end
    @param y2 the y position of the end
    @param colour the colour to use
    @param width the width, in points
    @throws IOException if the file cannot be read or written
    */
    private void line(float x1, float y1, float x2, float y2, Color colour, float width) throws IOException {
        stream.setStrokingColor(colour);
        stream.setLineWidth(width);
        stream.moveTo(x1, y1);
        stream.lineTo(x2, y2);
        stream.stroke();
    }

    /**
    Draws text centred on a position.

    @param text the text
    @param font the font to use
    @param size the font size in points
    @param centre the x position of the centre of the text
    @param baseline the y position of the text baseline
    @param color the colour to use
    @throws IOException if the file cannot be read or written
    */
    private void writeCentred(String text, PDType1Font font, float size, float centre, float baseline, Color color) throws IOException {
        write(text, font, size, centre - textWidth(sanitise(text), font, size) / 2, baseline, color);
    }

    /**
    Draws text starting at a position.

    @param text the text
    @param font the font to use
    @param size the font size in points
    @param x the left edge, in points
    @param baseline the y position of the text baseline
    @param color the colour to use
    @throws IOException if the file cannot be read or written
    */
    private void write(String text, PDType1Font font, float size, float x, float baseline, Color color) throws IOException {
        stream.beginText();
        stream.setFont(font, size);
        stream.setNonStrokingColor(color);
        stream.newLineAtOffset(x, baseline);
        stream.showText(sanitise(text));
        stream.endText();
    }

    /**
    Measures the width of some text.

    @param text the text
    @param font the font to use
    @param size the font size in points
    @return the width of the text in points
    @throws IOException if the file cannot be read or written
    */
    private static float textWidth(String text, PDType1Font font, float size) throws IOException {
        return font.getStringWidth(text) / 1000 * size;
    }

    /**
    Starts a new page.

    @throws IOException if the file cannot be read or written
    */
    private void newPage() throws IOException {
        closeStream();
        PDPage page = new PDPage(PAGE_SIZE);
        document.addPage(page);
        stream = new PDPageContentStream(document, page);
        y = PAGE_SIZE.getHeight() - MARGIN;
    }

    /**
    Closes the drawing stream for the current page.

    @throws IOException if the file cannot be read or written
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
