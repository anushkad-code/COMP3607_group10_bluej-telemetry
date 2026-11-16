package comp3607.group10.telemetry.core;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
Makes an anonymous student ID, such as student-1a2b3c4d, so no real name or student number is
stored.

@author Haeley Parbhoo
*/
public class AnonymousIdProvider {
    /** The text every ID starts with. */
    private static final String PREFIX = "student-";

    /**
    Not used: this class only has static methods.
    */
    private AnonymousIdProvider() {
    }

    /**
    Makes an anonymous ID from a seed using SHA-256. The same seed always gives the same ID. A
    random seed is used if none is given.

    @param seed the value the ID is made from, usually BlueJ's data-collection ID
    @return the anonymous ID
    */
    public static String fromSeed(String seed) {
        String value = seed;
        if (seed == null || seed.trim().isEmpty()) {
            value = UUID.randomUUID().toString();
        }
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            String hex = "";
            for (int i = 0; i < 4; i++) {
                int number = hash[i];
                if (number < 0) {
                    number = number + 256;
                }
                hex = hex + Integer.toHexString(number / 16) + Integer.toHexString(number % 16);
            }
            return PREFIX + hex;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is required by every Java platform", e);
        }
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
Oracle. The Java Tutorials: Exceptions (Catching and Handling Exceptions). https://docs.oracle.com/javase/tutorial/essential/exceptions/handling.html
Oracle. The Java Tutorials: Learning the Java Language (access control, inheritance and super, primitive data types, generics). https://docs.oracle.com/javase/tutorial/java/
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
