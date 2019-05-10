package afester.javafx.examples.board;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public abstract class AbstractNode extends Circle implements PartShape, Interactable {
    public List<AbstractWire> traceStarts = new ArrayList<>();
    public List<AbstractWire> traceEnds = new ArrayList<>();

    protected int id;	// currently only required for serialization and deserialization

    public AbstractNode(double xpos, double ypos) {
    	super(xpos, ypos, 0.5);
    	setFill(null);
    }

    public AbstractNode(Point2D pos) {
    	this(pos.getX(), pos.getY());
    }

    public void setId(int i) {
       id = i;
    }

    @Override
	public Point2D getPos() {
        return new Point2D(getCenterX(), getCenterY());
    }

    public void addStart(AbstractWire wire) {
        traceStarts.add(wire);
    }

    public void addEnd(AbstractWire wire) {
        traceEnds.add(wire);
    }

    public void moveTraces2(double x, double y) {
        // TODO: This requires a reference to a real "Trace" object.
        // Depending on the Trace type, it might also require to move the other coordinates ....
        for (Line l : traceStarts) {
            l.setStartX(x);
            l.setStartY(y);
        }

        for (Line l : traceEnds) {
            l.setEndX(x);
            l.setEndY(y);
        }
        
    }

    @Override
    public Node createNode() {
        throw new RuntimeException ("NYI");
    }

	@Override
	public void setSelected(boolean isSelected) {
		if (isSelected) {
			setFill(Color.DARKRED);
		} else {
			setFill(null);
		}
	}


    public void setPos(Point2D snapPos) {
        setCenterX(snapPos.getX());
        setCenterY(snapPos.getY());
        moveTraces2(snapPos.getX(), snapPos.getY());
    }
}
