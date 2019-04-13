package afester.javafx.examples.board;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineJoin;

public class PartLine implements PartShape {

    private Double x1;
    private Double x2;
    private Double y1;
    private Double y2;
    private Double width;

    public PartLine(Double x1, Double y1, Double x2, Double y2, Double width) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.width = width;
    }

    @Override
    public Shape createNode() {
        Shape line = new Line(x1, y1, x2, y2);
        line.setStrokeWidth(width);
        line.setStroke(Color.GRAY);
        line.setStrokeLineJoin(StrokeLineJoin.ROUND);

        return line;
    }

    @Override
    public Node getXML(Document doc) {
        Element result = doc.createElement("line");
        
        result.setAttribute("x1", x1.toString());
        result.setAttribute("y1", y1.toString());
        result.setAttribute("x2", x2.toString());
        result.setAttribute("y2", y2.toString());
        result.setAttribute("width", width.toString());

        return result;
    }

}
