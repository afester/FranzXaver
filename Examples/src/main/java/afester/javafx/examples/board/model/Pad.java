package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.geometry.Point2D;

/**
 * A Pad is a junction which refers to a specific pin of a Part.
 */
public class Pad extends AbstractNode {

    private final Part part;
    private final String pinNumber;
    private Point2D localPos;

    /**
     * Creates a new Pad.
     *
     * @param part	    The part to which this pad is attached
     * @param pinNumber The (physical) pin number of this pad
     * @param padPos    The position of the pad (in Part coordinates!!!!)
     */
    public Pad(Part part, String pinNumber, Point2D padPos) {
        super(null, part.globalPadPos(padPos));

        this.localPos = padPos;
        
        this.part = part;
        this.pinNumber = pinNumber;
    }

    public Point2D getLocalPos() {
        return localPos;
    }

    public Part getPart() {
        return part;
    }

    public String getPinNumber() {
        return pinNumber;
    }

    public void seNet(Net net) {
        this.net = net;
    }

/*****************************/
//    
//
//    Node partNode = null;
//    @Override
//    public Point2D getPos() {
//        if (partNode != null) {
//            return partNode.localToParent(getCenterX(), getCenterY());
//        }
//        return new Point2D(0, 0); 
//    }
/*****************************/


    // NOTE: This shall return the global position of the pad, however this also depends on the rotation!!! 
//    public Point2D getPos() {
//        Point2D result = part.getPosition().add(super.getPos()); // pad. pos; // return new Point2D(getCenterX(), getCenterY());
//    }

// This is a hack to track the global connect position for the Pad.
//    private Point2D connectPos = new Point2D(0, 0);
//    public Point2D getConnectPosition() {
//        return connectPos;
//    }
//    public void setConnectPosition(Point2D pos) {
//        connectPos = pos;
//    }


//
//
//    @Override   // DEBUG ONLY
//    public void setSelected(boolean isSelected, Color col) {
//        Shape pad = (Shape) view.getChildrenUnmodifiable().get(0);  // HACK
//        if (isSelected) {
//            pad.setFill(col);
//        } else {
//            pad.setFill(null);
//        }
//    }

//    @Override
    public org.w3c.dom.Node getXML(Document doc) {
        Element result = doc.createElement("pad");
        result.setAttribute("x", Double.toString(getPos().getX()));
        result.setAttribute("y", Double.toString(getPos().getY()));
        result.setAttribute("pinNumber", pinNumber);
        result.setAttribute("id", Integer.toString(id));

        return result;
    }

    /**
     * Returns a unique pad id, consisting of the part number and the pin number
     *
     * @return A board-unique pad id in the form "partName$pinNumber"
     */
    public String getPadId() {
        return part.getName() + "$" + pinNumber;
    }

    @Override
    public int hashCode() {
        final String key = getPadId();

        final int prime = 31;
        int result = 1;
        result = prime * result + key.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pad other = (Pad) obj;
        
        final String key = getPadId();

        if (!key.equals(other.getPadId()))
            return false;
        return true;
    }

//    @Override
//    public String getRepr() {
//        return "Pad: " + getPadId();
//    }


    @Override
    public String toString() {
        return String.format("Pad[part=\"%s\", padName=%s, pos=%s]", 
                             part.getName(), pinNumber, /*pin + "@" + gate,*/ getPos());  
    }
//
//    @Override
//    public Node createNode() {
//        throw new RuntimeException("NYI");
//    }

}
