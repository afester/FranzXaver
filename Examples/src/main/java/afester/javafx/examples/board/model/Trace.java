package afester.javafx.examples.board.model;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;

/**
 * A Trace is one segment of a Net which can either be an Airwire,
 * a Trace or a Bridge. 
 */
public class Trace extends AbstractEdge {

    // The type of this trace.
    private ObjectProperty<TraceType> traceType = new SimpleObjectProperty<>(TraceType.AIRWIRE);
    public ObjectProperty<TraceType> traceTypeProperty() { return traceType; }
    public void setTraceType(TraceType type) { traceType.setValue(type); }
    public TraceType getTraceType() { return traceType.getValue(); }

    /**
     * Creates a new Trace of the specified type.
     *
     * @param type
     */
    public Trace(TraceType type) {
        setTraceType(type);
    }


    @Override
    public Node getXML(Document doc)  {
        Element traceNode;
        if (getTraceType() == TraceType.AIRWIRE) {
            traceNode = doc.createElement("airwire");
            traceNode.setAttribute("from", Integer.toString(getFrom().id));
            traceNode.setAttribute("to", Integer.toString(getTo().id));
        } else {
            traceNode = doc.createElement("trace");
            traceNode.setAttribute("from", Integer.toString(getFrom().id));
            traceNode.setAttribute("to",   Integer.toString(getTo().id));
            if (getTraceType() == TraceType.BRIDGE) {
                traceNode.setAttribute("isBridge",  Boolean.TRUE.toString());
            } else {
                traceNode.setAttribute("isBridge",  Boolean.FALSE.toString());
            }
        }

        return traceNode;
    }

    @Override
    public void splitTrace(Point2D pos) {
        Junction newJunction = new Junction(pos);
        getNet().addJunction(newJunction);
        
        AbstractNode oldDest = getTo();
        reconnect(getTo(), newJunction);

        Trace newTrace = new Trace(TraceType.TRACE);
        getNet().addTrace(newTrace, newJunction, oldDest);
//        Trace newTrace = new Trace(newJunction, oldDest, getNet(), TraceType.TRACE);
//        getNet().addTrace(newTrace);
    }

    /**
     * Converts this AirWire to a straight Trace.
     * An Airwire connects two Junctions - the AirWire is replaced by two AirWires, two junctions and one Trace:
     *
     *     Junction - AirWire - Junction 
     * ==> Junction - Trace - Junction
     *  
     *     Pad - AirWire - Pad 
     * ==> Pad - AirWire - (Junction1 - Trace - Junction2 - AirWire2) - Pad
     *
     *     Junction - AirWire - Pad 
     * ==> Junction - (Trace - Junction1) - AirWire - Pad
     *
     *     Pad - AirWire - Junction
     * ==> Pad - AirWire - (Junction1 - Trace) - Junction
     * 
     * The new junctions are initially at the locations of the existing junctions, so that initially the 
     * complete airwire is rendered as being replaced by the Trace. So, the 
     * junctions and the associated parts can still be moved!
     *
     * TODO: There might already exist a junction at one or both of the locations!
     * In that case, the new trace can be directly connected to the existing junction.
     */
    @Override
    public void convertToStraightTrace() {
        Net net = getNet();

        AbstractNode pFrom = this.getFrom();
        AbstractNode pTo = this.getTo();

        // TODO: remove the instanceof's
        if (pFrom instanceof Junction && pTo instanceof Junction) {
            System.err.println("JUNCTION/JUNCTION");

//            pFrom.traceStarts.remove(this);
//            pTo.traceEnds.remove(this);
//            net.getTraces().remove(this);
//            // update view
//            net.traces.getChildren().remove(this);
//
//            Trace t = new Trace(pFrom, pTo);
//            net.addTrace(t);

        } else if (pFrom instanceof Pin && pTo instanceof Pin) { 
            System.err.println("PAD/PAD");
            //     Pad - AirWire - Pad 
            // ==> Pad - AirWire - (Junction1 - Trace - Junction2 - AirWire2) - Pad
            //

            Junction j1 = new Junction(pFrom.getPosition());
            net.addJunction(j1);
    
            reconnect(pTo, j1);

            Junction j2 = new Junction(pTo.getPosition());
            net.addJunction(j2);

            
            Trace t = new Trace(TraceType.TRACE);
            net.addTrace(t, j1, j2);
//            Trace t = new Trace(j1, j2, getNet(), TraceType.TRACE);
//            net.addTrace(t);
    
            Trace aw2 = new Trace(TraceType.AIRWIRE);
            net.addTrace(aw2, j2, pTo);
//            Trace aw2 = new Trace(j2, pTo, getNet(), TraceType.AIRWIRE);
//            net.addTrace(aw2);
        } else if (pFrom instanceof Junction && pTo instanceof Pin) {
            System.err.println("JUNCTION/PAD");

            //    Junction -                       AirWire - Pad 
            //    Junction - (Trace - Junction2) - AirWire - Pad
            //    pFrom       new     new          this      pTo

            Junction j2 = new Junction(pTo.getPosition());
            net.addJunction(j2);

            reconnect(pFrom, j2);

            Trace t = new Trace(TraceType.TRACE);
            net.addTrace(t, pFrom, j2);
//            Trace t = new Trace(pFrom, j2, getNet(), TraceType.TRACE);
//            net.addTrace(t);
        } else if (pFrom instanceof Pin && pTo instanceof Junction) {
            System.err.println("PAD/JUNCTION");

            //    Pad - AirWire - Junction 
            //    Pad - AirWire - (Junction2 - Trace) - Junction
            //    pFrom this       new         new      pTo

            Junction j2 = new Junction(pFrom.getPosition());
            net.addJunction(j2);

            reconnect(pTo, j2);

            Trace t = new Trace(TraceType.TRACE);
            net.addTrace(t, j2, pTo);
//            Trace t = new Trace(j2, pTo, getNet(), TraceType.TRACE);
//            net.addTrace(t);
        }
    }

    @Override
    public void reconnectToClosestJunction(Point2D clickPos) {
        System.err.println("reconnectToNearestJunction");
        
        // get all nodes which are reachable in the net from the otherNode IF the given airwire would not exist
        List<AbstractNode> possibleNodes = getTo().getNodesWithout(this);
        AbstractNode nearestNode = AbstractNode.getClosestNode(clickPos, possibleNodes);
        System.err.println("NEAREST NODE:" + nearestNode);
        // Reconnect the edge from the old node to the new nearest node
        reconnect(this.getTo(), nearestNode);
    }

    @Override
    public void reconnectFromClosestJunction(Point2D clickPos) {
        System.err.println("reconnectFromNearestJunction");

        // get all nodes which are reachable in the net from the otherNode IF the given airwire would not exist
        List<AbstractNode> possibleNodes = getFrom().getNodesWithout(this);
        AbstractNode nearestNode = AbstractNode.getClosestNode(clickPos, possibleNodes);
        System.err.println("NEAREST NODE:" + nearestNode);
        // Reconnect the edge from the old node to the new nearest node
        reconnect(this.getFrom(), nearestNode);
    }


    @Override
    public String toString() {
        return String.format("Trace[%s - %s]", this.getFrom(), this.getTo());
    }
}
