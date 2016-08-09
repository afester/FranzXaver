package afester.javafx.examples.shapes;

import javafx.geometry.Point2D;
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

    public Arrow(double x, double y, double rot) {
        head = new Point2D(x, y);
        tail1 = new Point2D(x - 10, y + 20);
        tail2 = new Point2D(x + 10, y + 20);

        Rotate rotation = new Rotate(rot, x, y);
        tail1 = rotation.transform(x - 10, y + 20);
        tail2 = rotation.transform(x + 10, y + 20);
        conn = rotation.transform(new Point2D(x, y + 10));

        createClosedArrow();
    }

    private void createFilledArrow() {
        
    }
    
    private void createOpenArrow() {
        
    }

    private void createClosedArrow() {
        MoveTo start = new MoveTo(head.getX(), head.getY());
        LineTo first = new LineTo(tail1.getX(), tail1.getY());
        LineTo second = new LineTo(conn.getX(), conn.getY());
        LineTo third = new LineTo(tail2.getX(), tail2.getY());
        ClosePath fourth = new ClosePath();

        getElements().addAll(start, first, second, third, fourth);
    }

    public Point2D[] getEditHandles() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("Arrow[%s]", 1); // getElements());
    }

    public double getConnX() {
        return conn.getX();
    }

    public double getConnY() {
        return conn.getY();
    }
}
