package afester.javafx.examples.board.view;

import afester.javafx.examples.board.model.AbstractEdge;
import afester.javafx.examples.board.model.Net;
import javafx.beans.value.ChangeListener;
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

        // Set initial wire positions - pad connections will be corrected later!
        final var from = wire.getFrom();
        final var to = wire.getTo();

        // Handle the start position of the line
        ChangeListener<Point2D> startListener = (obj, oldPos, newPos) -> setStart(newPos);
        from.positionProperty().addListener(startListener);
        setStart(from.getPosition());
        wire.fromProperty().addListener((obj, oldFrom, newFrom) -> {
            oldFrom.positionProperty().removeListener(startListener);
            newFrom.positionProperty().addListener(startListener);
            setStart(newFrom.getPosition());
        });

        // Handle the end position of the line
        ChangeListener<Point2D> endListener = (obj, oldPos, newPos) -> setEnd(newPos);
        to.positionProperty().addListener(endListener);
        setEnd(to.getPosition());
        wire.toProperty().addListener((obj, oldTo, newTo) -> {
            oldTo.positionProperty().removeListener(endListener);
            newTo.positionProperty().addListener(endListener);
            setEnd(newTo.getPosition());
        });

        // handle the hidden property of the edge 
        visibleProperty().bind(wire.isHiddenProperty().not());

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
