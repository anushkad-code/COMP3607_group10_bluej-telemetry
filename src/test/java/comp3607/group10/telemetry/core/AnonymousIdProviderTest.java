package comp3607.group10.telemetry.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AnonymousIdProviderTest {
    @Test
    public void sameSeedGivesSameIdSoSessionsCanBeLinked() {
        assertEquals(AnonymousIdProvider.fromSeed("abc"), AnonymousIdProvider.fromSeed("abc"));
    }

    @Test
    public void idDoesNotContainTheSeed() {
        String id = AnonymousIdProvider.fromSeed("student.one@example.com");
        assertTrue(id.matches("student-[0-9a-f]{8}"));
        assertTrue(!id.contains("jane"));
    }

    @Test
    public void missingSeedStillGivesAnId() {
        assertTrue(AnonymousIdProvider.fromSeed(null).startsWith("student-"));
        assertTrue(!AnonymousIdProvider.fromSeed("").equals(AnonymousIdProvider.fromSeed("")));
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
*/
