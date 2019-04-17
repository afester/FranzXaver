package afester.javafx.examples.board;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineJoin;

public class PartArc implements PartShape {

    private Point2D center;
    private Double radius;
    private Double angle;
    private Double width;

    public PartArc(Point2D center, Double radius, Double angle, Double width) {
        this.center = center;
        this.radius = radius;
        this.angle = angle;
        this.width = width;
    }


    @Override
    public Shape createNode() {
        Arc arc = new Arc(center.getX(), center.getY(), radius, radius, angle, 180.0f);
        arc.setType(ArcType.ROUND);
        arc.setStrokeWidth(width);
        arc.setStroke(Color.GRAY);
        arc.setStrokeLineJoin(StrokeLineJoin.ROUND);
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
