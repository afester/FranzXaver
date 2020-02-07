package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;
import javafx.scene.text.FontWeight;


public class PartText implements PartShape {

    private final Point2D pos;
    private final Double size;          // text height in mm
    private final FontWeight weight;    // text weight
    private final StringBuffer text;

    public PartText(Point2D textPos, Double size, FontWeight weight) {
        this.pos = textPos;
        this.text = new StringBuffer();
        this.size = size;
        this.weight = weight;
    }

    public void append(String s) {
        text.append(s);
    }

    public Point2D getPos() {
        return pos;
    }

    public Double getSize() {
        return size;
    }

    public FontWeight getWeight() {
        return weight;
    }

    public String getText() {
        return text.toString();
    }

    @Override
    public Node getXML(Document doc) {
        Element result = doc.createElement("text");
        result.setAttribute("x", Double.toString(pos.getX()));
        result.setAttribute("y", Double.toString(pos.getY()));
        result.setAttribute("size", size.toString());
        result.setAttribute("weight", weight.toString());
        result.setTextContent(text.toString());

        return result;
    }


    @Override
    public final ShapeType getType() {
        return ShapeType.SHAPETYPE_TEXT;
    }
    
    @Override
    public String toString() {
        return String.format("%s[pos=%s, size=%s, text=\"%s\"]", 
                             PartText.class.getName(), pos, size, text);
    }

}
