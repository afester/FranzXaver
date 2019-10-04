package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;

public class PartLine implements PartShape {

    private Point2D p1;
    private Point2D p2;
    private Double width;

    public PartLine(Point2D p1, Point2D p2, Double width) {
        this.p1 = p1;
        this.p2 = p2;
        this.width = width;
    }

    @Override
    public Shape createNode() {
        Shape line = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        line.setStrokeWidth(width);
        return line;
    }

    @Override
    public Node getXML(Document doc) {
        Element result = doc.createElement("line");
        
        result.setAttribute("x1", Double.toString(p1.getX()));
        result.setAttribute("y1", Double.toString(p1.getY()));
        result.setAttribute("x2", Double.toString(p2.getX()));
        result.setAttribute("y2", Double.toString(p2.getY()));
        result.setAttribute("width", width.toString());

        return result;
    }

    @Override
    public String toString() {
        return String.format("PartLine[%s %s width=%s]", p1, p2, width);
    }
}
