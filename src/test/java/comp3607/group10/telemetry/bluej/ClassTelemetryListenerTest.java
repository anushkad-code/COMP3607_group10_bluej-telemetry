package comp3607.group10.telemetry.bluej;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import bluej.extensions2.BClass;
import bluej.extensions2.BMethod;
import bluej.extensions2.ProjectNotOpenException;
import bluej.extensions2.editor.DocumentListener;
import bluej.extensions2.editor.JavaEditor;
import bluej.extensions2.event.ClassEvent;

import comp3607.group10.telemetry.TestData;
import comp3607.group10.telemetry.model.DetailKeys;
import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class ClassTelemetryListenerTest {
    private ListenerTestSupport support;
    private EditorAttacher editors;
    private ClassTelemetryListener listener;
    private BClass circle;
    private JavaEditor editor;

    @BeforeEach
    public void setUp() throws Exception {
        support = new ListenerTestSupport();
        editors = new EditorAttacher(support.context);
        listener = new ClassTelemetryListener(support.context, editors);
        circle = mock(BClass.class);
        editor = mock(JavaEditor.class);
        when(circle.getName()).thenReturn("Circle");
        when(circle.getJavaEditor()).thenReturn(editor);
    }

    @Test
    public void newClassThenCompileWithAMethod() throws Exception {
        listener.classStateChanged(stateEvent(false, false));
        BMethod area = mock(BMethod.class);
        when(area.getName()).thenReturn("area");
        when(circle.getDeclaredMethods()).thenReturn(new BMethod[] {area});
        listener.classStateChanged(stateEvent(true, false));

        List<TelemetryEvent> events = support.runQueued();
        assertEquals(Arrays.asList(EventType.CLASS_CREATED, EventType.METHOD_DEFINED), TestData.typesOf(events));
        assertEquals("area", events.get(1).getDetail(DetailKeys.METHOD));
    }

    @Test
    public void typingInTheEditorBecomesAnEditBurstWithCountsOnly() throws Exception {
        listener.classStateChanged(stateEvent(false, false));
        ArgumentCaptor<DocumentListener> captor = ArgumentCaptor.forClass(DocumentListener.class);
        verify(editor).addDocumentListener(captor.capture());

        captor.getValue().textReplaced(0, "", "int r;", 0, 0);
        support.clock.advanceMillis(300);
        captor.getValue().textReplaced(5, ";", "", 0, 0);

        List<TelemetryEvent> events = support.runQueued();
        TelemetryEvent burst = events.get(1);
        assertEquals(EventType.EDIT_BURST, burst.getType());
        assertEquals("2", burst.getDetail(DetailKeys.KEYSTROKES));
        assertEquals("6", burst.getDetail(DetailKeys.CHARS_INSERTED));
        assertEquals("1", burst.getDetail(DetailKeys.CHARS_DELETED));
        for (String value : burst.getDetails().values()) {
            assertTrue(!value.contains("int"));
        }
    }

    @Test
    public void editorIsAttachedOnlyOnce() {
        listener.classStateChanged(stateEvent(false, false));
        listener.classStateChanged(stateEvent(false, false));
        verify(editor, org.mockito.Mockito.times(1)).addDocumentListener(any());
        assertTrue(editors.isAttached("Circle"));
    }

    @Test
    public void classWithoutAJavaEditorIsStillTracked() throws Exception {
        when(circle.getJavaEditor()).thenThrow(mock(ProjectNotOpenException.class));
        when(circle.getDeclaredMethods()).thenThrow(mock(ProjectNotOpenException.class));
        listener.classStateChanged(stateEvent(true, false));
        assertTrue(!editors.isAttached("Circle"));
        assertEquals(Arrays.asList(EventType.CLASS_CREATED), support.runQueuedTypes());
    }

    @Test
    public void renameAndRemove() throws Exception {
        listener.classStateChanged(stateEvent(false, false));
        BClass square = mock(BClass.class);
        when(square.getName()).thenReturn("Square");
        ClassEvent rename = mock(ClassEvent.class);
        when(rename.getBClass()).thenReturn(square);
        when(rename.getOldName()).thenReturn("Circle");
        listener.classNameChanged(rename);
        assertTrue(editors.isAttached("Square"));

        ClassEvent remove = mock(ClassEvent.class);
        when(remove.getBClass()).thenReturn(square);
        listener.classRemoved(remove);
        assertTrue(!editors.isAttached("Square"));

        List<TelemetryEvent> events = support.runQueued();
        assertEquals(Arrays.asList(EventType.CLASS_CREATED, EventType.CLASS_RENAMED, EventType.CLASS_DELETED), TestData.typesOf(events));
        assertEquals("Circle", events.get(1).getDetail(DetailKeys.OLD_NAME));
    }

    private ClassEvent stateEvent(boolean compiled, boolean hasError) {
        ClassEvent event = mock(ClassEvent.class);
        when(event.getBClass()).thenReturn(circle);
        when(event.isClassCompiled()).thenReturn(compiled);
        when(event.hasError()).thenReturn(hasError);
        return event;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Design Patterns: Observer, Week 7 lecture notes.
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
BlueJ. Writing Extensions for BlueJ 5+. https://www.bluej.org/extensions/writingextensions2.html
BlueJ / King's College London. BlueJ source code, Extensions API package bluej.extensions2. https://github.com/k-pet-group/BlueJ-Greenfoot
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Mockito. Mockito documentation. https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
