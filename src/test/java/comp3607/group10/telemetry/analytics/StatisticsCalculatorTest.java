package comp3607.group10.telemetry.analytics;

import static comp3607.group10.telemetry.SampleSessions.START;
import static comp3607.group10.telemetry.SampleSessions.at;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import comp3607.group10.telemetry.TestData;
import comp3607.group10.telemetry.SampleSessions;
import comp3607.group10.telemetry.core.TelemetrySession;
import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class StatisticsCalculatorTest {
    private static final double DELTA = 0.001;

    private StatisticsCalculator calculator;
    private SessionStatistics stats;

    @BeforeEach
    public void setUp() {
        calculator = new StatisticsCalculator(60000);
        stats = calculator.calculate(SampleSessions.typicalSession(), at(510));
    }

    @Test
    public void durationIsEndMinusStart() {
        assertEquals(510, stats.getSessionDurationSec());
        assertEquals("student-1234abcd", stats.getStudentId());
        assertEquals(1, stats.getProjectNames().size());
        assertEquals("Shapes", stats.getProjectNames().get(0));
    }

    @Test
    public void compileTotalsCountFinishedCompilations() {
        assertEquals(3, stats.getCompiles().getTotal());
        assertEquals(2, stats.getCompiles().getSuccessful());
        assertEquals(1, stats.getCompiles().getFailed());
        assertEquals(2.0 / 3.0, stats.getCompiles().getSuccessRate(), DELTA);
    }

    @Test
    public void averageCompileIntervalUsesCompileStartTimes() {
        assertEquals(195.0, stats.getCompiles().getAverageIntervalSec(), DELTA);
    }

    @Test
    public void runsIncludeRunsThatThrew() {
        assertEquals(2, stats.getRuns().getTotalRuns());
        assertEquals(1, stats.getRuns().getRuntimeExceptions());
        assertEquals(1, stats.getRuns().getNormalRuns());
        assertTrue(stats.hasRuns());
        assertEquals(1.5, stats.getCompileToRunRatio(), DELTA);
    }

    @Test
    public void gapsOverTheThresholdAreIdle() {
        assertEquals(300, stats.getActivity().getIdleTimeSec());
        assertEquals(210, stats.getActivity().getActiveTimeSec());
        assertEquals(210.0 / 510.0, stats.getActivity().getActiveShare(), DELTA);
    }

    @Test
    public void activeAndIdleAlwaysAddUpToTheDuration() {
        assertEquals(stats.getSessionDurationSec(), stats.getActivity().getActiveTimeSec() + stats.getActivity().getIdleTimeSec());
    }

    @Test
    public void typingCadenceIsCharactersPerMinuteOfTyping() {
        assertEquals(120.0, stats.getActivity().getTypingCadenceCpm(), DELTA);
        assertEquals(40, stats.getActivity().getTotalEdits());
    }

    @Test
    public void errorsAreGroupedByCodeMostFrequentFirst() {
        TelemetrySession session = new TelemetrySession("s", START);
        session.update(error(10, "CANNOT_FIND_SYMBOL", "cannot find symbol - x"));
        session.update(error(11, "MISSING_SEMICOLON", "';' expected"));
        session.update(error(12, "CANNOT_FIND_SYMBOL", "cannot find symbol - y"));
        List<ErrorFrequency> frequency = calculator.calculate(session, at(20)).getErrorFrequency();

        assertEquals(2, frequency.size());
        assertEquals("CANNOT_FIND_SYMBOL", frequency.get(0).getErrorCode());
        assertEquals(2, frequency.get(0).getCount());
        assertEquals("cannot find symbol - x", frequency.get(0).getExampleMessage());
        assertEquals("MISSING_SEMICOLON", frequency.get(1).getErrorCode());
    }

    @Test
    public void timelineListsClassesAndMethodsInOrder() {
        List<TimelineEntry> timeline = stats.getTimeline();
        assertEquals(3, timeline.size());
        assertEquals("Class Circle created", timeline.get(0).describe());
        assertEquals("Method Circle.area() defined", timeline.get(1).describe());
        assertEquals("Square", timeline.get(2).getClassName());
    }

    @Test
    public void emptySessionHasNoDivisionByZero() {
        SessionStatistics empty = calculator.calculate(new TelemetrySession("s", START), START);
        assertEquals(0, empty.getSessionDurationSec());
        assertEquals(0.0, empty.getCompiles().getSuccessRate(), DELTA);
        assertEquals(0.0, empty.getCompiles().getAverageIntervalSec(), DELTA);
        assertEquals(0.0, empty.getActivity().getTypingCadenceCpm(), DELTA);
        assertEquals(0.0, empty.getActivity().getActiveShare(), DELTA);
        assertTrue(!empty.hasRuns());
        assertEquals(0.0, empty.getCompileToRunRatio(), DELTA);
        assertTrue(empty.getErrorFrequency().isEmpty());
    }

    @Test
    public void aSessionWithNoEventsIsIdleAfterTheThreshold() {
        SessionStatistics quiet = calculator.calculate(new TelemetrySession("s", START), at(30));
        assertEquals(30, quiet.getActivity().getActiveTimeSec());
        SessionStatistics longQuiet = calculator.calculate(new TelemetrySession("s", START), at(120));
        assertEquals(120, longQuiet.getActivity().getIdleTimeSec());
    }

    @Test
    public void anEditBurstKeepsTheUserActiveWhileTyping() {
        TelemetrySession session = new TelemetrySession("s", START);

        session.update(new TelemetryEvent(EventType.EDIT_BURST, at(0), "A", TestData.details(DetailKeys.DURATION_MS, "180000", DetailKeys.CHARS_INSERTED, "300")));
        SessionStatistics typing = calculator.calculate(session, at(210));
        assertEquals(210, typing.getActivity().getActiveTimeSec());
        assertEquals(0, typing.getActivity().getIdleTimeSec());
    }

    @Test
    public void badNumbersInDetailsCountAsZero() {
        TelemetrySession session = new TelemetrySession("s", START);
        session.update(new TelemetryEvent(EventType.EDIT_BURST, at(0), "A", TestData.details(DetailKeys.KEYSTROKES, "lots")));
        assertEquals(0, calculator.calculate(session, at(1)).getActivity().getTotalEdits());
    }

    @Test
    public void defaultThresholdIsSixtySeconds() {
        assertEquals(60000, StatisticsCalculator.DEFAULT_IDLE_THRESHOLD_MILLIS);
        SessionStatistics s = new StatisticsCalculator().calculate(SampleSessions.typicalSession(), at(510));
        assertEquals(300, s.getActivity().getIdleTimeSec());
        assertEquals(2, s.getTotalCompileErrors());
    }

    private static TelemetryEvent error(long second, String code, String message) {
        return new TelemetryEvent(EventType.COMPILE_ERROR, at(second), "A", TestData.details(DetailKeys.ERROR_CODE, code, DetailKeys.MESSAGE, message));
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
