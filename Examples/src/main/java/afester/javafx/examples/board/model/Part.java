package afester.javafx.examples.board.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import afester.javafx.examples.board.model.Board.IntVal;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;

public class Part {

    private final String partName;
    private final String partValue;
    private Package thePackage;

    private Map<String, Pad> pads = new HashMap<>();

    // Position of the part
    private ObjectProperty<Point2D> position = new SimpleObjectProperty<>(new Point2D(0, 0));
    public ObjectProperty<Point2D> positionProperty() { return position; }
    public void setPosition(Point2D pos) { 
        position.setValue(pos);
        getPads().forEach(pad -> pad.setPosition(this.globalPadPos(pad.getLocalPos())));
    }
    public Point2D getPosition() { return position.getValue(); }

    // Rotation of the part
    private DoubleProperty rotation = new SimpleDoubleProperty(0.0);
    public DoubleProperty rotationProperty() { return rotation; } 
    public void setRotation(double angle) { 
        rotation.setValue(angle); 
        getPads().forEach(pad -> pad.setPosition(this.globalPadPos(pad.getLocalPos())));
    }
    public double getRotation() { return rotation.getValue(); }



    /**
     * Creates a new Part.
     *
     * @param partName  The name of the part
     * @param partValue The part value
     * @param packageRef The name of the part's package.
     */
    public Part(String partName, String partValue, Package pkg) {
        this.partName = partName;
        this.partValue = partValue;
        this.thePackage = pkg;
    }

    @Deprecated
    public Part(String partName, String partValue, String packageRef) {
        this.partName = partName;
        this.partValue = partValue;
        this.thePackage = new Package(packageRef, packageRef);
    }

    public Package getPackage() {
        return thePackage;
    }

    public void addPad(Pad pad) { /// , String pinId) {
        System.err.println("ADDING:" + pad);
        final String key = pad.getPadName();
        pads.put(key, pad);
    }

    public Pad getPad(String pinId) {
        return pads.get(pinId);
    }

    @Deprecated
    public void addShape(PartShape shape) {
        thePackage.addShape(shape);
        // shapes.add(shape);
    }

    @Deprecated
    public List<PartShape> getShapes() {
        return thePackage.getShapes();
//        return shapes;
    }

    public Collection<Pad> getPads() {
        return pads.values();
    }

    /**
     * @return The name / reference of this part (like R1, U3, C2)
     */
    public String getName() {
        return partName;
    }

    /**
     * @return The value of this part (like 1,2k / 4,7µF / BC547)
     */
    public String getValue() {
        return partValue;
    }

    /**
     * @return The package name of this part (like 205/07)
     */
    public String getPackageRef() {
        // return packageRef;
        return thePackage.getName();
    }



    public Element getXml(Document doc, IntVal junctionId) {
        Element partNode = doc.createElement("part");
        partNode.setAttribute("name", partName);
        partNode.setAttribute("value", partValue);
        partNode.setAttribute("packageRef", thePackage.getId());
        partNode.setAttribute("x", Double.toString(getPosition().getX()));
        partNode.setAttribute("y", Double.toString(getPosition().getY()));
        partNode.setAttribute("rotation", Double.toString(getRotation()));

        for (Pad ps : getPads()) {
            ps.setId(junctionId.val++);
            org.w3c.dom.Node padNode = ps.getXML(doc);
            partNode.appendChild(padNode);
        }

        return partNode;
    }


    protected boolean replacedWith(Part p2) {
        // This is a first trivial attempt to decide whether the package for the part has changed:
        return !getPackageRef().equals(p2.getPackage().getName()) || 
                getValue().equals(p2.getValue()) || 
                getName().equals(p2.getName());
    }


    /**
     * Rotates the part 90° clockwise. 
     */
    public void rotateClockwise() {
        double rot = getRotation();
        rot += 90;
        if (rot >= 360) {
            rot = 0;
        }
        setRotation(rot);
    }
    

    /**
     * Calculates the global position from a position which is relative to this part.
     * This is required to properly set the global Pad positions to be able to 
     * connect the traces in a simple way.
     *
     * @param localPos The coordinates, local to the Part
     * @return The global coordinates
     */
    public Point2D globalPadPos(Point2D localPos) {
        double angle = getRotation() * (Math.PI/180); // Convert to radians
        double rotatedX = Math.cos(angle) * localPos.getX() - Math.sin(angle) * localPos.getY();
        double rotatedY = Math.sin(angle) * localPos.getX() + Math.cos(angle) * localPos.getY();
        Point2D rot = new Point2D(rotatedX, rotatedY);
        return rot.add(getPosition());
    }

    @Override
    public String toString() {
        String padList = getPads().stream()
                                  .map( p -> p.getPadName())
                                  .collect( Collectors.joining(",") ); 
        return String.format("Part[name=\"%s\", pos=%s, pads=[%s]]", partName, getPosition(), padList);
    }
}
