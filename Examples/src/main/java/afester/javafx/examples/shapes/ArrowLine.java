package afester.javafx.examples.shapes;

import javafx.geometry.Point2D;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class ArrowLine extends Path implements EditableShape {

    private double startX, startY, endX, endY;

    public ArrowLine(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        
        update();
    }
    
    private void update() {
        getElements().clear();

        double rot = Math.toDegrees(Math.atan2(endY - startY, endX - startX));
        Arrow start = new Arrow(startX, startY, rot + 270);
        Arrow end = new Arrow(endX, endY, rot + 90);

        MoveTo moveTo = new MoveTo(start.getConnX(), start.getConnY());
        LineTo lineTo = new LineTo(end.getConnX(), end.getConnY());

        getElements().addAll(moveTo, lineTo);

        getElements().addAll(start.getElements());
        getElements().addAll(end.getElements());

    }


    public void setEndX(double x) {
        this.endX = x;
        update();
    }

    public void setEndY(double y) {
        this.endY = y;
        update();
    }

    public Point2D[] getEditHandles() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("ArrowLine[x1=%s, y1=%s, x2=%s, y2=%s]", -1, -1, -1, -1);
    }
}
