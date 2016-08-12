package afester.javafx.shapes;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;

public class ArrowCurvedLine extends ArrowLine  {

    private CubicCurve curve = new CubicCurve();
    private Orientation orientation;

    public ArrowCurvedLine(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;

        curve.setFill(null);
        curve.setStroke(Color.BLACK);

        update();
    }
    private static int count = 0;

    
    @Override
    protected void update() {
        System.err.printf("update(%s, %s)%n", count++, orientation);

        if (orientation == Orientation.HORIZONTAL) {
            if (startX > endX) {
                start.setPosition(startX, startY, 90, startStyle);
                end.setPosition(endX, endY, 270, endStyle);
            } else {
                start.setPosition(startX, startY, 270, startStyle);
                end.setPosition(endX, endY, 90, endStyle);
            }
            curve.setControlX1((end.getConnX() + start.getConnX()) / 2);
            curve.setControlY1(start.getConnY());
            curve.setControlX2((end.getConnX() + start.getConnX()) / 2);
            curve.setControlY2(end.getConnY());
        } else {
            if (endY > startY) {
                start.setPosition(startX, startY, 0, startStyle);
                end.setPosition(endX, endY, 180, endStyle);
            } else {
                start.setPosition(startX, startY, 180, startStyle);
                end.setPosition(endX, endY, 0, endStyle);
            }
            curve.setControlX1(start.getConnX());
            curve.setControlY1((end.getConnY() + start.getConnY()) / 2);
            curve.setControlX2(end.getConnX());
            curve.setControlY2((end.getConnY() + start.getConnY()) / 2);
        }

        curve.setStartX(start.getConnX());
        curve.setStartY(start.getConnY());
        curve.setEndX(end.getConnX());
        curve.setEndY(end.getConnY());

        // reset path elements
        getChildren().setAll(curve, start, end);
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

    public void setStrokeWidth(Double newWidth) {
        curve.setStrokeWidth(newWidth);
        start.setStrokeWidth(newWidth);
        end.setStrokeWidth(newWidth);
    }


    public ObservableList<Double> getStrokeDashArray() {
        return curve.getStrokeDashArray();
    }

    @Override
    public String toString() {
        return String.format("ArrowLine[x1=%s, y1=%s, x2=%s, y2=%s]", -1, -1, -1, -1);
    }
}
