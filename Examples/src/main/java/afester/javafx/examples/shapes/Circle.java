package afester.javafx.examples.shapes;

import javafx.geometry.Point2D;
import javafx.scene.paint.Paint;

public class Circle extends javafx.scene.shape.Circle implements EditableShape {

    public Circle() {
        super();
    }

    public Circle(double centerX, double centerY, double radius, Paint fill) {
        super(centerX, centerY, radius, fill);
    }

    public Circle(double centerX, double centerY, double radius) {
        super(centerX, centerY, radius);
    }

    public Circle(double radius, Paint fill) {
        super(radius, fill);
    }

    public Circle(double radius) {
        super(radius);
    }

    public Point2D[] getEditHandles() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("Circle[pos=(%s, %s), radius=%s]", getCenterX(), getCenterY(), getRadius());
    }
}
