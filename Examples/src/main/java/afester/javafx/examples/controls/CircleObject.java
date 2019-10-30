package afester.javafx.examples.controls;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

// TODO: move to the shapes package?
class CircleObject extends Circle {

    public CircleObject(String id, Color col, Point2D center, double radius) {
        super(center.getX(), center.getY(), radius);
        setId(id);
        setFill(null);
        setStroke(col);
    }

    @Override
    public String toString() {
        return String.format("CircleObject[%s]", getId());
    }
}