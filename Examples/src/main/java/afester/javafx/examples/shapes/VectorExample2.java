package afester.javafx.examples.shapes;

import afester.javafx.examples.Example;
import afester.javafx.shapes.ArcFactory;
import afester.javafx.shapes.ArcParameters;
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
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

class Vector extends Point2D {

    public Vector(double arg0, double arg1) {
        super(arg0, arg1);
    }
    
}

/**
 */
@Example(desc = "Vector",
         cat  = "FranzXaver")
public class VectorExample2 extends Application {

	private Point2D p1;
	private Point2D p2;
//    private ArrowStraightLine baseLine; 
    private ArrowStraightLine rotatedLine;
    private ArrowStraightLine perpLine;

    private Circle centerCirc;
    private Arc resultCirc;

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

        p1 = new Point2D(200, 300);
        p2 = new Point2D(600, 200);
//        baseLine = new ArrowStraightLine(p1, pm);
//        baseLine.setEndArrow(new ArrowStyle(ArrowShape.OPEN, 20, 30, false));
// 
//        Point2D baseLineDir = pm.subtract(p1);
//        Point2D baseLinePerpendicular = new Point2D(baseLineDir.getY(), -baseLineDir.getX());
//        Point2D p4 = pm.add(baseLinePerpendicular);
//
        perpLine = new ArrowStraightLine(0, 0, 0, 0);
        perpLine.setEndArrow(new ArrowStyle(ArrowShape.OPEN, 20, 30, false));

        rotatedLine = new ArrowStraightLine(200, 300, 350, 200);
        rotatedLine.setEndArrow(new ArrowStyle(ArrowShape.OPEN, 20, 30, false));

        Circle p1Circ = new Circle(p1.getX(), p1.getY(), 3);
        p1Circ.setStroke(Color.RED);
        Circle p2Circ = new Circle(p2.getX(), p2.getY(), 3);
        p2Circ.setStroke(Color.RED);
        centerCirc = new Circle(3);
        centerCirc.setStroke(Color.GREEN);


        resultCirc = new Arc();
        resultCirc.setType(ArcType.OPEN);
        resultCirc.setStrokeWidth(5);
        resultCirc.setFill(null);
        resultCirc.setStroke(Color.BLUE);

        updateScene(180);

        drawPane.getChildren().addAll(//baseLine, rotatedLine, 
        		perpLine, 
        							  p1Circ, centerCirc, p2Circ, 
        							  resultCirc); 

        VBox bottomPanel = new VBox();

        Slider s = new Slider(-360, 360, 180);
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


    private void updateScene(final double cAngle) {
    	ArcParameters ap = ArcFactory.arcFromPointsAndAngle(p1, p2, -cAngle, Color.BLUE);

        resultCirc.setCenterX(ap.getCenter().getX());
        resultCirc.setCenterY(ap.getCenter().getY());
        resultCirc.setRadiusX(ap.getRadius());
        resultCirc.setRadiusY(ap.getRadius());
        resultCirc.setLength(ap.getLength());
        resultCirc.setStartAngle(ap.getStartAngle());

//        rotatedLine.setEnd(ap.getCenter());
        centerCirc.setCenterX(ap.getCenter().getX());
        centerCirc.setCenterY(ap.getCenter().getY());
        
        perpLine.setStart(p1.midpoint(p2));
        perpLine.setEnd(ap.getCenter());
    }

}
