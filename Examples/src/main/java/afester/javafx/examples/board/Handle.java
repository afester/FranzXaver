package afester.javafx.examples.board;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Handle extends Circle implements Interactable {

    public Handle(Point2D pos, double radius) {
        super(pos.getX(), pos.getY(), radius);
        setFill(Color.GREEN);
    }

    
    @Override
    public String toString() {
        return String.format("Handle[pos=%s/%s]", getCenterX(), getCenterY());  
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
}
