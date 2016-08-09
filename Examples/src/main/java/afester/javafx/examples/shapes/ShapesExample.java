package afester.javafx.examples.shapes;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


/**
 * Simple dynamic table.
 * Uses the DynamicTable component from FranzXaver.
 */
@Example(desc = "Shapes",
         cat  = "FranzXaver")
public class ShapesExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX shapes example");

        BorderPane mainLayout = new BorderPane();
        
        Pane drawPane = new Pane();

        Rectangle rect = new Rectangle(50, 150, 100, 130);
        rect.setStroke(Color.AQUA);
        rect.setFill(Color.BISQUE);
        rect.setOnMouseClicked( e-> setSelected((EditableShape) e.getSource())  );

        Circle circ = new Circle(100, 100, 50);
        circ.setStroke(Color.RED);
        circ.setFill(null);
        circ.setOnMouseClicked( e-> setSelected((EditableShape) e.getSource())  );

        Line line = new Line(100, 100, 200, 50);
        line.setStroke(Color.GREEN);
        // Drawback: this requires a pixel-accurate selection on the Line (hence the line is hard to select)   
        line.setOnMouseClicked( e-> setSelected((EditableShape) e.getSource())  );

        Triangle tri = new Triangle(225, 80, 250, 150, 150, 150);
        tri.setStroke(Color.BLUE);
        tri.setStrokeWidth(2.0);
        tri.setFill(Color.YELLOW);
        tri.setOnMouseClicked( e-> setSelected((EditableShape) e.getSource())  );

        Arrow arrow = new Arrow(300, 100, 90);
        arrow.setStroke(Color.BLUE);
        arrow.setFill(Color.YELLOW);

        ArrowLine line2 = new ArrowLine(200, 200, 300, 300);
        line2.setStroke(Color.DARKRED);

        drawPane.getChildren().addAll(rect, circ, line, tri, arrow, line2);

        VBox controls = new VBox();
        Slider s = new Slider(0, 360, 0);
        s.valueProperty().addListener((a, b, newVal) -> {
            double angle = Math.toRadians(newVal.doubleValue());
            double x = 200 + Math.cos(angle) * 150;
            double y = 200 + Math.sin(angle + Math.PI) * 150;

            line2.setEndX(x);
            line2.setEndY(y);
            System.err.printf("%s / %s%n", x, y);
        });
        controls.getChildren().add(s);

        mainLayout.setBottom(controls);
        mainLayout.setCenter(drawPane);

        // show the generated scene graph
        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void setSelected(EditableShape es) {
        System.err.println("Selected: " + es);
    }
}
