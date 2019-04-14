package afester.javafx.examples.board;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public class Junction extends Circle implements PartShape, Interactable {
    public List<Line> traceStarts = new ArrayList<>();
    public List<Line> traceEnds = new ArrayList<>();

    public Junction(double xpos, double ypos) {
    	super(xpos, ypos, 0.5);
    	setFill(null);
    }

    public Junction(Point2D pos) {
    	this(pos.getX(), pos.getY());
    }

    @Override
	public Point2D getPos() {
        return new Point2D(getCenterX(), getCenterY());
    }

    @Override
    public String toString() {
        return String.format("Junction[pos=%s/%s]", getCenterX(), getCenterY());  
    }

    public void addStart(Line wire) {
        traceStarts.add(wire);
    }

    public void addEnd(Line wire) {
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
    public Shape createNode() {
        throw new RuntimeException ("NYI");
    }

    @Override
    public Node getXML(Document doc) {
        Element result = doc.createElement("junction");
        result.setAttribute("x", Double.toString(getCenterX()));
        result.setAttribute("y", Double.toString(getCenterY()));
        result.setAttribute("id", Integer.toString(id));

        return result;
    }
    
    public int id;

    public void setId(int i) {
        id = i;
    }

	@Override
	public void setSelected(boolean isSelected) {
		if (isSelected) {
			setFill(Color.RED);
		} else {
			setFill(null);
		}
	}

	// TODO: Duplicate code in Part class!
    private Point2D snapToGrid(double x, double y, BoardView bv, Point2D offset) {                                                      
        // final double grid = 2.54;                                                                      
        final double grid = 1.27;       // for now, we also allow positions between pads - this is        
                                        // required to properly position the Eagle parts ...              

        double xpos = offset.getX() + x;                                                                        
        double ypos = offset.getY() + y;                                                                        

        xpos = (int) ( (xpos - bv.getPadOffset().getX()) / grid);                                         
        ypos = (int) ( (ypos - bv.getPadOffset().getY()) / grid);                                         

        xpos = xpos * grid + bv.getPadOffset().getX();                                                    
        ypos = ypos * grid + bv.getPadOffset().getY();                                                    

        return new Point2D(xpos, ypos);                                                                   
    }

	@Override
	public void mouseDragged(MouseEvent e, BoardView bv, Point2D offset) {
        Point2D snapPos = snapToGrid(e.getX(), e.getY(), bv, offset);
        setCenterX(snapPos.getX());
        setCenterY(snapPos.getY());
        moveTraces2(snapPos.getX(), snapPos.getY());
	}

	@Override
	public void leftMouseAction(MouseEvent e, BoardView bv) {
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
        throw new RuntimeException ("NYI");
	}
}
