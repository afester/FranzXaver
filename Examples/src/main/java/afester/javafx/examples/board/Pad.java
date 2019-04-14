package afester.javafx.examples.board;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 * A Pad is a junction which refers to a specific pin of a Part.
 */
public class Pad extends Junction {

    private Part part;
    private String padName;

//    private String gate;
//    private String pin;

    public Pad(Part part, String padName, /*String gate, String pin, */double xpos, double ypos) {
        super(xpos, ypos);

        this.part = part;
        this.padName = padName;

//        this.gate = gate;
//        this.pin = pin;
    }

    public Part getPart() {
        return part;
    }


    @Override
    public Point2D getPos() {
        return part.localToParent(getCenterX(), getCenterY());
    }

    @Override
    public String toString() {
        return String.format("Pad[part=\"%s\", padName=%s, pos=%s]", 
        					 part.getName(), padName, /*pin + "@" + gate,*/ getPos());  
    }

    @Override
    public Shape createNode() {
        Shape pad = new Circle(getCenterX(), getCenterY(), 0.7); // drill*2);
        pad.setFill(Color.WHITE);
        pad.setStroke(Color.BLACK);
        pad.setStrokeWidth(0.6);
        return pad;
    }

    @Override
    public Node getXML(Document doc) {
        Element result = doc.createElement("pad");
        result.setAttribute("x", Double.toString(getCenterX()));
        result.setAttribute("y", Double.toString(getCenterY()));
        result.setAttribute("id", Integer.toString(id));

        return result;
    }
    
 }
