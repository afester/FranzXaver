package afester.javafx.examples.board.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Package {

    private final String packageName;
    private final Map<String, PartPad> pads = new HashMap<>();  // pad shapes
    private final List<PartShape> shapes = new ArrayList<>();   // normal shapes

    public Package(String name) {
        this.packageName = name;
    }

    public void addPad(PartPad pad) {
        final String key = pad.getName();
        pads.put(key, pad);
    }


    public PartPad getPad(String padName) {
        return pads.get(padName);
    }

    public void addShape(PartShape shape) {
        shapes.add(shape);
    }

    public List<PartShape> getShapes() {
        return shapes;
    }

    public Collection<PartPad> getPads() {
        return pads.values();
    }

    public String getName() {
        return packageName;
    }

    @Override
    public String toString() {
        return String.format("Package[name=\"%s\", %s shapes, %s pads]", packageName, shapes.size(), pads.size());
    }
}
