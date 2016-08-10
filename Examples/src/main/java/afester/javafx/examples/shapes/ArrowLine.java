package afester.javafx.examples.shapes;

import javafx.geometry.Point2D;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class ArrowLine extends Path implements EditableShape {
    private Arrow start = new Arrow();
    private Arrow end = new Arrow();
    private MoveTo moveTo = new MoveTo();
    private LineTo lineTo = new LineTo();

    private static int count = 0;
    private double startX, startY, endX, endY;

    // TODO: Probably ArrowShape, and make ArrowStyle a class of its own
    // with size and angle members
    private ArrowStyle startStyle = ArrowStyle.CLOSED;
    private ArrowStyle endStyle = ArrowStyle.CLOSED;
    double startLength, startAngle;
    double endLength, endAngle;

    public ArrowLine(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;

        update();
    }

    private void update() {
        System.err.printf("update(%s)%n", count++);

        // update arrow positions and line start/end position
        double rot = Math.toDegrees(Math.atan2(endY - startY, endX - startX));

        start.setPosition(startX, startY, rot + 270, startStyle, startLength, startAngle);
        end.setPosition(endX, endY, rot + 90, endStyle, endLength, endAngle);

        moveTo.setX(start.getConnX());
        moveTo.setY(start.getConnY());
        lineTo.setX(end.getConnX());
        lineTo.setY(end.getConnY());

        // reset path elements
        getElements().setAll(moveTo, lineTo);
        getElements().addAll(start.getElements());
        getElements().addAll(end.getElements());
    }


// TODO: How to delay update() to a later time so that the setters are not unnecessarily
// costly? Means, can we set the new values here only and "schedule" a "repaint"?
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

    public void setStartArrow(ArrowStyle style, double size, double angle) {
        this.startStyle = style;
        this.startLength = size;
        this.startAngle = angle;
        update();
    }


    public void setEndArrow(ArrowStyle style, double size, double angle) {
        this.endStyle = style;
        this.endLength = size;
        this.endAngle = angle;
        update();
    }
}
