package afester.javafx.examples.board;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;

public class DimensionView extends Group {

    final private static Font DIM_FONT = Font.font("Courier", 2.0);
    final private static Point2D UNIT_VEC = new Point2D(1.0, 0.0);

    /**
     * Creates a new dimension line for the given points.
     * The direction vector goes from p1 to p2, and the dimension line is created at
     * a specific distance *above* that direction vector.
     * The dimension text is located with the dimension line as the base line, and centered
     * to the dimension line.
     *
     * @param p1
     * @param p2
     */
    public DimensionView(Point2D p1, Point2D p2) {
        final Point2D vecDir = p2.subtract(p1);                               // direction vector
        System.err.println("DIR: " + vecDir);
        Point2D vecNorm = new Point2D(vecDir.getY(), -vecDir.getX());   // norm vector
        vecNorm = vecNorm.normalize();                                  // normalized norm vector ...
        vecNorm = vecNorm.multiply(3.0);                                // ... of length 3.0

        // calculate the end points for a line which is parallel to the two given points
        final Point2D p1_1 = p1.add(vecNorm);
        final Point2D p2_1 = p2.add(vecNorm);

        // calculate the mid point of the line
        final Point2D midpoint = p2_1.midpoint(p1_1);

        // calculate the angle relative to the X axis
        Double angle = UNIT_VEC.angle(vecDir);  // 0 .. 180
        if (vecDir.getY() > 0) {
            angle = 360 - angle;
        }
        angle = 360 - angle;

        //     __
        //      /|
        //     /
        //    /
        //   / * angle
        //   ----->

//        if (angle > 180) {
//            angle -= 180;
//        }

        Double length = p1.distance(p2);
        System.err.printf("%s/%s => %s\n", Point2D.ZERO, vecDir, angle);

///////////////////////////

        // create the dimension line
        Line l = new Line(p1_1.getX(), p1_1.getY(), p2_1.getX(), p2_1.getY());
        getChildren().add(l);

        // visualize the mid point - for debugging purposes
        Circle c = new Circle(midpoint.getX(), midpoint.getY(), 0.5);
        c.setFill(null);
        c.setStroke(Color.RED);
        c.setStrokeWidth(0.3);
        getChildren().add(c);

        
        Group textBox = new Group();
        
        // Add the text for the dimension line
        //Text value = new Text(midpoint.getX(), midpoint.getY(), String.format("%.1f mm / %s", length, angle));
        Text value = new Text(String.format("%.1f mm / %s", length, angle));
        value.setFont(DIM_FONT);
        value.setBoundsType(TextBoundsType.VISUAL);

        // value.setRotationAxis(new Point3D(midpoint.getX(), midpoint.getY(), 0.0));

        // value.setTextAlignment(TextAlignment.RIGHT); // In the case of a single line of text, where the width of the node
                                                        // is determined by the width of the text, the alignment setting has no effect.

        value.setTextOrigin(VPos.BASELINE); // the vertical alignment of the text, relative to the given coordinates
        //value.setRotate(angle);

        Bounds b = value.getBoundsInLocal();
        Rectangle r = new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
        r.setFill(Color.GREENYELLOW);

        textBox.getChildren().addAll(r, value);
        textBox.setRotate(angle);
        textBox.setLayoutX(midpoint.getX());
        textBox.setLayoutY(midpoint.getY());
        getChildren().add(textBox);
    }

//    @Override
//    public String toString() {
//        return String.format("HoleView[centerX=%s, centerY=%s, radius=%s, fill=%s, stroke=%s, strokeWidth=%s]",
//                              getCenterX(), getCenterY(), getRadius(), getFill(), getStroke(),getStrokeWidth());
//    }
}
