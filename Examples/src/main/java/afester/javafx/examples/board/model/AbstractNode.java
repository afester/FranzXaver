package afester.javafx.examples.board.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;


public abstract class AbstractNode {
    private Net net;
    List<AbstractWire> traceStarts = new ArrayList<>();
    List<AbstractWire> traceEnds = new ArrayList<>();

    protected int id;	// currently only required for serialization and deserialization

    // The position of the node
    private ObjectProperty<Point2D> position = new SimpleObjectProperty<Point2D>(Point2D.ZERO);
    public ObjectProperty<Point2D> positionProperty() { return position; }
    public void setPosition(Point2D pos) {
        position.setValue(pos);
        traceStarts.forEach(trace -> trace.setStart(pos));
        traceEnds.forEach(trace -> trace.setEnd(pos));
    }
    public Point2D getPosition() { return position.get(); }

    // The color of this node
    private ObjectProperty<Color> color = new SimpleObjectProperty<Color>(Color.LIGHTGRAY);
    public ObjectProperty<Color> colorProperty() { return color; }
    public void setColor(Color col) { color.setValue(col); }
    public Color getColor() { return color.get(); }


    public AbstractNode(Net net, Point2D pos) {
        this.net = net;
        setPosition(pos);
    }

    public void setId(int i) {
       id = i;
    }

    public void addStart(AbstractWire wire) {
        traceStarts.add(wire);
    }

    public void addEnd(AbstractWire wire) {
        traceEnds.add(wire);
    }


    /**
     * @return A list of all edges which are connected to this node.
     */
    public List<AbstractWire> getEdges() {
        List<AbstractWire> result = new ArrayList<>();

        result.addAll(traceStarts);
        result.addAll(traceEnds);

        return result;
    }


    /**
     * From a collection of nodes, get the one which is nearest to this one.
     *
     * @param nodeList The list of nodes from which to get the nearest one.
     * @return The node which is the nearest to this one.
     */
    public AbstractNode getNearestNode(List<AbstractNode> nodeList) {
        return net.getNearestNode(getPosition(), nodeList);
    }

    public Net getNet() {
        return net;
    }

    /**
     * @return The number of edges which are connected to this node.
     */
    public int getEdgeCount() {
        return traceStarts.size() + traceEnds.size();
    }
}
