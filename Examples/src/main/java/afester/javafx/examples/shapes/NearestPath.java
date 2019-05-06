package afester.javafx.examples.shapes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class NearestPath extends Application {

    private Pane drawPane = new Pane();

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX nearest path example");

        List<Point2D> points = new ArrayList<>();
        points.add(new Point2D(100, 100));
        points.add(new Point2D(120, 200));
        points.add(new Point2D(90, 140));
        points.add(new Point2D(140, 50));
        points.add(new Point2D(80, 40));
        points.add(new Point2D(130, 130));
        points.add(new Point2D(180, 130));
        
        renderScene(points);

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
                points.add(p);
            }

            odd = !odd;
        });

        renderScene(points);
    }
    
    
    private static void lineIterator(Iterable<Point2D> iterable, BiConsumer<Point2D, Point2D> consumer) {
        Iterator<Point2D> it = iterable.iterator();

        if(!it.hasNext()) return;
        Point2D previous = it.next();

        while(it.hasNext()) {
            Point2D current = it.next();

            consumer.accept(previous, current);
            previous = current;
        }
    }
    
    private void renderScene(List<Point2D> points) {
        List<Point2D> nearestPath = calculateNearestPath(points);

        drawPane.getChildren().clear();

        // Polygon p = new PolygonShape(convexHull);
        // p.setFill(null);
        // p.setStroke(Color.BLACK);

        points.forEach(e -> {
            if (e == nearestPath.get(0))
                drawPane.getChildren().add(new PointShape(e.getX(), e.getY(), Color.BLUE));
            else
                drawPane.getChildren().add(new PointShape(e.getX(), e.getY(), Color.RED));
        });

        lineIterator(nearestPath, (p1, p2) -> {
            drawPane.getChildren().add(new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY()));
        });
        
        // drawPane.getChildren().add(p);
    }

    /**
     * Calculate the shortest path using the nearest point heuristic. 
     *
     * @param points The input list of points.
     * @return The list of points in the order of their nearest point.
     */
    private List<Point2D> calculateNearestPath(List<Point2D> points) {
        List<Point2D> result = new ArrayList<>(points);

        for (int idx2 = 0;  idx2 < points.size() - 1;  idx2++) {
            Point2D P0 = result.get(idx2);

            double minDist = Double.MAX_VALUE;
            int nearestIdx = -1;
            Point2D nearest = null;
            for (int idx = idx2+1;  idx < result.size();  idx++) {
                Point2D p = result.get(idx);
                double dist = P0.distance(p);
                if (dist < minDist) {
                    nearestIdx = idx;
                    minDist = dist;
                    nearest = p;
                }
            }

            // swap the next point with the nearest one.
            Point2D tmp = result.get(idx2+1);
            result.set(idx2+1, nearest);
            result.set(nearestIdx, tmp);
        }

        return result;
    }
}
