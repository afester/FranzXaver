package afester.javafx.examples.board.view;

import afester.javafx.examples.board.Interactable;
import afester.javafx.examples.board.model.AbstractWire;
import afester.javafx.examples.board.model.Net;
import javafx.geometry.Point2D;
import javafx.scene.shape.Line;

/**
 * A JavaFX shape to visualize the edge of a graph.
 */
public abstract class AbstractEdgeView extends Line implements Interactable  {

    public AbstractWire edge;

    /**
     * Creates a new AbstractWireView for a given AbstractWire.
     *
     * @param wire The model object for the graph edge.
     */
    public AbstractEdgeView(AbstractWire wire) {
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


//    @Override
//    public void setSelected(boolean isSelected) {
//        TraceView t = (TraceView) obj;
//        Trace trace = (Trace) t.getTrace();
//        Net net = trace.getNet();
//        net.setSelected(trace);
//
//            //    	if (isSelected) {
////                        Net net = getNet();
////                        net.getTraces().forEach(e -> e.setSegmentSelected(isSelected));
//            
//                        //from.setSelected(true);
//                        //to.setSelected(true);
//                		//setStroke(Color.DARKGRAY);
//              //  	} else {
//                        //from.setSelected(false);
//                        //to.setSelected(false);
//                		//setStroke(Color.SILVER);
//                //	}
//    }

    protected void setSegmentSelected(boolean isSelected) {
//      if (isSelected) {
//        from.setSelected(true);
//        to.setSelected(true);
//        setStroke(Color.RED);
//      } else {
//        from.setSelected(false);
//        to.setSelected(false);
//        setStroke(Color.ORANGE);
//      }
    }

	@Override
	public Point2D getPos() {
		return new Point2D(getLayoutX(), getLayoutY());
	}

    @Override
    public String getRepr() {
        return "Net: "; //  + getNet().getName(); 
    }

//    
//    public AbstractNode getOtherNode(AbstractNode node) {
//        if (from == node) {
//            return to;
//        }
//        if (to == node) {
//            return from;
//        }
//
//        throw new RuntimeException("Unexpected: Edge does neither go FROM nor TO the given node!");
//    }
//


    @Override
    public void moveToGrid(Point2D clickPos) {
        System.err.println("MOVE " + this + " to " + clickPos);
    }

    @Override
    public void setSelected(boolean isSelected) {
        Net net = edge.getNet();
        net.setSelected(isSelected, edge);
    }

}
