package afester.javafx.examples.board.view;

import afester.javafx.examples.board.AirWireHandle;
import afester.javafx.examples.board.Interactable;
import afester.javafx.examples.board.model.AbstractEdge;
import afester.javafx.examples.board.model.Net;
import javafx.geometry.Point2D;
import javafx.scene.shape.Line;

/**
 * A JavaFX shape to visualize the edge of a graph.
 */
public abstract class AbstractEdgeView extends Line implements Interactable  {

    public AbstractEdge edge;

    /**
     * Creates a new AbstractWireView for a given AbstractWire.
     *
     * @param wire The model object for the graph edge.
     */
    public AbstractEdgeView(AbstractEdge wire) {
        this.edge = wire;

        // Remember: the change listener is REALLY only called when the value CHANGES (i.e. is not equals() to the old value)
        wire.startPointProperty().addListener((obj, oldValue, newValue) -> {
            System.err.println("New start point:" + newValue);
            setStart(newValue);
        });
        wire.endPointProperty().addListener((obj, oldValue, newValue) -> {
            System.err.println("New end point:" + newValue);
            setEnd(newValue);
        });

        // Set initial wire positions - pad connections will be corrected later!
        setStart(wire.getStart());
        setEnd(wire.getEnd());
        wire.startPointProperty().addListener((obj, oldPos, newPos) -> setStart(newPos));
        wire.endPointProperty().addListener((obj, oldPos, newPos) -> setEnd(newPos));

        // TODO: We need a thicker selectionShape (a thicker transparent line) with the same coordinates
        // so that selecting the line is easier

        createContextMenu();
    }

    /**
     * @return The start point of this edge as a Point2D object.
     */
    public Point2D getStart() {
        return new Point2D(getStartX(), getStartY());
    }

    /**
     * Sets the start point of this edge from a Point2D object.
     *
     * @param p The new start point.
     */
    public void setStart(Point2D p) {
        setStartX(p.getX());
        setStartY(p.getY());
    }

    /**
     * @return The end point of this edge as a Point2D object.
     */
    public Point2D getEnd() {
        return new Point2D(getEndX(), getEndY());
    }

    /**
     * Sets the end point of this edge from a Point2D object.
     *
     * @param p The new end point.
     */
    public void setEnd(Point2D p) {
        setEndX(p.getX());
        setEndY(p.getY());
    }


    // TODO: currently each trace has its own context menu instance!
//    private ContextMenu contextMenu;
    private void createContextMenu() {
//        contextMenu = new ContextMenu();
//    	MenuItem item1 = new MenuItem("Delete");
//    	item1.setOnAction(e -> {
//    	        System.out.println("Delete " + AbstractWire.this);
//    	});
//    	contextMenu.getItems().addAll(item1);
    }

//	@Override
//	public Point2D getPos() {
//		return new Point2D(getLayoutX(), getLayoutY());
//	}

    @Override
    public String getRepr() {
        return String.format("Net: %s", edge.getNet().getName());
    }

    @Override
    public void drag(BoardView bv, Point2D clickPos) {
        System.err.println("MOVE " + this + " to " + clickPos);
    }

    @Override
    public void setSelected(BoardView bv, boolean isSelected) {
        Net net = edge.getNet();
        net.setSelected(isSelected, edge);

        if (isSelected) {
            FromHandle h1 = new FromHandle(this);
            ToHandle h2 = new ToHandle(this);
            bv.getHandleGroup().getChildren().add(h1);
            bv.getHandleGroup().getChildren().add(h2);
        } else {
            // TODO: Hack!
            bv.getHandleGroup().getChildren().forEach(e -> ((AirWireHandle) e).disconnectListener());

            bv.getHandleGroup().getChildren().clear();
        }
    }
}
