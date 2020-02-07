package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;


public class PartRectangle implements PartShape {

    private Point2D p1;
    private Point2D p2;

    public PartRectangle(Point2D p1, Point2D p2) {
        this.p1 = p1;
        this.p2 = p2;
    }


    public Point2D getP1() {
        return p1;
    }


    public Point2D getP2() {
        return p2;
    }


    @Override
    public Node getXML(Document doc) {
        Element result = doc.createElement("rectangle");
        
        result.setAttribute("x1", Double.toString(p1.getX()));
        result.setAttribute("y1", Double.toString(p1.getY()));
        result.setAttribute("x2", Double.toString(p2.getX()));
        result.setAttribute("y2", Double.toString(p2.getY()));

        return result;
    }


    @Override
    public final ShapeType getType() {
        return ShapeType.SHAPETYPE_RECTANGLE;
    }

    @Override
    public String toString() {
        return String.format("%s[%s %s]", 
                             PartRectangle.class.getName(), p1, p2);
    }
}
