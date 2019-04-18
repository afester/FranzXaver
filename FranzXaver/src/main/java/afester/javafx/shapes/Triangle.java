package afester.javafx.shapes;

import javafx.geometry.Point2D;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class Triangle extends Path implements EditableShape {

    public Triangle(double x1, double y1, double x2, double y2, double x3, double y3) {
        MoveTo start = new MoveTo(x1, y1);
        LineTo first = new LineTo(x2, y2);
        LineTo second = new LineTo(x3, y3);
        ClosePath third = new ClosePath();

        getElements().addAll(start, first, second, third);
    }


    public Point2D[] getEditHandles() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("Triangle[%s]", getElements());
    }
}
