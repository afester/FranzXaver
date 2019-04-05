package afester.javafx.examples.board;

import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SelectionShape extends Rectangle {

    public SelectionShape(Bounds bounds) {
        super(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
        setFill(Color.WHITE);
        setOpacity(0);
        //setMouseTransparent(false);
        // setOnMousePressed(e -> System.err.println(e));
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
}
