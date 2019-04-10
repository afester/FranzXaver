package afester.javafx.examples.board;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * An AirWire is a line between two Junctions which has not been routed yet.
 */
public class AirWire extends Trace implements Interactable {

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
    public void mousePressed(MouseEvent e, BoardView bv) {
        Net net = (Net) getParent(); // TODO: provide an explicit access path
        System.err.println("Clicked AirWire of " + net);

          Point2D clickPoint = new Point2D(e.getX(), e.getY());

          // determine which part of the AirWire to keep 
          double d1 = clickPoint.distance(getStart());
          double d2 = clickPoint.distance(getEnd());
          System.err.printf("CLICKED: %s -> %s (%s, %s)\n", clickPoint, this, d1, d2);

          Junction newJunction = new Junction(clickPoint);

          if (d1 < d2) {
              // keep To
              Junction nearest = getFrom();
              System.err.println("NEARBY: " + nearest);

              // connect existing wire to new junction
              nearest.traceStarts.remove(this);
              setFrom(newJunction);

//***************************************
              Junction edge = new Junction(nearest.getPos().getX(), newJunction.getYpos());
              System.err.println("EDGE1: " + edge);

              // create new Trace: nearest - edge - newJunction
              Trace t = new Trace(nearest, edge);
              net.addTrace(t);            // TODO: Here, one operation should be sufficient
              nearest.traceStarts.add(t); // to add the new trace ... (the second line should not be required)
              edge.traceEnds.add(t);

              Trace t2 = new Trace(edge, newJunction);
              net.addTrace(t2);
              edge.traceStarts.add(t2);
              newJunction.traceEnds.add(t2);
              
              net.addJunction(edge);
              net.addJunction(newJunction);
//***************************************
          } else {
              // keep From
              Junction nearest = getTo();
              System.err.println("NEARBY: " +  nearest);

              // connect existing wire to new junction
              nearest.traceEnds.remove(this);
              setTo(newJunction);

//***************************************                        
              Junction edge = new Junction(newJunction.getXpos(), nearest.getPos().getY());
              System.err.println("EDGE2: " + edge);

              // create new Trace: newJunction - edge - nearest
              Trace t = new Trace(newJunction, edge);
              net.addTrace(t);                    // TODO: Here, one operation should be sufficient
              newJunction.traceStarts.add(t);
              edge.traceEnds.add(t);

              Trace t2 = new Trace(edge, nearest);
              net.addTrace(t2);;
              edge.traceStarts.add(t2);
              nearest.traceEnds.add(t2);           // to add the new trace ... (the second line should not be required)

              net.addJunction(edge);
              net.addJunction(newJunction);

//***************************************
          }
    }

    @Override
    public String toString() {
        return String.format("AirWire[%s - %s]", getFrom(), getTo());
    }
}
