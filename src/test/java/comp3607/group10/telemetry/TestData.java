package comp3607.group10.telemetry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import comp3607.group10.telemetry.model.EventType;
import comp3607.group10.telemetry.model.TelemetryEvent;

public class TestData {
    public static Map<String, String> details() {
        return new LinkedHashMap<String, String>();
    }

    public static Map<String, String> details(String key, String value) {
        Map<String, String> details = details();
        details.put(key, value);
        return details;
    }

    public static Map<String, String> details(String key1, String value1, String key2, String value2) {
        Map<String, String> details = details(key1, value1);
        details.put(key2, value2);
        return details;
    }

    public static Map<String, String> details(String key1, String value1, String key2, String value2, String key3, String value3) {
        Map<String, String> details = details(key1, value1, key2, value2);
        details.put(key3, value3);
        return details;
    }

    public static Map<String, String> details(String key1, String value1, String key2, String value2, String key3, String value3, String key4, String value4) {
        Map<String, String> details = details(key1, value1, key2, value2, key3, value3);
        details.put(key4, value4);
        return details;
    }

    public static List<EventType> typesOf(List<TelemetryEvent> events) {
        List<EventType> types = new ArrayList<EventType>();
        for (TelemetryEvent event : events) {
            types.add(event.getType());
        }
        return types;
    }
}

/*
SOURCES USED:
COMP 3607 (n.d.). OOP Testing: JUnit Framework, Week 8 lecture notes.
JUnit Team. JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Oracle. Java SE 21 API Specification. https://docs.oracle.com/en/java/javase/21/docs/api/
*/
