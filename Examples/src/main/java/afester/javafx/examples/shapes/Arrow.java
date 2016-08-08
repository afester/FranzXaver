package afester.javafx.examples.shapes;

import javafx.geometry.Point2D;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

// Shape can not be extended unless we use @deprecated APIs ...
//
//public class Arrow extends Shape implements EditableShape {
//
//@Override
//public com.sun.javafx.geom.Shape impl_configShape() {
//    return null;
//}

public class Arrow extends Path implements EditableShape {

    public Arrow(double x, double y, double rot) {
        createClosedArrow(x, y);
        setRotate(rot);
    }

    private void createFilledArrow() {
        
    }
    
    private void createOpenArrow() {
        
    }

    private void createClosedArrow(double x, double y) {
        MoveTo start = new MoveTo(x, y);
        LineTo first = new LineTo(x + 10, y + 20);
        LineTo second = new LineTo(x, y + 10);
        LineTo third = new LineTo(x - 10, y + 20);
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
}
