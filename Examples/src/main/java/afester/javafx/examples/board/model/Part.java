package afester.javafx.examples.board.model;

import java.util.ArrayList;
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
    private final String packageRef;      // TODO: This should refer to a "package / footprint template"
    private Map<String, Pad> pads = new HashMap<>();
    private List<PartShape> shapes = new ArrayList<>();

    // Position of the part
    private ObjectProperty<Point2D> position = new SimpleObjectProperty<>(new Point2D(0, 0));
    public ObjectProperty<Point2D> positionProperty() { return position; }
    public void setPosition(Point2D pos) { 
        position.setValue(pos);
        getPads().forEach(pad -> pad.setPosition(this.globalPadPos(pad.getLocalPos())));
    }
    public Point2D getPosition() { return position.getValue(); }

    // direction of the part
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
    public Part(String partName, String partValue, String packageRef) {
        this.partName = partName;
        this.partValue = partValue;
        this.packageRef = packageRef;
    }


    public void addPad(Pad pad) { /// , String pinId) {
        System.err.println("ADDING:" + pad);
        final String key = pad.getPadName();
        pads.put(key, pad);
    }

    public Pad getPad(String pinId) {
        return pads.get(pinId);
    }

    public void addShape(PartShape shape) {
        shapes.add(shape);
    }

    public List<PartShape> getShapes() {
        return shapes;
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
        return packageRef;
    }



    public Element getXml(Document doc, IntVal junctionId) {
        Element partNode = doc.createElement("part");
        partNode.setAttribute("name", partName);
        partNode.setAttribute("value", partValue);
        partNode.setAttribute("package", packageRef);
        partNode.setAttribute("x", Double.toString(getPosition().getX()));
        partNode.setAttribute("y", Double.toString(getPosition().getY()));
        partNode.setAttribute("rotation", Double.toString(getRotation()));

        for (PartShape ps : getShapes()) {
            org.w3c.dom.Node shapeNode = ps.getXML(doc);
            partNode.appendChild(shapeNode);
        }

        for (Pad ps : getPads()) {
            ps.setId(junctionId.val++);
            org.w3c.dom.Node padNode = ps.getXML(doc);
            partNode.appendChild(padNode);
        }

        return partNode;
    }

    protected boolean replacedWith(Part p2) {
        // This is a first trivial attempt to decide whether the package for the part has changed:
        return !packageRef.equals(p2.packageRef);
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
                                  .collect( Collectors.joining( "," ) ); 
        return String.format("Part[partName=%s, pads=[%s]]", partName, padList);
    }
}
