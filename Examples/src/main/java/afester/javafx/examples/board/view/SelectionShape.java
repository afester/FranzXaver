package afester.javafx.examples.board.view;

import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;

public class SelectionShape extends Rectangle {

    public SelectionShape(Bounds bounds) {
        super(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());

        setFill(null);
        setStroke(null);
        setMouseTransparent(true);      // this shape must not react to mouse events
        getStrokeDashArray().addAll(1.0, 1.0);
    }

//    public void setSelected(boolean isSelected) {
//        if (isSelected) {
//            setStroke(Color.DARKGRAY);
//            getStrokeDashArray().addAll(0.5, 0.5);
//            setStrokeWidth(0.3);
//
//            setFill(null);
//            setOpacity(1);
//        } else {
//            setStroke(null);
//            setFill(Color.WHITE);
//            setOpacity(0.01);
//        }
//    }
    
    
    @Override
    public String toString() {
        return String.format("SelectionShape[x=%s, y=%s, width=%s, height=%s]", getX(), getY(), getWidth(), getHeight());  
    }
}
