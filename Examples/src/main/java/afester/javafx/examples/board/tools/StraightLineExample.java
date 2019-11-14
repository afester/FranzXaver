package afester.javafx.examples.board.tools;


import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class StraightLineExample extends Application {

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

        BorderPane mainLayout = new BorderPane();

        mainLayout.setCenter(drawPane);

        StraightLine l1 = new StraightLine(new Point2D(6, 4), new Point2D(10, 6));
        StraightLine l2 = new StraightLine(new Point2D(7, 4), new Point2D(6, 11));
        
//        StraightLine l1 = new StraightLine(new Point2D(6, 4), new Point2D(10, 4));
//        StraightLine l2 = new StraightLine(new Point2D(7, 2), new Point2D(7, 11));

//        StraightLine l1 = new StraightLine(new Point2D(1, 5), new Point2D(10, 5));
//        StraightLine l2 = new StraightLine(new Point2D(1, 5.1), new Point2D(10, 4.9));

//        StraightLine l1 = new StraightLine(new Point2D(1, 5), new Point2D(10, 5));
//        StraightLine l2 = new StraightLine(new Point2D(1, 5), new Point2D(10, 5));
        render(l1, Color.GREEN);
        render(l2, Color.BLUE);

        final Point2D p = new Point2D(10, 10); // 6.46, 7.78); // 8, 8); 
        render(p, Color.GREEN);

        final StraightLine ortho = l2.getOrthogonalLine(p);
        render(ortho, Color.CYAN);

        final Point2D footPoint = l2.getFootpoint(p);
        System.err.println("Footpoint:" + footPoint);
        render(footPoint, Color.CYAN);

        System.err.println("Distance:" + l2.getDistance(p));

        final Point2D intersection = l1.intersection(l2);
        render(intersection);

        // show the generated scene graph
        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void render(StraightLine l, Color col) {
        Line line = new Line(l.getStart().getX() * 20, l.getStart().getY() * 20, l.getEnd().getX() * 20, l.getEnd().getY() * 20);
        line.setStroke(col);
        drawPane.getChildren().add(line);
    }

    private void render(Point2D point) {
        render(point, Color.RED);
    }

    private void render(Point2D point, Color col) {
        Circle circle = new Circle(point.getX() * 20, point.getY() * 20, 2);
        circle.setFill(col);
        drawPane.getChildren().add(circle);
    }
}
