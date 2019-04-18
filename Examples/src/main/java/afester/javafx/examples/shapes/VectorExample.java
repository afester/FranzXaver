package afester.javafx.examples.shapes;

import afester.javafx.examples.Example;
import afester.javafx.shapes.ArrowShape;
import afester.javafx.shapes.ArrowStraightLine;
import afester.javafx.shapes.ArrowStyle;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;


/**
 */
@Example(desc = "Vector",
         cat  = "FranzXaver")
public class VectorExample extends Application {

    private ArrowStraightLine baseLine; 
    private ArrowStraightLine rotatedLine;
    private Circle centerCirc;
    private Circle endCirc;

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX vector example");

        BorderPane mainLayout = new BorderPane();

        Pane drawPane = new Pane();

        baseLine = new ArrowStraightLine(200, 300, 400, 250);
        baseLine.setEndArrow(new ArrowStyle(ArrowShape.OPEN, 20, 30, false));
        rotatedLine = new ArrowStraightLine(200, 300, 350, 200);
        rotatedLine.setEndArrow(new ArrowStyle(ArrowShape.OPEN, 20, 30, false));

        centerCirc = new Circle(rotatedLine.getStart().getX(), rotatedLine.getStart().getY(), 3);
        centerCirc.setStroke(Color.RED);
        endCirc = new Circle(3);
        endCirc.setStroke(Color.RED);
        updateScene(45);

        drawPane.getChildren().addAll(baseLine, rotatedLine, centerCirc, endCirc); 

        VBox bottomPanel = new VBox();

        Slider s = new Slider(0, 360, 45);
        s.valueProperty().addListener((a, b, newVal) -> {
            System.err.println(newVal);
            updateScene(newVal.doubleValue());
        });
        bottomPanel.getChildren().add(s);

        mainLayout.setBottom(bottomPanel);
        mainLayout.setCenter(drawPane);

        // show the generated scene graph
        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void updateScene(final double angle) {
        final Point2D p1 = baseLine.getStart();
        final Point2D p2 = baseLine.getEnd();

        final Point2D dirVec = p2.subtract(p1);     // direction vector of the base line
        // rotate the direction vector
        final double rad = -Math.toRadians(angle);
        final Point2D dirVec2 = new Point2D(Math.cos(rad) * dirVec.getX() - Math.sin(rad) * dirVec.getY(), 
                                            Math.sin(rad) * dirVec.getX() + Math.cos(rad) * dirVec.getY());

        // set the new end point
        final Point2D endPoint = p1.add(dirVec2);
        rotatedLine.setEnd(endPoint);
        endCirc.setCenterX(endPoint.getX());
        endCirc.setCenterY(endPoint.getY());
    }

}
