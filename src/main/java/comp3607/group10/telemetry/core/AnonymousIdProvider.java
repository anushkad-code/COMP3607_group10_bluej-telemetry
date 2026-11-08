package comp3607.group10.telemetry.core;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class AnonymousIdProvider {
    private static final String PREFIX = "student-";

    private AnonymousIdProvider() {
    }

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
*/
