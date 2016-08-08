package afester.javafx.examples.shapes;

import javafx.geometry.Point2D;

public class Line extends javafx.scene.shape.Line implements EditableShape {


    public Line() {
        super();
    }

    public Line(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
    }

    public Point2D[] getEditHandles() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("Line[x1=%s, y1=%s, x2=%s, y2=%s]", getStartX(), getStartY(), getEndX(), getEndY());
    }
}
