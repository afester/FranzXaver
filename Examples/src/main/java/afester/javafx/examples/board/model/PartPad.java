package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;


public class PartPad implements PartShape {

    private String name;
    private Point2D pos;    // local position!

    public PartPad(String name, Point2D pos) {
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
    public Shape createNode() {
        return null;    // TODO ...
//        Shape line = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
//        line.setStrokeWidth(width);
//        return line;
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
    public String toString() {
        return String.format("PartPad[name=\"%s\" pos=%s]", name, pos);
    }
}
