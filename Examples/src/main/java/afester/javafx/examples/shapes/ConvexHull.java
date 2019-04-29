package afester.javafx.examples.shapes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import afester.javafx.shapes.Circle;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
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

    private Pane drawPane = new Pane();

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Convex hull example");

        List<Point2D> points = new ArrayList<>();
        points.add(new Point2D(100, 100));
        points.add(new Point2D(120, 200));
        points.add(new Point2D(90, 140));
        points.add(new Point2D(140, 50));
        points.add(new Point2D(80, 40));
        points.add(new Point2D(130, 130));
        points.add(new Point2D(180, 130));
        
        renderScene(points);
//        
//        points.forEach(e -> drawPane.getChildren().add(new PointShape(e.getX(), e.getY())));
//
//        List<Point2D> convexHull = calculateConvexHull(points);
//
//        Polygon p = new PolygonShape(convexHull);
//        p.setFill(null);
//        p.setStroke(Color.BLACK);
//
//        drawPane.getChildren().add(p);

        BorderPane mainLayout = new BorderPane();
        HBox controls = new HBox();
        Button randomButton = new Button("Random");
        randomButton.setOnAction(e -> createRandomPointSet());
        controls.getChildren().add(randomButton);

        mainLayout.setBottom(controls);
        mainLayout.setCenter(drawPane);

        // show the generated scene graph
        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private final static int MAX_POINTS = 20;
    private boolean odd = true;
    private Integer xpos = 0;

    private void createRandomPointSet() {

        List<Point2D> points = new ArrayList<>();
        odd = true;
        Random r = new Random(System.currentTimeMillis());
        r.ints(MAX_POINTS*2, 0, 201).forEach(e -> {
            if (odd) {
                xpos = e;
            } else {
                Point2D p = new Point2D(xpos, e);
                System.err.println(p);
                points.add(p);
            }

            odd = !odd;
        });

        renderScene(points);
    }
    
    private void renderScene(List<Point2D> points) {
        List<Point2D> convexHull = calculateConvexHull(points);

        drawPane.getChildren().clear();

        Polygon p = new PolygonShape(convexHull);
        p.setFill(null);
        p.setStroke(Color.BLACK);

        points.forEach(e -> drawPane.getChildren().add(new PointShape(e.getX(), e.getY())));
        drawPane.getChildren().add(p);
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
        // Note: can be at most 180� since P0 is the lowest point!
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
            System.err.printf("  %s -> %s�\n", p, angle);
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
