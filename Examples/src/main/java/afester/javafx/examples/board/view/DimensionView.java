package afester.javafx.examples.board.view;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;


class TextBox extends Group {
    private final Text theText;

    public TextBox(String text, Font font) {

        theText = new Text(text);
        theText.setBoundsType(TextBoundsType.VISUAL);

        if (font != null) {
            theText.setFont(font);
        }

        // value.setTextAlignment(TextAlignment.RIGHT); // In the case of a single line of text, where the width of the node
                                                        // is determined by the width of the text, the alignment setting has no effect.

        getChildren().add(theText);
        this.applyCss();

// DEBUG: Add a highlighted background behind the text
//        Bounds b = getBoundsInLocal();
//        Rectangle r = new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
//        System.err.println("XXXX:" + r);
//        r.setFill(Color.GREENYELLOW);
//        r.setStroke(null);
//        r.setStrokeWidth(0);
//        getChildren().add(0, r);

// DEBUG: Create a cross indicating the center point
//        Line l1 = new Line(b.getMinX() + b.getWidth()/2, 
//                           b.getMinY(), 
//                           b.getMinX() + b.getWidth()/2, 
//                           b.getMinY() + b.getHeight());
//        l1.setStrokeLineCap(StrokeLineCap.BUTT);
//        l1.setStroke(Color.BLUE);
//        l1.setStrokeWidth(0.1);
//
//        Line l2 = new Line(b.getMinX(),
//                           b.getMinY() + b.getHeight() / 2, 
//                           b.getMinX() + b.getWidth(), 
//                           b.getMinY() + b.getHeight()/2);
//        l2.setStrokeLineCap(StrokeLineCap.BUTT);
//        l2.setStroke(Color.RED);
//        l2.setStrokeWidth(0.1);

        // getChildren().addAll(r, theText, l1, l2);
//        getChildren().addAll(theText);
    }
}

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

        Double length = p1.distance(p2);

/////////////////////////

        // create the dimension line
        Line l = new Line(p1_1.getX(), p1_1.getY(), p2_1.getX(), p2_1.getY());
        getChildren().add(l);

//DEBUG: visualize the mid point - for debugging purposes
//    Circle c = new Circle(midpoint.getX(), midpoint.getY(), 0.5);
//    c.setFill(null);
//    c.setStroke(Color.RED);
//    c.setStrokeWidth(0.3);
//    getChildren().add(c);

        TextBox tb = new TextBox(String.format("%.1f mm", length), DIM_FONT);
        final Bounds offset = tb.getLayoutBounds(); // Note: the local X- and Y coordinates of the TextBox are NOT 0!
        tb.setLayoutX((midpoint.getX() - offset.getMinX()) - offset.getWidth() / 2);
        tb.setLayoutY(midpoint.getY() - offset.getMinY() - offset.getHeight() / 2);

//        Bounds b = tb.getBoundsInParent();
//        Circle c4 = new Circle(b.getMinX() + b.getWidth()/2, b.getMinY() + b.getHeight()/2, 0.3);
//        c4.setFill(null);
//        c4.setStrokeWidth(0.2);
//        c4.setStroke(Color.CYAN);
        
//        final Bounds size = textBox.localToParent(textBox.getLayoutBounds());
//
//        System.err.println("SIZE:" + size);
//        textBox.setLayoutX(midpoint.getX() - size.getWidth() /2 - 0.2);
//        textBox.setLayoutY(midpoint.getY() - 2);
//
//        final Bounds b2 = textBox.localToParent(textBox.getLayoutBounds());
//        System.err.println("B2:" + b2);

// DEBUG: 0-based border!!
//        final Bounds br = tb.getBoundsInLocal();
//        Rectangle borders = new Rectangle(0, -br.getHeight(), /*br.getMinX(), br.getMinY(), */br.getWidth(), br.getHeight());
//        borders.setFill(null);
//        borders.setStroke(Color.RED);
//        borders.setStrokeWidth(0.1);
//        borders.getStrokeDashArray().addAll(1.0, 1.0);
//        tb.getChildren().add(borders);

        // Position the text box at a specific distance above the dimension line 
        final Point2D p = tb.parentToLocal(midpoint);
        tb.getTransforms().add(new Rotate(angle, p.getX(), p.getY()));
        tb.getTransforms().add(new Translate(0,  -2));
        
//        Circle c2 = new Circle(b2.getMinX() + b2.getWidth() / 2.0, b2.getMinY() + b2.getHeight() / 2, 0.5);
//        c2.setFill(null);
//        c2.setStroke(Color.BLUE);
//        c2.setStrokeWidth(0.3);
//
//        Rectangle r2 = new Rectangle(b2.getMinX(), b2.getMinY(), b2.getWidth(), b2.getHeight());
//        r2.setFill(Color.CADETBLUE);

        getChildren().addAll(tb); // , c4); // , c2);
    }

}
