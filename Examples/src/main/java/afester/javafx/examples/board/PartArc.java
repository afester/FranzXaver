package afester.javafx.examples.board;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class PartArc implements PartShape {

    private Point2D center;
    private Double radius;
    private Double angle;
    private Double startAngle;
    private Double width;
    private Color color;
    

    public PartArc(Point2D center, Double radius, Double startAngle, Double angle, Double width, Color color) {
        this.center = center;
        this.radius = radius;
        this.startAngle = startAngle;
        this.angle = angle;
        this.width = width;
        this.color = color;
    }


    @Override
    public Shape createNode() {
        Arc arc = new Arc(center.getX(), center.getY(), radius, radius, startAngle, angle);
        arc.setType(ArcType.OPEN);
        arc.setFill(null);
        arc.setStrokeWidth(width);
        arc.setStroke(color); // Color.GRAY);
        arc.setStrokeLineCap(StrokeLineCap.ROUND);
        return arc;
    }

    @Override
    public Node getXML(Document doc) {
        Element result = doc.createElement("arc");

        result.setAttribute("cx", Double.toString(center.getX()));
        result.setAttribute("cy", Double.toString(center.getY()));
        result.setAttribute("radius", Double.toString(radius));
        result.setAttribute("start", Double.toString(180.0f));
        result.setAttribute("end", Double.toString(angle));
        result.setAttribute("width", width.toString());

        return result;
    }

}
