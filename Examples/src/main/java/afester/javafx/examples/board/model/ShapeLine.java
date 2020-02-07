package afester.javafx.examples.board.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;

public class ShapeLine implements ShapeModel {

    private Point2D p1;
    private Point2D p2;
    private Double width;

    public ShapeLine(Point2D p1, Point2D p2, Double width) {
        this.p1 = p1;
        this.p2 = p2;
        this.width = width;
    }
    
    public Point2D getStart() {
        return p1;
    }

    public Point2D getEnd() {
        return p2;
    }

    public Double getWidth() {
        return width;
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
    public final ShapeType getType() {
        return ShapeType.SHAPETYPE_LINE;
    }

    @Override
    public String toString() {
        return String.format("%s[%s %s width=%s]", 
                             ShapeLine.class.getName(), p1, p2, width);
    }
}
