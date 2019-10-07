package afester.javafx.examples.board.eagle;

import java.util.HashMap;
import java.util.Map;

import afester.javafx.examples.board.model.Package;

// A device is the package and the mapping of pins to the pads on the package
class Device {

    private String name;
    private Package thePackage;

    // Maps gate/pin to pad
    private Map<String, String> connections = new HashMap<>();
    
    public Device(String deviceName, Package pkg) {
        this.name = deviceName;
        this.thePackage = pkg;
    }

    public String getName() {
        return name;
    }

    public Package getPackage() {
        return thePackage;
    }

    public Map<String, String> getPinPadMapping() {
        return connections;
    }

    public void addPinPadMapping(String pinId, String pad) {
        connections.put(pinId, pad);
    }

    @Override
    public String toString() {
        return String.format("Device[name=\"%s\"]", name);
    }

}