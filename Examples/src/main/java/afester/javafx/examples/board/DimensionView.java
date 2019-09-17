package afester.javafx.examples.board;

import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DimensionView extends Group {

    final private static Font DIM_FONT = Font.font("Courier", 2.0);

    public DimensionView(Point2D p1, Point2D p2, Point2D midpoint, Double length, Double angle) {
        Line l = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        getChildren().add(l);

      Circle c = new Circle(midpoint.getX(), midpoint.getY(), 0.5);
      c.setFill(null);
      c.setStroke(Color.RED);
      c.setStrokeWidth(0.3);
      getChildren().add(c);

        Text value = new Text(midpoint.getX(), midpoint.getY(), String.format("%.1f mm", length));
        // value.setRotationAxis(new Point3D(midpoint.getX(), midpoint.getY(), 0.0));
        value.setTextOrigin(VPos.BASELINE);
        value.setRotate(angle);
        value.setFont(DIM_FONT);

        getChildren().add(value);
    }

//    @Override
//    public String toString() {
//        return String.format("HoleView[centerX=%s, centerY=%s, radius=%s, fill=%s, stroke=%s, strokeWidth=%s]",
//                              getCenterX(), getCenterY(), getRadius(), getFill(), getStroke(),getStrokeWidth());
//    }
}
