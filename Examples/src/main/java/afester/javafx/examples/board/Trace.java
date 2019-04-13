package afester.javafx.examples.board;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

/**
 * A Trace is a part of a Net which has already been routed. 
 */
public class Trace extends Line implements Interactable {
    private Junction from;
    private Junction to;

    public Trace(Junction from, Junction to) {
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


    public Junction getFrom() {
        return from;
    }

    public Junction getTo() {
        return to;
    }


    public void setFrom(Junction newJunction) {
        from = newJunction;
        setStart(from.getPos());
    }

    public void setTo(Junction newJunction) {
        to = newJunction;
        setEnd(to.getPos());
    }

    @Override
    public String toString() {
        return String.format("Trace[%s - %s]", this.getStart(), this.getEnd());
    }

    public Node getXML(Document doc) {
        Element traceNode = doc.createElement("trace");
        traceNode.setAttribute("from", Integer.toString(getFrom().id));
        traceNode.setAttribute("to",   Integer.toString(getTo().id));

        return traceNode;
    }

    // TODO: currently each trace has its own context menu instance!
    private ContextMenu contextMenu;
    private void createContextMenu() {
        contextMenu = new ContextMenu();
    	MenuItem item1 = new MenuItem("Delete");
    	item1.setOnAction(new EventHandler<ActionEvent>() {
    	    public void handle(ActionEvent e) {
    	        System.out.println("Delete " + Trace.this);
    	    }
    	});
    	contextMenu.getItems().addAll(item1);
    }


    @Override
    public void leftMouseAction(MouseEvent e, BoardView bv) {
//        Net net = (Net) getParent(); // TODO: provide an explicit access path
//        System.err.println("Clicked Trace of " + net);

    	Interactable currentSelection = bv.getSelectedObject();
        if (currentSelection != this) {
            if (currentSelection != null) {
                currentSelection.setSelected(false);
            }
            setSelected(true);
            bv.setSelectedObject(this);
        }
    }

    @Override
    public void rightMouseAction(MouseEvent e) {
    	contextMenu.show(this, e.getScreenX(), e.getScreenY());
    }


    @Override
    public void setSelected(boolean isSelected) {
    	if (isSelected) {
            from.setSelected(true);
            to.setSelected(true);
    		setStroke(Color.DARKGRAY);
    	} else {
            from.setSelected(false);
            to.setSelected(false);
    		setStroke(Color.SILVER);
    	}
    }

    @Override
    public void mouseDragged(MouseEvent e, BoardView bv, Point2D offset) {
        // TODO Auto-generated method stub
        
    }
}
