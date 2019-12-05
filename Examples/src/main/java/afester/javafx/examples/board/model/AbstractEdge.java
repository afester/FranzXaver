package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;

/**
 * An AbstractEdge is the basic edge in a net graph. It connects exactly two 
 * Nodes (either Pin or Junction).
 */
public abstract class AbstractEdge {
    
    // The "from" node of the edge
    private ObjectProperty<AbstractNode> from = new SimpleObjectProperty<AbstractNode>();
    public ObjectProperty<AbstractNode> fromProperty() { return from; }
    public void setFrom(AbstractNode node) { from.setValue(node); }
    public AbstractNode getFrom() { return from.get();  }

    // The "to" node of the edge
    private ObjectProperty<AbstractNode> to = new SimpleObjectProperty<AbstractNode>();
    public ObjectProperty<AbstractNode> toProperty() { return to; }
    public void setTo(AbstractNode node) { to.setValue(node); }
    public AbstractNode getTo() { return to.get();  }

    // The "state" of the edge
    private ObjectProperty<AbstractWireState> state = new SimpleObjectProperty<AbstractWireState>(AbstractWireState.NORMAL);
    public ObjectProperty<AbstractWireState> stateProperty() { return state; }
    public void setState(AbstractWireState stat) { state.setValue(stat); }
    public AbstractWireState getState() { return state.get(); }

    // A flag to indicate whether the edge is currently hidden
    private final BooleanProperty isHidden = new SimpleBooleanProperty(false);
    public BooleanProperty isHiddenProperty() { return isHidden; }
    public boolean isHidden() { return isHidden.get(); }
    public void setHidden(boolean flag) { isHidden.set(flag); }

    private Net net;

    /**
     * Creates a new AbstractEdge.
     *
     * @param from The first node of the edge
     * @param to The second node of the edge
     * @param net The net which contains the edge
     */
    public AbstractEdge(AbstractNode from, AbstractNode to, Net net) {
        setFrom(from);
        setTo(to);
        this.net = net;

        from.addEdge(this);
        to.addEdge(this);
    }


    abstract public TraceType getType();

    /**
     * @return The Net this AbstractWire is part of.
     */
    public Net getNet() {
        return net; 
    }

    // The visual state of the wire
    public enum AbstractWireState {
        NORMAL, HIGHLIGHTED, SELECTED;
    }

    public abstract Node getXML(Document doc);

    /**
     * Returns the opposite node of the given node.
     *  
     * @param node The node for which to get the opposite node.
     * @return The opposite node.
     * 
     * @throws RuntimeException if the given node is not connected to this edge. 
     */
    public AbstractNode getOtherNode(AbstractNode node) {
        if (getFrom() == node) {
            return getTo();
        }
        if (getTo() == node) {
            return getFrom();
        }

        throw new RuntimeException("Unexpected: Edge does neither go FROM nor TO the given node!");
    }


    public abstract void reconnectToNearestJunction(Point2D clickPos);


    public abstract void reconnectFromNearestJunction(Point2D clickPos);


    /**
     * Reconnects this edge from one node to another node.
     *
     * @param currentNode The current node to which the edge is connected.
     * @param newNode The new node to which the edge shall be connected.
     */
    public void reconnect(AbstractNode currentNode, AbstractNode newNode) {
        if (getFrom() == currentNode) {
            currentNode.getEdges().remove(this);
            newNode.getEdges().add(this);
            setFrom(newNode);
        } else if (getTo() == currentNode) {
            currentNode.getEdges().remove(this);
            newNode.getEdges().add(this);
            setTo(newNode);
        } else {
            throw new RuntimeException("Unexpected: Edge does neither go FROM nor TO the given node!");
        }
    }


    public void convertToStraightTrace() {
    }


    public void splitTrace(Point2D newPos) {
    }


    public void remove() {
        System.err.println("Removing AirWire: " + this);
        AbstractNode from = getFrom();
        AbstractNode to = getTo();

        // Pin - Pin    No deletion allowed
        // Jun - Jun    Deletion allowed, keep one of the junctions
        // Pin - Jun    Deletion allowed, keep the Pin (TODO: probably should not be allowed if this results in 
        //              a real Trace being connected to the Pin!) 
        // Jun - Pin    Deletion allowed, keep the Pin
        if (from instanceof Junction && to instanceof Junction) {    // TODO
            System.err.println("Junction-Junction");
            getNet().removeTraceAndFrom(this);
        } else if (from instanceof Junction && to instanceof Pin) {    // TODO
            System.err.println("Junction-Pin");
            getNet().removeTraceAndFrom(this);
        } else if (from instanceof Pin && to instanceof Junction) {    // TODO
            System.err.println("Pin-Junction");
            getNet().removeTraceAndTo(this);
        } else {
            System.err.println("Pin-Pin => NOT ALLOWED");
        }
    }

    
    public void move(Point2D newPos) {
        AbstractNode fromNode = getFrom();
        AbstractNode toNode = getTo();
        if (fromNode instanceof Pin || toNode instanceof Pin) {
            // Alternatively: Move the whole part which is connected to the Pin!
            return;
        }

        final var delta = toNode.getPosition().subtract(fromNode.getPosition());

        // The start node is the reference point and can directly be set to the new position.
        // final var snappedPos = net.b boardView.snapToGrid(newPos, false);
        fromNode.setPosition(newPos);

        // update dependent positions
        final var newToPos = fromNode.getPosition().add(delta);
        toNode.setPosition(newToPos);
    }

}
