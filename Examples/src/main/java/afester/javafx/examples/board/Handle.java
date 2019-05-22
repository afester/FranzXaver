package afester.javafx.examples.board;

import org.w3c.dom.Document;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public class Handle extends AbstractNode { // Circle implements Interactable {

    public Handle(Point2D pos, double radius) {
        super(pos); // pos.getX(), pos.getY(), radius);
        setFill(Color.GREEN);
    }


    @Override
    public Point2D getPos() {
        return new Point2D(getCenterX(), getCenterY());
    }


    @Override
    public void setSelected(boolean isSelected) {
    }


    @Override
    public String getRepr() {
        return "";
    }


    
    @Override
    public String toString() {
        return String.format("Handle[pos=%s/%s]", getCenterX(), getCenterY());  
    }


    @Override
    public Node createNode() {
        throw new RuntimeException("NYI");
    }


    @Override
    public org.w3c.dom.Node getXML(Document doc) {
        throw new RuntimeException("NYI");
    }
}
