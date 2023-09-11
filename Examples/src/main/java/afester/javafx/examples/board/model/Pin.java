package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.geometry.Point2D;

/**
 * A Pin is a junction which refers to a specific pin of a Part.
 */
public final class Pin extends AbstractNode {

    private final Part part;
    private final ShapePad pad;

    /**
     * Creates a new Pin.
     *
     * @param part	    The part to which this pad is attached
     * @param padName   The (physical) pad number of this pad
     * @param padPos    The position of the pad (in Part coordinates!!!!)
     */
    public Pin(Part part, ShapePad pad) {
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
    public String toString() {
        return String.format("Pin[part=\"%s\", padName=\"%s\", pos=%s]", 
                             (part == null? "null" : part.getName()), getPadName(), /*pin + "@" + gate,*/ getPosition());  
    }
}
