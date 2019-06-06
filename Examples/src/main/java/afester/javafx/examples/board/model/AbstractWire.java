package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;

/**
 * An AbstractWire is the basic edge in a net graph. It connects exactly two junctions.
 */
public abstract class AbstractWire {
    protected AbstractNode from;
    protected AbstractNode to;
    protected Net net;

    
    

    public AbstractWire(AbstractNode from, AbstractNode to, Net net) {
        this.from = from;
        this.to = to;
        this.net = net;

        from.addStart(this);
        to.addEnd(this);
        
        setStart(from.getPos());
        setEnd(to.getPos());
    }


    abstract public TraceType getType();

    /**
     * @return The Net this AbstractWire is part of.
     */
    public Net getNet() {
        return net; 
    }


// some geometric stuff - not part of the persistet model!

    // The start point of the wire (TODO: can this be combined with from.getPos()????)
    private ObjectProperty<Point2D> startPoint = new SimpleObjectProperty<Point2D>(Point2D.ZERO);
    public ObjectProperty<Point2D> startPointProperty() { return startPoint; }
    public void setStart(Point2D pos) { startPoint.setValue(pos); }
    public Point2D getStart() { return startPoint.get();  }

    // The end point of the wire (TODO: can this be combined with to.getPos()????)
    private ObjectProperty<Point2D> endPoint = new SimpleObjectProperty<Point2D>();
    public ObjectProperty<Point2D> endPointProperty() { return endPoint; }
    public void setEnd(Point2D pos) { endPoint.setValue(pos); }
    public Point2D getEnd() { return endPoint.get(); }

    // The visual state of the wire
    public enum AbstractWireState {
        NORMAL, HIGHLIGHTED, SELECTED;
    }

    private ObjectProperty<AbstractWireState> state = new SimpleObjectProperty<AbstractWireState>(AbstractWireState.NORMAL);
    public ObjectProperty<AbstractWireState> stateProperty() { return state; }
    public void setState(AbstractWireState stat) { state.setValue(stat); }
    public AbstractWireState getState() { return state.get(); }

//    private ObjectProperty<Color> color = new SimpleObjectProperty<Color>(Color.LIGHTGRAY);
//    public ObjectProperty<Color> colorProperty() { return color; }
//    public void setColor(Color col) { color.setValue(col); }
//    public Color getColor() { return color.get(); }


    public AbstractNode getFrom() {
        return from;
    }

    public AbstractNode getTo() {
        return to;
    }

//    public void setFrom(AbstractNode newJunction) {
//        from = newJunction;
//        setStart(from.getPos());
//    }
//
//    public void setTo(AbstractNode newJunction) {
//        to = newJunction;
//        setEnd(to.getPos());
//    }

    public abstract Node getXML(Document doc);

    public AbstractNode getOtherNode(AbstractNode node) {
        if (from == node) {
            return to;
        }
        if (to == node) {
            return from;
        }

        throw new RuntimeException("Unexpected: Edge does neither go FROM nor TO the given node!");
    }


    public void setSelected(boolean b) {
    }



//
//    /**
//     * Reconnects this edge from one node to another node.
//     *
//     * @param currentNode The current node to which the edge is connected.
//     * @param newNode The new node to which the edge shall be connected.
//     */
//    public void reconnect(AbstractNode currentNode, AbstractNode newNode) {
//        if (from == currentNode) {
//            currentNode.traceStarts.remove(this);
//            newNode.traceStarts.add(this);
//            from = newNode;
//            
//            setStart(newNode.getPos());
//        } else if (to == currentNode) {
//            currentNode.traceEnds.remove(this);
//            newNode.traceEnds.add(this);
//            to = newNode;
//
//            setEnd(newNode.getPos());
//        } else {
//            throw new RuntimeException("Unexpected: Edge does neither go FROM nor TO the given node!");
//        }
//    }
}
