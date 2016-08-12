package afester.javafx.shapes;

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
    private Point2D conn = new Point2D(0, 0);    // the connection point for the line

    private double dx = 0;
    private Rotate rotation = null;

    private ArrowStyle style = new ArrowStyle(ArrowShape.OPEN, 15, 45, false);

    public Arrow(double x, double y, double rot, ArrowStyle style) {
        setPosition(x, y, rot, style);
    }

    public Arrow() {
        this(0, 0, 0, null); // new ArrowStyle(ArrowShape.NONE, 0, 0, false));
    }


    public void setPosition(double x, double y, double rot, ArrowStyle style) {
        this.style = style;

        if (style == null) {
            getElements().clear();
            conn = new Point2D(x, y);
            return;
        }

        head = new Point2D(x, y);

        dx = style.getLength() * Math.tan(Math.toRadians(style.getAngle() / 2));
        rotation = new Rotate(rot, x, y);

        tail1 = rotation.transform(x - dx, y + style.getLength());
        tail2 = rotation.transform(x + dx, y + style.getLength());

        switch(style.getShape()) {
            case OPEN   : createOpenArrow();   
                          break;

            case CLOSED : createClosedArrow(); 
                          break;

            case TAILED : createTailedArrow(); 
                          break;

            case DIAMOND: createDiamondArrow(); 
                          break;

//            case NONE   : getElements().clear();
//                          conn = new Point2D(x, y);
//                          break;
        }
        
        if (style.isFilled()) {
            setFill(Color.BLACK);
        }
    }

    public double getConnX() {
        return conn.getX();
    }

    public double getConnY() {
        return conn.getY();
    }


    private void createClosedArrow() {
        conn = rotation.transform(new Point2D(head.getX(), head.getY() + style.getLength()));

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
        conn = rotation.transform(new Point2D(head.getX(), head.getY() + style.getLength() * 0.75));

        MoveTo start = new MoveTo(head.getX(), head.getY());
        LineTo first = new LineTo(tail1.getX(), tail1.getY());
        LineTo second = new LineTo(conn.getX(), conn.getY());
        LineTo third = new LineTo(tail2.getX(), tail2.getY());
        ClosePath fourth = new ClosePath();

        getElements().setAll(start, first, second, third, fourth);
    }


    private void createDiamondArrow() {
        tail1 = rotation.transform(head.getX() - dx, head.getY() + style.getLength() / 2);
        tail2 = rotation.transform(head.getX() + dx, head.getY() + style.getLength() / 2);
        conn = rotation.transform(new Point2D(head.getX(), head.getY() + style.getLength()));

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
        return String.format("Arrow[%s]", getElements());
    }


}
