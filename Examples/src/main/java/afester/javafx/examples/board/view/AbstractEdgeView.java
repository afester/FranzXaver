package afester.javafx.examples.board.view;

import afester.javafx.examples.board.model.AbstractEdge;
import afester.javafx.examples.board.model.Net;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * A JavaFX shape to visualize the edge of a graph.
 * Note that the edge might consist of more than one shape, which might 
 * need to be added to different groups to allow a consistent visualization.
 * Hence the AbstraceEdgeView itself is NOT a Node - it is a collection of
 * Nodes which can be added to any other parent as required. 
 */
public abstract class AbstractEdgeView extends Line implements Interactable  {

    public AbstractEdge edge;

    // The start position of this edge.
    private final ObjectProperty<Point2D> start = new SimpleObjectProperty<Point2D>(new Point2D(0, 0));
    public ObjectProperty<Point2D> startProperty() { return start; }
    public Point2D getStart() { return start.get(); }
    public void setStart(Point2D pos) { start.set(pos); }

    // The end position of this edge.
    private final ObjectProperty<Point2D> end = new SimpleObjectProperty<Point2D>();
    public ObjectProperty<Point2D> endProperty() { return end; }
    public Point2D getEnd() { return end.get(); }
    public void setEnd(Point2D pos) { end.set(pos); }

//    // A flag to determine if this edge is visible or not.
//    private final BooleanProperty visible = new SimpleBooleanProperty(true);
//    public BooleanProperty visibleProperty() { return visible; }
//    public boolean isVisible() { return visible.get(); }
//    public void setVisible(boolean flag) { visible.set(flag); }

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
        start.bind(from.positionProperty());
        wire.fromProperty().addListener((obj, oldFrom, newFrom) -> {
            start.unbind();
            start.bind(newFrom.positionProperty());
        });

        // Handle the end position of the line
        end.bind(to.positionProperty());
        wire.toProperty().addListener((obj, oldTo, newTo) -> {
            end.unbind();
            end.bind(newTo.positionProperty());
        });

        // handle the hidden property of the edge 
        visibleProperty().bind(wire.isHiddenProperty().not());

        // TODO: We need a thicker selectionShape (a thicker transparent line) with the same coordinates
        // so that selecting the line is easier

        createContextMenu();
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
