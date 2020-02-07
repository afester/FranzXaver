package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;


public class PartCircle implements PartShape {

    private Point2D center;
    private double radius;
    private double width;

    public PartCircle(Point2D center, double radius, double width) {
        this.center = center;
        this.radius = radius;
        this.width = width;
    }

    public Point2D getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    public double getWidth() {
        return width;
    }

    @Override
    public Node getXML(Document doc) {
        Element result = doc.createElement("circle");
        
        result.setAttribute("x", Double.toString(center.getX()));
        result.setAttribute("y", Double.toString(center.getY()));
        result.setAttribute("radius", Double.toString(radius));
        result.setAttribute("width", Double.toString(width));

        return result;
    }


    @Override
    public final ShapeType getType() {
        return ShapeType.SHAPETYPE_CIRCLE;
    }

    @Override
    public String toString() {
        return String.format("%s[center=%s radius=%s width=%s]", 
                             PartCircle.class.getName(), center, radius, width);
    }
}
