package comp3607.group10.telemetry.command;

import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import comp3607.group10.telemetry.CollectedErrors;

import org.junit.jupiter.api.Test;

public class TelemetryInvokerTest {
    @Test
    public void commandsRunInSubmissionOrderOnTheWorkerThread() throws Exception {
        List<String> log = new ArrayList<String>();
        TelemetryInvoker worker = new TelemetryInvoker(new CollectedErrors());
        worker.start();
        for (int i = 0; i < 100; i++) {
            worker.submit(new LogCommand(log, Integer.toString(i)));
        }
        List<String> threads = new ArrayList<String>();
        worker.submit(new ThreadNameCommand(threads));

        assertTrue(worker.shutdown(5, TimeUnit.SECONDS));
        assertEquals(100, log.size());
        assertEquals("0", log.get(0));
        assertEquals("99", log.get(99));
        assertTrue(!Thread.currentThread().getName().equals(threads.get(0)));
        assertEquals(0, worker.pendingCommands());
    }

    @Test
    public void aFailingCommandIsReportedAndLaterCommandsStillRun() throws Exception {
        CollectedErrors errors = new CollectedErrors();
        List<String> log = new ArrayList<String>();
        TelemetryInvoker worker = new TelemetryInvoker(errors);
        worker.start();
        worker.submit(new FailingCommand());
        worker.submit(new LogCommand(log, "after"));
        worker.shutdown(5, TimeUnit.SECONDS);
        assertEquals(1, errors.errors.size());
        assertEquals(Arrays.asList("after"), log);
    }

    @Test
    public void submitIsRejectedAfterShutdown() throws Exception {
        TelemetryInvoker worker = new TelemetryInvoker(new CollectedErrors());
        worker.start();
        worker.shutdown(5, TimeUnit.SECONDS);
        assertTrue(!worker.submit(new LogCommand(new ArrayList<String>(), "too late")));
    }

    @Test
    public void submitNeverBlocksTheCaller() {
        TelemetryInvoker worker = new TelemetryInvoker(new CollectedErrors());

        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            worker.submit(new SleepCommand());
        }
        assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) < 1000);
        assertEquals(10000, worker.pendingCommands());
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
