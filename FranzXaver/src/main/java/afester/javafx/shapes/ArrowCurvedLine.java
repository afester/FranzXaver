package afester.javafx.shapes;

import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.shape.CubicCurveTo;

public class ArrowCurvedLine extends ArrowLine  {

    private CubicCurveTo curveTo = new CubicCurveTo();
    private Orientation orientation;

    public ArrowCurvedLine(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;

        update();
    }
    private static int count = 0;

    private void update() {
        System.err.printf("update(%s, %s)%n", count++, orientation);

        if (orientation == Orientation.HORIZONTAL) {
            if (startX > endX) {
                start.setPosition(startX, startY, 90, startStyle);
                end.setPosition(endX, endY, 270, endStyle);
            } else {
                start.setPosition(startX, startY, 270, startStyle);
                end.setPosition(endX, endY, 90, endStyle);
            }
            curveTo.setControlX1((end.getConnX() + start.getConnX()) / 2);
            curveTo.setControlY1(start.getConnY());
            curveTo.setControlX2((end.getConnX() + start.getConnX()) / 2);
            curveTo.setControlY2(end.getConnY());
        } else {
            if (endY > startY) {
                start.setPosition(startX, startY, 0, startStyle);
                end.setPosition(endX, endY, 180, endStyle);
            } else {
                start.setPosition(startX, startY, 180, startStyle);
                end.setPosition(endX, endY, 0, endStyle);
            }
            curveTo.setControlX1(start.getConnX());
            curveTo.setControlY1((end.getConnY() + start.getConnY()) / 2);
            curveTo.setControlX2(end.getConnX());
            curveTo.setControlY2((end.getConnY() + start.getConnY()) / 2);
        }

        moveTo.setX(start.getConnX());
        moveTo.setY(start.getConnY());
        curveTo.setX(end.getConnX());
        curveTo.setY(end.getConnY());

        // reset path elements
//        getElements().setAll(moveTo, curveTo);
//        getElements().addAll(start.getElements());
//        getElements().addAll(end.getElements());
    }


// TODO: How to delay update() to a later time so that the setters are not unnecessarily
// costly? Means, can we set the new values here only and "schedule" a "repaint"?
    

    public void setDirection(Orientation ori) {
        this.orientation = ori;
        update();
    }

    
    public void setEndX(double x) {
        this.endX = x;
        update();
    }

    public void setEndY(double y) {
        this.endY = y;
        update();
    }


    public void setStartX(double x) {
        this.startX = x;
        update();
    }

    public void setStartY(double y) {
        this.startY = y;
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
