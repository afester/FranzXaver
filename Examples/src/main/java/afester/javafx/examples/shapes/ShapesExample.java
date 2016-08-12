package afester.javafx.examples.shapes;

import afester.javafx.components.GraphicalComboBox;
import afester.javafx.examples.Example;
import afester.javafx.shapes.Arrow;
import afester.javafx.shapes.ArrowCurvedLine;
import afester.javafx.shapes.ArrowLine;
import afester.javafx.shapes.ArrowShape;
import afester.javafx.shapes.ArrowStraightLine;
import afester.javafx.shapes.ArrowStyle;
import afester.javafx.shapes.Circle;
import afester.javafx.shapes.EditableShape;
import afester.javafx.shapes.Line;
import afester.javafx.shapes.LineDash;
import afester.javafx.shapes.Triangle;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

        Arrow arrow = new Arrow(300, 100, 90, new ArrowStyle(ArrowShape.CLOSED, 20, 30, false));
        arrow.setStroke(Color.BLUE);
        arrow.setFill(Color.YELLOW);

        ArrowStraightLine line2 = new ArrowStraightLine(200, 200, 300, 300);

        final Point2D center = new Point2D(200, 200);
        Circle centerCirc = new Circle(center.getX(), center.getY(), 3);
        centerCirc.setStroke(Color.RED);

        updateLine(line2, center, 45);

        ArrowCurvedLine line3 = new ArrowCurvedLine(400, 200, 300, 300);

        final Point2D center2 = new Point2D(400, 200);
        Circle centerCirc2 = new Circle(center2.getX(), center2.getY(), 3);
        centerCirc2.setStroke(Color.RED);

        updateLine(line3, center2, 45);

        drawPane.getChildren().addAll(rect, circ, line, tri, arrow, 
                                      line2, centerCirc, 
                                      line3, centerCirc2);

        VBox bottomPanel = new VBox();

        HBox controls = new HBox();
        Slider s = new Slider(0, 360, 45);
        s.valueProperty().addListener((a, b, newVal) -> {
            updateLine(line2, center, newVal.doubleValue());
            updateLine(line3, center2, newVal.doubleValue());
        });
        bottomPanel.getChildren().add(s);

        final ArrowStyle[] allStyles = {
                null,
                new ArrowStyle(ArrowShape.OPEN, 10, 30, false),
                new ArrowStyle(ArrowShape.CLOSED, 10, 30, false),
                new ArrowStyle(ArrowShape.TAILED, 10, 30, false),
                new ArrowStyle(ArrowShape.DIAMOND, 10, 30, false),
                new ArrowStyle(ArrowShape.CLOSED, 10, 30, true),
                new ArrowStyle(ArrowShape.TAILED, 10, 30, true),
                new ArrowStyle(ArrowShape.DIAMOND, 10, 30, true),
                new ArrowStyle(ArrowShape.OPEN, 10, 45, false),
                new ArrowStyle(ArrowShape.CLOSED, 10, 45, false),
                new ArrowStyle(ArrowShape.TAILED, 10, 45, false),
                new ArrowStyle(ArrowShape.CLOSED, 10, 45, true),
                new ArrowStyle(ArrowShape.TAILED, 10, 45, true),
                new ArrowStyle(ArrowShape.DIAMOND, 10, 45, true),
                new ArrowStyle(ArrowShape.OPEN, 10, 90, false),
                new ArrowStyle(ArrowShape.CLOSED, 10, 90, false),
                new ArrowStyle(ArrowShape.TAILED, 10, 90, false),
                new ArrowStyle(ArrowShape.CLOSED, 10, 90, true),
                new ArrowStyle(ArrowShape.TAILED, 10, 90, true),
        };
        ComboBox<ArrowStyle> startShape = new GraphicalComboBox<>(item -> {
            final ArrowStraightLine startLine =  new ArrowStraightLine(0, 0, 20, 0);
            startLine.setStartArrow(item);
            startLine.setEndArrow(null);
            return startLine;
        });
        startShape.getItems().addAll(allStyles);
        startShape.getSelectionModel().selectedItemProperty().addListener(
                (source, oldItem, newItem) -> {
                    line2.setStartArrow(newItem) ;   
                    line3.setStartArrow(newItem) ;
                });

        ComboBox<LineDash> lineStyle = new GraphicalComboBox<>(item -> {
            final Line lineDash =  new Line(0, 0, 50, 0);
            lineDash.getStrokeDashArray().setAll(item.getDashArray());
            return lineDash;
        });
        lineStyle.getItems().addAll(LineDash.values());
        lineStyle.getSelectionModel().selectedItemProperty().addListener(
                (source, oldItem, newItem) -> 
                {
                    line2.getStrokeDashArray().setAll(newItem.getDashArray()); 
                    line3.getStrokeDashArray().setAll(newItem.getDashArray()); 
                } );

        ComboBox<ArrowShape> endShape = new GraphicalComboBox<>(item -> {
            final ArrowStraightLine startLine =  new ArrowStraightLine(0, 0, 20, 0);
            startLine.setStartArrow(null); // new ArrowStyle(ArrowShape.NONE, 10, 30, false));
            startLine.setEndArrow(new ArrowStyle(item, 10, 30, false));
            return startLine;
        });
        endShape.getItems().addAll(ArrowShape.values());
        endShape.getSelectionModel().selectedItemProperty().addListener(
                (source, oldItem, newItem) ->{
                    line2.setEndArrow(new ArrowStyle(newItem, 20, 30, false)); 
                    line3.setEndArrow(new ArrowStyle(newItem, 20, 30, false));
               } );

        ComboBox<Double> lineWidth = new GraphicalComboBox<>(item -> {
            final Line lineView=  new Line(0, 0, 50, 0);
            lineView.setStrokeWidth(item);
            return lineView;
        });
        lineWidth.getItems().addAll(1.0, 3.0, 5.0, 7.0, 10.0);
        lineWidth.getSelectionModel().selectedItemProperty().addListener(
                (source, oldItem, newItem) -> {
                    line2.setStrokeWidth(newItem);
                    line3.setStrokeWidth(newItem);
                });

        CheckBox isFilled = new CheckBox("Filled");
        isFilled.selectedProperty().addListener(
                (source, oldValue, newValue) -> System.err.println("Filled: " + newValue) ); 

        controls.getChildren().add(startShape);
        controls.getChildren().add(lineStyle);
        controls.getChildren().add(endShape);
        controls.getChildren().add(lineWidth);
        controls.getChildren().add(isFilled);
        bottomPanel.getChildren().add(controls);

        mainLayout.setBottom(bottomPanel);
        mainLayout.setCenter(drawPane);

        // show the generated scene graph
        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void updateLine(ArrowLine line, Point2D center, double newVal) {
        double angle = Math.toRadians(newVal);
        double x1 = Math.cos(angle) * 75;
        double y1 = Math.sin(angle + Math.PI) * 75;
        double x2 = Math.cos(angle) * 75;
        double y2 = Math.sin(angle + Math.PI) * 75;

        if (line instanceof ArrowCurvedLine) {
            if (angle >= Math.PI/4 && angle <= (Math.PI/2 + Math.PI/4)) {
                ((ArrowCurvedLine) line).setDirection(Orientation.VERTICAL);
            } else {
                ((ArrowCurvedLine) line).setDirection(Orientation.HORIZONTAL);
            }
        }

        line.setStartX(center.getX() + x1);
        line.setStartY(center.getY() + y1);
        line.setEndX(center.getX() - x2);
        line.setEndY(center.getY() - y2);
    }

    private void setSelected(EditableShape es) {
        System.err.println("Selected: " + es);
    }
}
