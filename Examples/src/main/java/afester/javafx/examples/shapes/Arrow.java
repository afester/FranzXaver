package afester.javafx.examples.shapes;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.transform.Rotate;

// Shape can not be extended unless we use @deprecated APIs ...
//
//public class Arrow extends Shape implements EditableShape {
//
//@Override
//public com.sun.javafx.geom.Shape impl_configShape() {
//    return null;
//}

public class Arrow extends Path implements EditableShape {

    private Point2D head = null;    // the head of the arrow
    private Point2D tail1 = null;   // the first tail point of the arrow
    private Point2D tail2 = null;   // the second tail point of the arrow
    private Point2D conn = null;    // the connection point for the line
    private Rotate rotation = null;

    private double length = 15;
    private double angle = 45;

    public Arrow(double x, double y, double rot, ArrowStyle style, double length, double angle) {
        setPosition(x, y, rot, style, length, angle);
    }

    public Arrow() {
        this(0, 0, 0, ArrowStyle.OPEN, 0, 0);
    }


    public void setPosition(double x, double y, double rot, ArrowStyle style, double length, double angle) {
        this.angle = angle;
        this.length = length;

        head = new Point2D(x, y);

        double dx = length * Math.tan(Math.toRadians(angle/2));
        rotation = new Rotate(rot, x, y);
        tail1 = rotation.transform(x - dx, y + length);
        tail2 = rotation.transform(x + dx, y + length);

        switch(style) {
            case OPEN   : createOpenArrow();   break;
            case CLOSED : createClosedArrow(); break;
            case TAILED : createTailedArrow(); break;
            case FILLED : createClosedArrow();
                          setFill(Color.BLACK);
                          break;
            case NONE   : getElements().clear();
                          break;
        }
    }

    public double getConnX() {
        return conn.getX();
    }

    public double getConnY() {
        return conn.getY();
    }


    private void createClosedArrow() {
        conn = rotation.transform(new Point2D(head.getX(), head.getY() + length));

        MoveTo start = new MoveTo(head.getX(), head.getY());
        LineTo first = new LineTo(tail1.getX(), tail1.getY());
        LineTo second = new LineTo(tail2.getX(), tail2.getY());
        ClosePath third = new ClosePath();

        getElements().setAll(start, first, second, third);
    }

    private void createOpenArrow() {
        conn = head;

        MoveTo start = new MoveTo(tail1.getX(), tail1.getY());
        LineTo first = new LineTo(head.getX(), head.getY());
        LineTo second = new LineTo(tail2.getX(), tail2.getY());

        getElements().setAll(start, first, second);
    }

    private void createTailedArrow() {
        conn = rotation.transform(new Point2D(head.getX(), head.getY() + length/2));

        MoveTo start = new MoveTo(head.getX(), head.getY());
        LineTo first = new LineTo(tail1.getX(), tail1.getY());
        LineTo second = new LineTo(conn.getX(), conn.getY());
        LineTo third = new LineTo(tail2.getX(), tail2.getY());
        ClosePath fourth = new ClosePath();

        getElements().setAll(start, first, second, third, fourth);
    }

    public Point2D[] getEditHandles() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("Arrow[%s]", 1); // getElements());
    }


}
