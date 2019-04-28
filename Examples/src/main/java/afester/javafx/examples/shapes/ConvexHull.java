package afester.javafx.examples.shapes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import afester.javafx.shapes.Circle;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

class PointShape extends Group {

    public PointShape(double x, double y) {
        
        Circle c = new Circle(x, y, 3);
        c.setFill(Color.RED);
        c.setStroke(null);
        
        Text t = new Text(x, y, String.format("%d/%d",  (int) x, (int) y));
        getChildren().addAll(c, t);
    }
    
}

public class ConvexHull extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Convex hull example");

        // BorderPane mainLayout = new BorderPane();
        
        Pane drawPane = new Pane();

        List<Point2D> points = new ArrayList<>();
        points.add(new Point2D(100, 100));
        points.add(new Point2D(120, 200));
        points.add(new Point2D(90, 140));
        points.add(new Point2D(140, 50));
        points.add(new Point2D(80, 40));
        points.add(new Point2D(130, 130));
        points.add(new Point2D(180, 130));
        points.forEach(e -> drawPane.getChildren().add(new PointShape(e.getX(), e.getY())));

        List<Point2D> convexHull = calculateConvexHull(points);

        // draw the polygon which visualizes the convex hull
        Point2D p0 = convexHull.get(0);
        Iterator<Point2D> iter = convexHull.iterator();
        Point2D next = iter.next();
        while(iter.hasNext()) {
            next = iter.next();
            System.err.println(p0.toString() + next.toString());

            Line l = new Line(p0.getX(), p0.getY(), next.getX(), next.getY());
            drawPane.getChildren().add(l);
            p0 = next;
        }
        // closing line
        p0 = convexHull.get(0);
        Line l = new Line(next.getX(), next.getY(), p0.getX(), p0.getY());
        drawPane.getChildren().add(l);
        

        // show the generated scene graph
        Scene scene = new Scene(drawPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    final static Point2D UNIT_X = new Point2D(1, 0);
    

    private List<Point2D> calculateConvexHull(List<Point2D> points) {
        Stack<Point2D> stack = new Stack<>();

        // find the lowest y-coordinate and leftmost point, called P0
        System.err.println(points);
        final Point2D P0 = points.stream().max((a, b) -> Double.compare(a.getY(), b.getY())).get();
        System.err.println("P0: " + P0);

//        sort remaining points by polar angle with P0, if several points have the same polar angle then only keep the farthest
        points.remove(P0);
        // Note: can be at most 180° since P0 is the lowest point!
        List<Point2D> sorted = new ArrayList<>();
        points.stream().sorted((a, b) -> (int) Math.signum((UNIT_X.angle(a.subtract(P0)) - UNIT_X.angle(b.subtract(P0))))).forEach(e -> sorted.add(e));

        System.err.println("INPUT:");
        dumpAngles(P0, points);
        System.err.println("SORTED:");
        dumpAngles(P0, sorted);
        System.err.println("SORTED:" + sorted);

        stack.push(P0);
        stack.push(sorted.get(0));
        for (int i = 1;  i < sorted.size();  i++) {
            Point2D e = sorted.get(i);
//        sorted.forEach(e -> {
            // pop the last point from the stack if we turn clockwise to reach this point
            while(stack.size() > 1 && ccw(stack.get(stack.size() - 2), stack.get(stack.size() - 1), e) > 0) {
                stack.pop();
            }
            stack.push(e);
        } // );
        
        System.err.println(stack);
        return stack.subList(0,  stack.size());
    }

    private void dumpAngles(Point2D P0, List<Point2D> points) {
        for (Point2D p : points) {
            double angle = UNIT_X.angle(p.subtract(P0));
            System.err.printf("  %s -> %s°\n", p, angle);
        }
    }

    private int ccw(Point2D p1, Point2D p2, Point2D p3) {
        double crossZ = (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p2.getY() - p1.getY()) * (p3.getX() - p1.getX());
        if (crossZ > 0) {
            return 1;
        }
        if (crossZ < 0) {
            return -1;
        }

        return 0;
    }
}
