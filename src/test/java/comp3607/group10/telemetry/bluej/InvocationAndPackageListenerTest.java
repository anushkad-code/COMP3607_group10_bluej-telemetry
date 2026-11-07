package comp3607.group10.telemetry.bluej;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bluej.extensions2.BClass;
import bluej.extensions2.BPackage;
import bluej.extensions2.BProject;
import bluej.extensions2.PackageNotFoundException;
import bluej.extensions2.ProjectNotOpenException;
import bluej.extensions2.event.InvocationFinishedEvent;
import bluej.extensions2.event.PackageEvent;

import comp3607.group10.telemetry.TestData;
import comp3607.group10.telemetry.model.ClassSnapshot;
import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class InvocationAndPackageListenerTest {
    private ListenerTestSupport support;

    @BeforeEach
    public void setUp() {
        support = new ListenerTestSupport();
    }

    @Test
    public void runsMapEachExitTypeToAnOutcome() throws Exception {
        InvocationTelemetryListener listener = new InvocationTelemetryListener(support.context);
        listener.invocationFinished(run(InvocationFinishedEvent.EventType.NORMAL_EXIT, "main"));
        listener.invocationFinished(run(InvocationFinishedEvent.EventType.EXCEPTION_EXIT, "divide"));
        listener.invocationFinished(run(InvocationFinishedEvent.EventType.TERMINATED_EXIT, "loop"));
        listener.invocationFinished(run(InvocationFinishedEvent.EventType.UNKNOWN_EXIT, null));

        List<TelemetryEvent> events = support.runQueued();
        assertEquals(Arrays.asList(EventType.PROGRAM_RUN, EventType.RUNTIME_EXCEPTION, EventType.PROGRAM_RUN,
                EventType.PROGRAM_RUN), TestData.typesOf(events));
        assertEquals("TERMINATED", events.get(2).getDetail(DetailKeys.EXIT_TYPE));
        assertEquals("UNKNOWN", events.get(3).getDetail(DetailKeys.EXIT_TYPE));
        assertEquals("divide on Calculator ended with a runtime exception",
                events.get(1).getDetail(DetailKeys.OUTPUT_SUMMARY));
    }

    @Test
    public void openingAProjectBaselinesItsClasses() throws Exception {
        BClass existing = mock(BClass.class);
        when(existing.getName()).thenReturn("Circle");
        when(existing.isCompiled()).thenReturn(true);
        BPackage bPackage = packageNamed("Shapes");
        when(bPackage.getClasses()).thenReturn(new BClass[] {existing});

        PackageTelemetryListener listener = new PackageTelemetryListener(support.context,
                new EditorAttacher(support.context));
        listener.packageOpened(packageEvent(bPackage));
        listener.packageClosing(packageEvent(bPackage));
        assertEquals(Arrays.asList(EventType.PROJECT_OPENED, EventType.PROJECT_CLOSED), support.runQueuedTypes());

        support.context.getRecorder().classStateChanged(new ClassSnapshot("Circle", true, false, Arrays.asList()), 5L);
        assertEquals(3, support.session.getEvents().size());
    }

    @Test
    public void projectThatCannotBeReadStillGetsEvents() throws Exception {
        BPackage broken = mock(BPackage.class);
        when(broken.getProject()).thenThrow(mock(ProjectNotOpenException.class));
        when(broken.getClasses()).thenThrow(mock(PackageNotFoundException.class));
        new PackageTelemetryListener(support.context, new EditorAttacher(support.context))
                .packageOpened(packageEvent(broken));
        TelemetryEvent opened = support.runQueued().get(0);
        assertEquals("unknown", opened.getDetail(DetailKeys.PROJECT));
    }

    private static InvocationFinishedEvent run(InvocationFinishedEvent.EventType type, String method) {
        InvocationFinishedEvent event = mock(InvocationFinishedEvent.class);
        when(event.getEventType()).thenReturn(type);
        when(event.getClassName()).thenReturn("Calculator");
        when(event.getMethodName()).thenReturn(method);
        return event;
    }

    private static BPackage packageNamed(String name) throws Exception {
        BProject project = mock(BProject.class);
        when(project.getName()).thenReturn(name);
        BPackage bPackage = mock(BPackage.class);
        when(bPackage.getProject()).thenReturn(project);
        return bPackage;
    }

    private static PackageEvent packageEvent(BPackage bPackage) {
        PackageEvent event = mock(PackageEvent.class);
        when(event.getPackage()).thenReturn(bPackage);
        return event;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
BlueJ. Writing Extensions for BlueJ 5+. https://www.bluej.org/extensions/writingextensions2.html
BlueJ / King's College London. BlueJ source code, Extensions API package bluej.extensions2. https://github.com/k-pet-group/BlueJ-Greenfoot
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Mockito. Mockito documentation. https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
*/
