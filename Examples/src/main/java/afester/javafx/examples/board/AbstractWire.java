package afester.javafx.examples.board;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

/**
 * An AbstractWire is the basic edge in a net graph. It connects exactly two junctions.
 */
public abstract class AbstractWire extends Line implements Interactable {
    protected AbstractNode from;
    protected AbstractNode to;

    public AbstractWire(AbstractNode from, AbstractNode to) {
        this.from = from;
        this.to = to;

        setStart(from.getPos());
        setEnd(to.getPos());

        // TODO: We need a thicker selectionShape (a thicker transparent line) with the same coordinates
        // so that selecting the line is easier
        setStrokeWidth(1.0); // 0.2);
        setStroke(Color.SILVER);
        setStrokeLineCap(StrokeLineCap.ROUND);
        from.addStart(this);
        to.addEnd(this);
        
        createContextMenu();
    }

    public Point2D getStart() {
        return new Point2D(getStartX(), getStartY());
    }

    public void setStart(Point2D p) {
        setStartX(p.getX());
        setStartY(p.getY());
    }

    public Point2D getEnd() {
        return new Point2D(getEndX(), getEndY());
    }

    public void setEnd(Point2D p) {
        setEndX(p.getX());
        setEndY(p.getY());
    }

    public AbstractNode getFrom() {
        return from;
    }

    public AbstractNode getTo() {
        return to;
    }

    public void setFrom(AbstractNode newJunction) {
        from = newJunction;
        setStart(from.getPos());
    }

    public void setTo(AbstractNode newJunction) {
        to = newJunction;
        setEnd(to.getPos());
    }

    public abstract Node getXML(Document doc);

    // TODO: currently each trace has its own context menu instance!
    private ContextMenu contextMenu;
    private void createContextMenu() {
        contextMenu = new ContextMenu();
    	MenuItem item1 = new MenuItem("Delete");
    	item1.setOnAction(e -> {
    	        System.out.println("Delete " + AbstractWire.this);
    	});
    	contextMenu.getItems().addAll(item1);
    }


    @Override
    public void setSelected(boolean isSelected) {
//    	if (isSelected) {
            Net net = getNet();
            net.getTraces().forEach(e -> e.setSegmentSelected(isSelected));

            //from.setSelected(true);
            //to.setSelected(true);
    		//setStroke(Color.DARKGRAY);
  //  	} else {
            //from.setSelected(false);
            //to.setSelected(false);
    		//setStroke(Color.SILVER);
    //	}
    }

    protected void setSegmentSelected(boolean isSelected) {
      if (isSelected) {
        //from.setSelected(true);
        //to.setSelected(true);
        setStroke(Color.RED);
      } else {
        //from.setSelected(false);
        //to.setSelected(false);
        setStroke(Color.ORANGE);
      }
    }

	@Override
	public Point2D getPos() {
		return new Point2D(getLayoutX(), getLayoutY());
	}

    @Override
    public String getRepr() {
        return "Net: " + getNet().getName(); 
    }

    /**
     * @return The Net this AbstractWire is part of.
     */
    public Net getNet() {
        Net net = (Net) getParent().getParent();
        return net; 
    }
}
