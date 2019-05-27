package afester.javafx.examples.board;

import afester.javafx.examples.board.model.Net;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Handle extends Circle implements Interactable {

    public Handle(Net net, Point2D pos, double radius) {
        //super(net, pos); // pos.getX(), pos.getY(), radius);
        super(pos.getX(), pos.getY(), radius);
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

//
//    @Override
//    public Node createNode() {
//        throw new RuntimeException("NYI");
//    }
//
//
//    @Override
//    public org.w3c.dom.Node getXML(Document doc) {
//        throw new RuntimeException("NYI");
//    }
}
