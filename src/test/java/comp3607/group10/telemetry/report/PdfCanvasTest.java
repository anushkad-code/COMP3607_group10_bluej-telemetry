package comp3607.group10.telemetry.report;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;

public class PdfCanvasTest {
    @Test
    public void sanitiseKeepsAsciiAndReplacesTheRest() {
        assertEquals("abc ?q? ?", PdfCanvas.sanitise("abc ‘q’ é"));
        assertEquals("a b", PdfCanvas.sanitise("a\nb"));
        assertEquals("", PdfCanvas.sanitise(null));
    }

    @Test
    public void longTextIsShortenedToFit() throws IOException {
        PdfCanvas canvas = new PdfCanvas(1);
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        String fitted = canvas.fit(longText(500), font, 8, 100);
        String notShortened = canvas.fit("short", font, 8, 100);
        canvas.close();
        assertTrue(fitted.endsWith("..."));
        assertTrue(fitted.length() < 500);
        assertEquals("short", notShortened);
    }

    @Test
    public void pageBudgetIsEnforced() throws IOException {
        PdfCanvas canvas = new PdfCanvas(2);
        assertTrue(canvas.ensureSpace(canvas.remainingHeight() + 1));
        assertEquals(2, canvas.pageCount());
        assertTrue(!(canvas.ensureSpace(canvas.remainingHeight() + 1)));
        assertEquals(2, canvas.pageCount());
        canvas.close();
    }

    private static String longText(int length) {
        String text = "";
        for (int i = 0; i < length; i++) {
            text = text + "a";
        }
        return text;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
The Apache Software Foundation. Apache PDFBox 3.0. https://pdfbox.apache.org/
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
*/
