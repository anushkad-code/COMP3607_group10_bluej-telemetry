package comp3607.group10.telemetry.command;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import comp3607.group10.telemetry.core.ErrorHandler;

public class TelemetryInvoker implements CommandQueue, Runnable {
    private static final Command STOP = new StopCommand();

    private final BlockingQueue<Command> queue = new LinkedBlockingQueue<Command>();
    private final ErrorHandler errorHandler;
    private final Thread thread;
    private boolean accepting = true;

    public TelemetryInvoker(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.thread = new Thread(this, "bluej-telemetry-worker");
        this.thread.setDaemon(true);
    }

    public void start() {
        thread.start();
    }

    @Override
    public synchronized boolean submit(Command command) {
        if (!accepting) {
            return false;
        }
        return queue.offer(command);
    }

    public boolean shutdown(long timeout, TimeUnit unit) throws InterruptedException {
        stopAccepting();
        thread.join(unit.toMillis(timeout));
        return !thread.isAlive();
    }

    private synchronized void stopAccepting() {
        accepting = false;
        queue.offer(STOP);
    }

    public int pendingCommands() {
        return queue.size();
    }

    @Override
    public void run() {
        runLoop();
    }

    private void runLoop() {
        while (true) {
            Command command;
            try {
                command = queue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            if (command == STOP) {
                return;
            }
            runSafely(command);
        }
    }

    private void runSafely(Command command) {
        try {
            command.execute();
        } catch (Exception e) {
            errorHandler.handle(e);
        }
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Command, Week 7 lecture notes.
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
SourceMaking. Command Design Pattern. https://sourcemaking.com/design_patterns/command
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Exceptions (Catching and Handling Exceptions). https://docs.oracle.com/javase/tutorial/essential/exceptions/handling.html
*/
