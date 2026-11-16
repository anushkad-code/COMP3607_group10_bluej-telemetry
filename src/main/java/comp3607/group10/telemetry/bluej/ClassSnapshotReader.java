package comp3607.group10.telemetry.bluej;

import java.util.ArrayList;
import java.util.List;

import bluej.extensions2.BClass;
import bluej.extensions2.BMethod;

import comp3607.group10.telemetry.model.ClassSnapshot;

/**
Reads a class's name, compile state and method names from BlueJ into a ClassSnapshot.

@author Anushka Dwarika
*/
class ClassSnapshotReader {
    /**
    Not used: this class only has static methods.
    */
    private ClassSnapshotReader() {
    }

    /**
    Reads the current state of a BlueJ class. Method names are only read when the class is
    compiled.

    @param bClass the BlueJ class
    @param compiled whether the class is compiled
    @param hasError whether the class has a compile error
    @return the snapshot
    */
    static ClassSnapshot read(BClass bClass, boolean compiled, boolean hasError) {
        List<String> methods = new ArrayList<String>();
        if (compiled) {
            methods = methodNames(bClass);
        }
        return new ClassSnapshot(bClass.getName(), compiled, hasError, methods);
    }

    /**
    Reads the names of the methods declared in a compiled class, without repeats.

    @param bClass the BlueJ class
    @return the method names, or an empty list if they cannot be read
    */
    static List<String> methodNames(BClass bClass) {
        try {
            List<String> names = new ArrayList<String>();
            BMethod[] methods = bClass.getDeclaredMethods();
            if (methods == null) {
                return names;
            }
            for (BMethod method : methods) {
                if (!names.contains(method.getName())) {
                    names.add(method.getName());
                }
            }
            return names;
        } catch (Exception e) {
            return new ArrayList<String>();
        }
    }
}

/*
SOURCES USED:
BlueJ. Writing Extensions for BlueJ 5+. https://www.bluej.org/extensions/writingextensions2.html
BlueJ / King's College London. BlueJ source code, Extensions API package bluej.extensions2. https://github.com/k-pet-group/BlueJ-Greenfoot
COMP 3607 (n.d.). Lab tutorials and code samples: Command (Light, LightOnCommand, RemoteControl), Week 6 Demo (Composite), Week 7 (Patient), Interface Segregation Principle (Door, TimedDoor) and Coffee Machine (SOLID).
Oracle. How to Write Doc Comments for the Javadoc Tool. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
*/
