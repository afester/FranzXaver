package afester.javafx.examples.board;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * A Pad is a junction which refers to a specific pin of a Part.
 */
public class Pad extends Junction {

    private final Part part;
    private final String pinNumber;

    /**
     * Creates a new Pad.
     *
     * @param part	  The part to which this pad is attached
     * @param pinNumber The (physical) pin number of this pad
     * @param xpos    The x position of the Pad
     * @param ypos    The y position of the Pad
     */
    public Pad(Part part, String pinNumber, double xpos, double ypos) {
        super(xpos, ypos);

        this.part = part;
        this.pinNumber = pinNumber;
    }

    public Part getPart() {
        return part;
    }

    public String getPinNumber() {
        return pinNumber;
    }

    @Override
    public Point2D getPos() {
        return part.localToParent(getCenterX(), getCenterY());
    }

    @Override
    public String toString() {
        return String.format("Pad[part=\"%s\", padName=%s, pos=%s]", 
        					 part.getName(), pinNumber, /*pin + "@" + gate,*/ getPos());  
    }

    @Override
    public Node createNode() {
    	Group result = new Group();

        Shape pad = new Circle(getCenterX(), getCenterY(), 0.7); // drill*2);
        pad.setFill(Color.WHITE);
        pad.setStroke(Color.BLACK);
        pad.setStrokeWidth(0.6);

        Text padName = new Text(getCenterX(), getCenterY(), this.pinNumber);

    	// TODO: The rendered text is messed up if the size is too small!
        // A possible solution seems to be to keep the text size larger and 
        // apply an appropriate scaling (and translation) to the Text node
        Font theFont = Font.font("Courier", 10.0);
        padName.setScaleX(0.1);
        padName.setScaleY(0.1);
        padName.setTranslateX(-3);
        padName.setFont(theFont);
        padName.setFill(Color.RED);
        padName.setTextOrigin(VPos.CENTER);

        result.getChildren().addAll(pad, padName);
        return result;
    }

    @Override
    public org.w3c.dom.Node getXML(Document doc) {
        Element result = doc.createElement("pad");
        result.setAttribute("x", Double.toString(getCenterX()));
        result.setAttribute("y", Double.toString(getCenterY()));
        result.setAttribute("pinNumber", pinNumber);
        result.setAttribute("id", Integer.toString(id));

        return result;
    }

    /**
     * Returns a unique pad id, consisting of the part number and the pin number
     *
     * @return A board-unique pad id in the form "partName$pinNumber"
     */
    public String getPadId() {
        return part.getName() + "$" + pinNumber;
    }
 }
