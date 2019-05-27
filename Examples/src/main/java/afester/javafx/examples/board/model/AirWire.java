package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * An AirWire is a line between two Junctions which has not been routed yet.
 */
public class AirWire extends AbstractWire {

    /**
     * Creates a new AirWire.
     * 
     * @param from The start junction for the AirWire.
     * @param from The end junction for the AirWire.
     */
    public AirWire(AbstractNode from, AbstractNode to) {
        super(from, to);

        // TODO: We need a thicker selectionShape (a thicker transparent line) with the
        // same coordinates
        // so that selecting the line is easier
//        setStrokeWidth(0.3); // 0.2);
//        setStroke(Color.ORANGE);
    }

    @Override
	public Node getXML(Document doc) {
        Element traceNode = doc.createElement("airwire");
        traceNode.setAttribute("from", Integer.toString(getFrom().id));
        traceNode.setAttribute("to", Integer.toString(getTo().id));

        return traceNode;
    }

    
    
    
    
//    private void convertToTrace(MouseEvent e) {
//		Net net = getNet();
//		System.err.println("Clicked AirWire of " + net);
//
//		Point2D clickPoint = new Point2D(e.getX(), e.getY());
//
//		// determine which part of the AirWire to keep
//		double d1 = clickPoint.distance(getStart());
//		double d2 = clickPoint.distance(getEnd());
//		System.err.printf("CLICKED: %s -> %s (%s, %s)\n", clickPoint, this, d1, d2);
//
//		Junction newJunction = new Junction(clickPoint);
//
//		if (d1 < d2) {
//			// keep To
//			AbstractNode nearest = getFrom();
//			System.err.println("NEARBY: " + nearest);
//
//			// connect existing wire to new junction
//			nearest.traceStarts.remove(this);
//			setFrom(newJunction);
//			newJunction.traceStarts.add(this);
//
//			// ***************************************
//			Junction edge = new Junction(nearest.getPos().getX(), newJunction.getCenterY());
//			System.err.println("EDGE1: " + edge);
//
//			// create new Trace: nearest - edge - newJunction
//			Trace t = new Trace(nearest, edge);
//			net.addTrace(t); // TODO: Here, one operation should be sufficient
//			nearest.traceStarts.add(t); // to add the new trace ... (the second line should not be required)
//			edge.traceEnds.add(t);
//
//			Trace t2 = new Trace(edge, newJunction);
//			net.addTrace(t2);
//			edge.traceStarts.add(t2);
//			newJunction.traceEnds.add(t2);
//
//			net.addJunction(edge);
//			net.addJunction(newJunction);
//			// ***************************************
//		} else {
//			// keep From
//			AbstractNode nearest = getTo();
//			System.err.println("NEARBY: " + nearest);
//
//			// connect existing wire to new junction
//			nearest.traceEnds.remove(this);
//			setTo(newJunction);
//			newJunction.traceEnds.add(this);
//
//			// ***************************************
//			Junction edge = new Junction(newJunction.getCenterX(), nearest.getPos().getY());
//			System.err.println("EDGE2: " + edge);
//
//			// create new Trace: newJunction - edge - nearest
//			Trace t = new Trace(newJunction, edge);
//			net.addTrace(t); // TODO: Here, one operation should be sufficient
//			newJunction.traceStarts.add(t);
//			edge.traceEnds.add(t);
//
//			Trace t2 = new Trace(edge, nearest);
//			net.addTrace(t2);
//			edge.traceStarts.add(t2);
//			nearest.traceEnds.add(t2); // to add the new trace ... (the second line should not be required)
//
//			net.addJunction(edge);
//			net.addJunction(newJunction);
//
//			// ***************************************
//		}
//	}


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
    public void convertToStraightTrace() {
//        Net net = getNet();
//
//        AbstractNode pFrom = this.getFrom();
//        AbstractNode pTo = this.getTo();
//
//        // TODO: remove the instanceof's
//        if (pFrom instanceof Junction && pTo instanceof Junction) {
//            System.err.println("JUNCTION/JUNCTION");
//
//            pFrom.traceStarts.remove(this);
//            pTo.traceEnds.remove(this);
//            net.getTraces().remove(this);
//            // update view
//            net.traces.getChildren().remove(this);
//
//            Trace t = new Trace(pFrom, pTo);
//            net.addTrace(t);
//
//        } else if (pFrom instanceof Pad && pTo instanceof Pad) { 
//            System.err.println("PAD/PAD");
//
//            Junction j1 = new Junction(getNet(), pFrom.getPos());
//            net.addJunction(j1);
//            Junction j2 = new Junction(getNet(), pTo.getPos());
//            net.addJunction(j2);
//    
//            pTo.traceEnds.remove(this);
//            this.setTo(j1);
//            j1.traceEnds.add(this);
//    
//            Trace t = new Trace(j1, j2);
//            net.addTrace(t);
//    
//            AirWire aw2 = new AirWire(j2, pTo);
//            net.addTrace(aw2);
//        } else if (pFrom instanceof Junction && pTo instanceof Pad) {
//            System.err.println("JUNCTION/PAD");
//            //    Junction -                       AirWire - Pad 
//            //    Junction - (Trace - Junction2) - AirWire - Pad
//            //    pFrom       new     new          this      pTo
//
//            Junction j2 = new Junction(getNet(), pTo.getPos());
//            net.addJunction(j2);
//
//            pFrom.traceStarts.remove(this);
//            this.setFrom(j2);
//            j2.traceStarts.add(this);
//
//            Trace t = new Trace(pFrom, j2);
//            net.addTrace(t);
//        } else if (pFrom instanceof Pad && pTo instanceof Junction) {
//            System.err.println("PAD/JUNCTION");
//
//            //    Pad - AirWire - Junction 
//            //    Pad - AirWire - (Junction2 - Trace) - Junction
//            //    pFrom this       new         new      pTo
//
//            Junction j2 = new Junction(getNet(), pFrom.getPos());
//            net.addJunction(j2);
//
//            pTo.traceEnds.remove(this);
//            this.setTo(j2);
//            j2.traceEnds.add(this);
//
//            Trace t = new Trace(j2, pTo);
//            net.addTrace(t);
//        }
    }
//
//
//    protected void setSegmentSelected(boolean isSelected) {
//        BoardView bv = getNet().getBoardView();
//        bv.getHandleGroup().getChildren().clear();
//        if (isSelected) {
//            // Both ends of an Airwire can ALLWAYS be moved to a different node  
//
////            if (from instanceof Junction) {
////                from.setSelected(true);
////            } else {
//                System.err.println("Adding handle for FROM: " + from.getPos());
//                Handle handle = new FromHandle(getNet(), this);
//                bv.getHandleGroup().getChildren().add(handle);
////            }
////            if (to instanceof Junction) {
////                to.setSelected(true);
////            } else {
//                System.err.println("Adding handle for TO: " + to.getPos());
//                handle = new ToHandle(getNet(), this);
//                bv.getHandleGroup().getChildren().add(handle);
////            }
//
//            setStroke(Color.RED);
//        } else {
////            if (from instanceof Junction) {
////                from.setSelected(false);
////            }
////            if (to instanceof Junction) {
////                to.setSelected(false);
////            }
//            setStroke(Color.ORANGE);
//        }
//    }

    @Override
    public String toString() {
        return String.format("AirWire[%s - %s]", getFrom(), getTo());
    }
}
