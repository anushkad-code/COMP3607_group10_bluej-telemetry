package comp3607.group10.telemetry.command;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import comp3607.group10.telemetry.core.ErrorHandler;

/**
The invoker of the Command pattern. It keeps a thread-safe queue of commands and runs them one at
a time, in order, on a single background thread, so BlueJ's screen thread never waits.

@author Anushka Dwarika
*/
public class TelemetryInvoker implements CommandQueue, Runnable {
    /** The command that tells the worker to stop. */
    private static final Command STOP = new StopCommand();

    /** The thread-safe queue of waiting commands. */
    private final BlockingQueue<Command> queue = new LinkedBlockingQueue<Command>();
    /** Reports errors from commands that fail. */
    private final ErrorHandler errorHandler;
    /** The background worker thread. */
    private final Thread thread;
    /** Whether new commands are still accepted. */
    private boolean accepting = true;

    /**
    Creates the invoker. The worker thread starts when start is called.

    @param errorHandler reports errors from background work
    */
    public TelemetryInvoker(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.thread = new Thread(this, "bluej-telemetry-worker");
        this.thread.setDaemon(true);
    }

    /**
    Starts the worker thread.
    */
    public void start() {
        thread.start();
    }

    /**
    Adds a command to the queue without waiting.

    @param command the command to run
    @return true if accepted, false after shutdown
    */
    @Override
    public synchronized boolean submit(Command command) {
        if (!accepting) {
            return false;
        }
        return queue.offer(command);
    }

    /**
    Stops accepting commands, lets the queued ones finish, and waits for the worker to stop.

    @param timeout the longest time to wait
    @param unit the unit of the timeout
    @return true if the worker stopped before the timeout
    @throws InterruptedException if the thread is interrupted while waiting
    */
    public boolean shutdown(long timeout, TimeUnit unit) throws InterruptedException {
        stopAccepting();
        thread.join(unit.toMillis(timeout));
        return !thread.isAlive();
    }

    /**
    Stops accepting commands and queues the stop command.
    */
    private synchronized void stopAccepting() {
        accepting = false;
        queue.offer(STOP);
    }

    /**
    Gives the number of commands still waiting.

    @return the queue size
    */
    public int pendingCommands() {
        return queue.size();
    }

    /**
    Runs the worker loop on the background thread.
    */
    @Override
    public void run() {
        runLoop();
    }

    /**
    Takes commands from the queue and runs them until the stop command arrives.
    */
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

    /**
    Runs one command, reporting any error instead of letting it stop the worker.

    @param command the command to run
    */
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
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
