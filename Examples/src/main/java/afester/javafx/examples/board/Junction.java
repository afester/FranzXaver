package afester.javafx.examples.board;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.geometry.Point2D;

public class Junction extends AbstractNode {

    public Junction(double xpos, double ypos) {
    	super(xpos, ypos);
    	setFill(null);
    }

    public Junction(Point2D pos) {
    	this(pos.getX(), pos.getY());
    }
    


    @Override
    public org.w3c.dom.Node getXML(Document doc) {
        Element result = doc.createElement("junction");
        result.setAttribute("x", Double.toString(getCenterX()));
        result.setAttribute("y", Double.toString(getCenterY()));
        result.setAttribute("id", Integer.toString(id));

        return result;
    }

    

    @Override
    public String getRepr() {
        return "Junction";
    }

    @Override
    public String toString() {
        return String.format("Junction[pos=%s/%s]", getCenterX(), getCenterY());  
    }

}
