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
    private final Map<String, ShapePad> pads = new HashMap<>();  // pad shapes
    private final List<ShapeModel> shapes = new ArrayList<>();   // normal shapes

    public Package(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addPad(ShapePad pad) {
        final String key = pad.getName();
        pads.put(key, pad);
    }


    public ShapePad getPad(String padName) {
        return pads.get(padName);
    }

    public void addShape(ShapeModel shape) {
        shapes.add(shape);
    }

    public List<ShapeModel> getShapes() {
        return shapes;
    }

    public Collection<ShapePad> getPads() {
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
