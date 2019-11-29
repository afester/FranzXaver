package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.geometry.Point2D;

/**
 * A Pin is a junction which refers to a specific pin of a Part.
 */
public final class Pin extends AbstractNode {

    private final Part part;
    private final PartPad pad;

    /**
     * Creates a new Pin.
     *
     * @param part	    The part to which this pad is attached
     * @param padName   The (physical) pad number of this pad
     * @param padPos    The position of the pad (in Part coordinates!!!!)
     */
    public Pin(Part part, PartPad pad) {
        super(part.globalPadPos(pad.getPos()));

        this.part = part;
        this.pad = pad;
    }

    public Point2D getLocalPos() {
        return pad.getPos();
    }

    public Part getPart() {
        return part;
    }

    public String getPadName() {
        return pad.getName();
    }

    public org.w3c.dom.Node getXML(Document doc) {
        Element result = doc.createElement("pad");
        result.setAttribute("padName", getPadName());
        result.setAttribute("id", Integer.toString(id));

        return result;
    }

    /**
     * Returns a unique pad id, consisting of the part number and the pad name
     *
     * @return A board-unique pad id in the form "partName$pinNumber"
     */
    public String getPadId() {
        return part.getName() + "$" + getPadName();
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
        Pin other = (Pin) obj;
        
        final String key = getPadId();

        if (!key.equals(other.getPadId()))
            return false;
        return true;
    }


    @Override
    public String toString() {
        return String.format("Pin[part=\"%s\", padName=\"%s\", pos=%s]", 
                             (part == null? "null" : part.getName()), getPadName(), /*pin + "@" + gate,*/ getPosition());  
    }
}
