package afester.javafx.examples.board;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.scene.paint.Color;

/**
 * An AirWire is a line between two Junctions which has not been routed yet.
 */
public class AirWire extends Trace {

    /**
     * Creates a new AirWire.
     * 
     * @param from The start junction for the AirWire.
     * @param from The end junction for the AirWire.
     */
    public AirWire(Junction from, Junction to) {
        super(from, to);

        // TODO: We need a thicker selectionShape (a thicker transparent line) with the
        // same coordinates
        // so that selecting the line is easier
        setStrokeWidth(0.3); // 0.2);
        setStroke(Color.ORANGE);
    }

    public Node getXML(Document doc) {
        Element traceNode = doc.createElement("airwire");
        traceNode.setAttribute("from", Integer.toString(getFrom().id));
        traceNode.setAttribute("to", Integer.toString(getTo().id));

        return traceNode;
    }

    @Override
    public String toString() {
        return String.format("AirWire[%s - %s]", getFrom(), getTo());
    }
}
