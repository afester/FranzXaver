package afester.javafx.examples.board.eagle;

import java.util.HashMap;
import java.util.Map;

import afester.javafx.examples.board.model.Package;

// A collection of devicesets and packages
class Library {
    private String id;
//    private String name;        // library name - not necessarily unique!!

    private Map<String, Package> packages = new HashMap<>();    // package name => package
    private Map<String, DeviceSet> deviceSets = new HashMap<>();

    public Library(String id) { // , String name) {
        this.id = id;
//        this.name = name;
    }

//    public String getName() {
//        return name;
//    }

    public String getId() {
        return id;
    }

    public void addPackage(Package result) {
        packages.put(result.getName(), result);
    }


    public Package getPackage(String packageName) {
        return packages.get(packageName);
    }

    public void addDeviceSet(DeviceSet ds) {
        deviceSets.put(ds.getName(), ds);
    }

    public DeviceSet getDeviceSet(String name) {
        return deviceSets.get(name);
    }


    @Override
    public String toString() {
        return String.format("Library[id=\"%s\"]" + deviceSets, id);
    }
}