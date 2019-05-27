package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class PartCircle implements PartShape {

    private Point2D center;
    private double radius;
    private double width;

    public PartCircle(Point2D center, double radius, double width) {
        this.center = center;
        this.radius = radius;
        this.width = width;
    }

    @Override
    public Shape createNode() {
        Shape circle = new Circle(center.getX(), center.getY(), radius);
        circle.setFill(null);
        circle.setStroke(Color.GRAY);
        circle.setStrokeWidth(width);

        return circle;
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

}
