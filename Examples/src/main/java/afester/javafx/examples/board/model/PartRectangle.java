package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;

public class PartRectangle implements PartShape {

    private Point2D p1;
    private Point2D p2;

    public PartRectangle(Point2D p1, Point2D p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public Shape createNode() {
        final double width = Math.abs(p2.getX() - p1.getX());
        final double height = Math.abs(p2.getY() - p1.getY());
        final double x = Math.min(p1.getX(), p2.getX());
        final double y = Math.min(p1.getY(), p2.getY());

        Shape rect = new Rectangle(x, y, width, height);
        rect.setFill(Color.GRAY);
        rect.setStroke(null);
        rect.setStrokeLineCap(StrokeLineCap.ROUND);

        return rect;
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

}
