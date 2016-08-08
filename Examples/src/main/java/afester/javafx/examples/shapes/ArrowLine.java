package afester.javafx.examples.shapes;

import javafx.geometry.Point2D;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class ArrowLine extends Path implements EditableShape {


    public ArrowLine(double startX, double startY, double endX, double endY) {
        Arrow start = new Arrow(startX, startY, 90);
        Arrow end = new Arrow(endX, endY, 270);

        MoveTo moveTo = new MoveTo(startX, startY);
        LineTo lineTo = new LineTo(endX, endY);

        getElements().addAll(moveTo, lineTo);
        getElements().addAll(start.getElements());
        getElements().addAll(end.getElements());
    }

    public Point2D[] getEditHandles() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("ArrowLine[x1=%s, y1=%s, x2=%s, y2=%s]", -1, -1, -1, -1);
    }
}
