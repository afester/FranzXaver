package afester.javafx.examples.board.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/**
 * An AbstractNode is the basic node in a net graph. 
 */
public abstract class AbstractNode {
    private List<AbstractEdge> edges = new ArrayList<>();

    // The position of the node
    private ObjectProperty<Point2D> position = new SimpleObjectProperty<Point2D>(Point2D.ZERO);
    public ObjectProperty<Point2D> positionProperty() { return position; }
    public void setPosition(Point2D pos) { position.setValue(pos); }
    public Point2D getPosition() { return position.get(); }

    // The color of this node
    private ObjectProperty<Color> color = new SimpleObjectProperty<Color>(Color.LIGHTGRAY);
    public ObjectProperty<Color> colorProperty() { return color; }
    public void setColor(Color col) { color.setValue(col); }
    public Color getColor() { return color.get(); }


    /**
     * Creates a new Node at the given position.
     *
     * @param pos The position of the new node.
     */
    public AbstractNode(Point2D pos) {
        setPosition(pos);
    }

    protected int id;   // currently only required for serialization and deserialization
    public void setId(int i) {
       id = i;
    }


    /**
     * Adds an edge to this node.
     *
     * @param edge The new edge which is connected to this node.
     */
    public void addEdge(AbstractEdge edge) {
        edges.add(edge);
    }


    /**
     * @return A list of all edges which are connected to this node.
     */
    public List<AbstractEdge> getEdges() {
        return edges;
    }


    /**
     * From a collection of nodes, get the one which is nearest to a given position.
     *
     * @param refPos The position for which to find the closes node.
     * @param nodeList The list of nodes from which to get the nearest one.
     * @return The node which is the nearest to this one.
     */
    public static AbstractNode getNearestNode(Point2D refPos, List<AbstractNode> nodeList) {
        double minDist = Double.MAX_VALUE;
        AbstractNode result = null;
        for (AbstractNode node: nodeList) {
            double dist = node.getPosition().distance(refPos);
            if (dist < minDist) {
                result = node;
                minDist = dist;
            }
        }

        return result;
    }

    
    /**
     * From a collection of nodes, get the one which is nearest to this one.
     *
     * @param nodeList The list of nodes from which to get the nearest one.
     * @return The node which is the nearest to this one.
     */
    public AbstractNode getNearestNode(List<AbstractNode> nodeList) {
        return getNearestNode(getPosition(), nodeList);
    }


    /**
     * @param startWith The start node.
     * @param aw The edge to disregard.
     * @return A list of nodes which are reachable from a given node, 
     *         without traversing the given edge.
     */
    public List<AbstractNode> getNodesWithout(final AirWire ignore) {
        Set<AbstractNode> result = new HashSet<>();
        Stack<AbstractNode> nodeStack = new Stack<>();

        // dumpNet();

        // start with the current node and add all destination nodes to the result set
        AbstractNode currentNode = this;
        while(currentNode != null) {
            List<AbstractEdge> edges = currentNode.getEdges();
            edges.remove(ignore);

            for(AbstractEdge edge : edges) {
                AbstractNode dest = edge.getOtherNode(currentNode);
                if (!result.contains(dest) ) {
                    result.add(dest);
                    nodeStack.push(dest);
                }
            }
            if (!nodeStack.empty()) {
                currentNode = nodeStack.pop();
            } else {
                currentNode = null;
            }
        }

        return result.stream().collect(Collectors.toList());
    }

    /**
     * @return The number of edges which are connected to this node.
     */
    public int getEdgeCount() {
        return edges.size(); // Starts.size() + traceEnds.size();
    }
}
