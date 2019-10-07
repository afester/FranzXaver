package afester.javafx.examples.board.eagle;

import java.util.HashMap;
import java.util.Map;

// A collection of devices
class DeviceSet {
    private String name;
    private boolean isUserValue;
    private Map<String, Device> devices = new HashMap<>();

    public DeviceSet(String deviceSetName, boolean isUserValue) {
        this.name = deviceSetName;
        this.isUserValue = isUserValue;
    }

    public void addDevice(Device device) {
        devices.put(device.getName(), device);
    }

    public Device getDevice(String name) {
        return devices.get(name);
    }

    public String getName() {
        return name;
    }

    public boolean isUserValue() {
        return isUserValue;
    }

    @Override
    public String toString() {
        return String.format("DeviceSet[name=\"%s\"]", name);
    }
}