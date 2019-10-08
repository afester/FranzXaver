package afester.javafx.examples.board.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class Package {

    private final String name;
    private final String id;
    private final Map<String, PartPad> pads = new HashMap<>();  // pad shapes
    private final List<PartShape> shapes = new ArrayList<>();   // normal shapes

    public Package(String id, String name) {
        this.id = id;
        this.name = name;
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
        return name;
    }

    public String getId() {
        return id;
    }


    public Element getXml(Document doc) {
        Element packageNode = doc.createElement("package");
        packageNode.setAttribute("id", id);
        packageNode.setAttribute("name", name);

        getShapes().forEach(shape -> {
            org.w3c.dom.Node shapeNode = shape.getXML(doc);
            packageNode.appendChild(shapeNode);
        });

        getPads().forEach(pad -> {
            org.w3c.dom.Node padNode = pad.getXML(doc);
            packageNode.appendChild(padNode);
        });

        return packageNode;
    }

    @Override
    public String toString() {
        return String.format("Package[name=\"%s\", %s shapes, %s pads]", name, shapes.size(), pads.size());
    }
}
