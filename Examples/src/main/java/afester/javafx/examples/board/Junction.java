package afester.javafx.examples.board;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public class Junction extends PartShape {

    // The position of this junction 
    private double xpos;
    private double ypos;

    public List<Line> traceStarts = new ArrayList<>();
    public List<Line> traceEnds = new ArrayList<>();

    public Junction(double xpos, double ypos) {
        this.xpos = xpos;
        this.ypos = ypos;
    }

    public Junction(Point2D pos) {
        this.xpos = pos.getX();
        this.ypos = pos.getY();
    }

    public double getXpos() {
        return xpos;
    }

    public double getYpos() {
        return ypos;
    }

    public Point2D getPos() {
        return new Point2D(xpos, ypos);
    }

    @Override
    public String toString() {
        return String.format("Junction[pos=%s/%s]", xpos, ypos);  
    }

    public void addStart(Line wire) {
        traceStarts.add(wire);
    }

    public void addEnd(Line wire) {
        traceEnds.add(wire);
    }

    public void moveTraces2(double x, double y) {
        // TODO: This requires a reference to a real "Trace" object.
        // Depending on the Trace type, it might also require to move the other coordinates ....
        for (Line l : traceStarts) {
            l.setStartX(x);
            l.setStartY(y);
        }

        for (Line l : traceEnds) {
            l.setEndX(x);
            l.setEndY(y);
        }
        
    }

    @Override
    public Shape createNode() {
        throw new RuntimeException ("NYI");
    }

    @Override
    public Node getXML(Document doc) {
        Element result = doc.createElement("junction");
        result.setAttribute("x", Double.toString(getXpos()));
        result.setAttribute("y", Double.toString(getYpos()));
        result.setAttribute("id", Integer.toString(id));

        return result;
    }
    
    public int id;

    public void setId(int i) {
        id = i;
    }

}
