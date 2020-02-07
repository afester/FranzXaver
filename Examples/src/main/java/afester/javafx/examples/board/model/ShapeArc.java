package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;


public class ShapeArc implements ShapeModel {

    private Point2D center;
    private Double radius;
    private Double angle;
    private Double startAngle;
    private Double width;

    public ShapeArc(Point2D center, Double radius, Double startAngle, Double angle, Double width) {
        this.center = center;
        this.radius = radius;
        this.startAngle = startAngle;
        this.angle = angle;
        this.width = width;
    }

    public Point2D getCenter() {
        return center;
    }

    public Double getRadius() {
        return radius;
    }

    public Double getAngle() {
        return angle;
    }

    public Double getStartAngle() {
        return startAngle;
    }

    public Double getWidth() {
        return width;
    }

    @Override
    public Node getXML(Document doc) {
        Element result = doc.createElement("arc");

        result.setAttribute("cx", Double.toString(center.getX()));
        result.setAttribute("cy", Double.toString(center.getY()));
        result.setAttribute("radius", Double.toString(radius));
        result.setAttribute("start", Double.toString(startAngle));
        result.setAttribute("angle", Double.toString(angle));
        result.setAttribute("width", width.toString());

        return result;
    }

    @Override
    public final ShapeType getType() {
        return ShapeType.SHAPETYPE_ARC;
    }

    @Override
    public String toString() {
        return String.format("%s[center=%s radius=%s start=%s angle=%s]", 
                             ShapeArc.class.getName(), center, radius, startAngle, angle); 
    }

}
