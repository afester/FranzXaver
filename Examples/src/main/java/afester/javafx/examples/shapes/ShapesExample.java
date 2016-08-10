package afester.javafx.examples.shapes;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Callback;


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

        Arrow arrow = new Arrow(300, 100, 90, ArrowStyle.CLOSED, 20, 30);
        arrow.setStroke(Color.BLUE);
        arrow.setFill(Color.YELLOW);

        ArrowLine line2 = new ArrowLine(200, 200, 300, 300);
        //ArrowCurvedLine line2 = new ArrowCurvedLine(200, 200, 300, 300);
        line2.setStroke(Color.BLUE);
        // line2.setFill(Color.BLACK);

        final Point2D center = new Point2D(200, 200);
        Circle centerCirc = new Circle(center.getX(), center.getY(), 3);
        centerCirc.setStroke(Color.RED);
        drawPane.getChildren().addAll(rect, circ, line, tri, arrow, line2, centerCirc);

        HBox controls = new HBox();
        Slider s = new Slider(0, 360, 0);
        s.valueProperty().addListener((a, b, newVal) -> {
            double angle = Math.toRadians(newVal.doubleValue());
            double x1 = center.getX() + Math.cos(angle) * 75;
            double y1 = center.getY() + Math.sin(angle + Math.PI) * 75;
            double x2 = center.getX() - Math.cos(angle) * 75;
            double y2 = center.getY() - Math.sin(angle + Math.PI) * 75;

            if (angle >= Math.PI/4 && angle <= (Math.PI/2 + Math.PI/4)) {
            //    line2.setDirection(Orientation.VERTICAL);
            } else {
            //    line2.setDirection(Orientation.HORIZONTAL);
            }
            line2.setStartX(x1);
            line2.setStartY(y1);
            line2.setEndX(x2);
            line2.setEndY(y2);
        });
        controls.getChildren().add(s);
        
        //ComboBox<String> styleBox = new ComboBox<>();
        
        ComboBox<ArrowStyle> startShape = new ComboBox<>();
        startShape.getItems().addAll(ArrowStyle.values());

        startShape.setCellFactory(new Callback<ListView<ArrowStyle>, ListCell<ArrowStyle>>() {
            @Override public ListCell<ArrowStyle> call(ListView<ArrowStyle> p) {
                return new ListCell<ArrowStyle>() {

                    private final ArrowLine content;

                    { 
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY); 
                        content = new ArrowLine(0, 10, 30, 10);
                    }
                    
                    @Override protected void updateItem(ArrowStyle item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            content.setStartArrow(item, 10, 30);
                            content.setEndArrow(ArrowStyle.NONE, 10, 30);
                            setGraphic(content);
                        }
                   }
              };
          }
       });
       
        ComboBox<ArrowStyle> endShape = new ComboBox<>();
        endShape.getItems().addAll(ArrowStyle.values());

        endShape.setCellFactory(new Callback<ListView<ArrowStyle>, ListCell<ArrowStyle>>() {
            @Override public ListCell<ArrowStyle> call(ListView<ArrowStyle> p) {
                return new ListCell<ArrowStyle>() {

                    private final ArrowLine content;

                    { 
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY); 
                        content = new ArrowLine(0, 10, 30, 10);
                    }
                    
                    @Override protected void updateItem(ArrowStyle item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            content.setStartArrow(ArrowStyle.NONE, 10, 30);
                            content.setEndArrow(item, 10, 30);
                            setGraphic(content);
                        }
                   }
              };
          }
       });
       
        
        
        
        
        
        //styleBox.getItems().addAll("solid", "dashed", "dotted");
        controls.getChildren().add(startShape);
        controls.getChildren().add(new ComboBox<>());
        controls.getChildren().add(endShape);

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
