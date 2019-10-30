package afester.javafx.examples.controls;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

class RectangleObject extends Rectangle {

    public RectangleObject(String id, Color col, Point2D pos, int width, int height) {
        super(width, height, col);
        setLayoutX(pos.getX());
        setLayoutY(pos.getY());
        setId(id);
    }

    @Override
    public String toString() {
        return String.format("RectangleObject[%s]", getId());
    }
}