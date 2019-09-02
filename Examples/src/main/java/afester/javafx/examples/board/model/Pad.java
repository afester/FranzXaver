package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.geometry.Point2D;

/**
 * A Pad is a junction which refers to a specific pin of a Part.
 */
public class Pad extends AbstractNode {

    private final Part part;
    private final String padName; // the physical pad name (unique within a Part)

    private Point2D localPos;

    /**
     * Creates a new Pad.
     *
     * @param part	    The part to which this pad is attached
     * @param padName   The (physical) pad number of this pad
     * @param padPos    The position of the pad (in Part coordinates!!!!)
     */
    public Pad(Part part, String padName, Point2D padPos) {
        super(null, part.globalPadPos(padPos));

        this.localPos = padPos;
        
        this.part = part;
        this.padName = padName;
    }

    public Point2D getLocalPos() {
        return localPos;
    }

    public Part getPart() {
        return part;
    }

    public String getPadName() {
        return padName;
    }

    public org.w3c.dom.Node getXML(Document doc) {
        Element result = doc.createElement("pad");
        result.setAttribute("x", Double.toString(getLocalPos().getX()));
        result.setAttribute("y", Double.toString(getLocalPos().getY()));
        result.setAttribute("pinNumber", padName);      // TODO: This should be renamed to "padName"
        result.setAttribute("id", Integer.toString(id));

        return result;
    }

    /**
     * Returns a unique pad id, consisting of the part number and the pad name
     *
     * @return A board-unique pad id in the form "partName$pinNumber"
     */
    public String getPadId() {
        return part.getName() + "$" + padName;
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


    @Override
    public String toString() {
        return String.format("Pad[part=\"%s\", padName=\"%s\", pos=%s]", 
                             part.getName(), padName, /*pin + "@" + gate,*/ getPosition());  
    }
}
