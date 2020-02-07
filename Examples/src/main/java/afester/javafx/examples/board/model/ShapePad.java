package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;


public class ShapePad implements ShapeModel {

    private String name;
    private Point2D pos;    // local position!

    public ShapePad(String name, Point2D pos) {
        this.name = name;
        this.pos = pos;
    }

    
    public String getName() {
        return name;
    }


    public Point2D getPos() {
        return pos;
    }

    @Override
    public Node getXML(Document doc) {
        Element result = doc.createElement("pad");
        result.setAttribute("x", Double.toString(pos.getX()));
        result.setAttribute("y", Double.toString(pos.getY()));
        result.setAttribute("padName", name);
//        result.setAttribute("id", Integer.toString(id));  // TODO

        return result;
    }


    @Override
    public final ShapeType getType() {
        return ShapeType.SHAPETYPE_PAD;
    }

    @Override
    public String toString() {
        return String.format("%s[name=\"%s\" pos=%s]", 
                             ShapePad.class.getName(), name, pos);
    }
}
