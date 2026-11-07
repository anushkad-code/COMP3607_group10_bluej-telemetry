package comp3607.group10.telemetry.bluej;

import java.util.ArrayList;
import java.util.List;

import bluej.extensions2.BClass;
import bluej.extensions2.BMethod;

import comp3607.group10.telemetry.model.ClassSnapshot;

class ClassSnapshotReader {
    private ClassSnapshotReader() {
    }

    static ClassSnapshot read(BClass bClass, boolean compiled, boolean hasError) {
        List<String> methods = new ArrayList<String>();
        if (compiled) {
            methods = methodNames(bClass);
        }
        return new ClassSnapshot(bClass.getName(), compiled, hasError, methods);
    }

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
*/
