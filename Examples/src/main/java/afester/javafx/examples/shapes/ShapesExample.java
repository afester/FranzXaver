package afester.javafx.examples.shapes;

import afester.javafx.components.GraphicalComboBox;
import afester.javafx.examples.Example;
import afester.javafx.shapes.Arrow;
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
import javafx.scene.Scene;
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

        Arrow arrow = new Arrow(300, 100, 90, new ArrowStyle(ArrowShape.CLOSED, 20, 30));
        arrow.setStroke(Color.BLUE);
        arrow.setFill(Color.YELLOW);

        ArrowStraightLine line2 = new ArrowStraightLine(200, 200, 300, 300);
        //ArrowCurvedLine line2 = new ArrowCurvedLine(200, 200, 300, 300);
        //line2.setStroke(Color.BLUE);
        // line2.setFill(Color.BLACK);

        final Point2D center = new Point2D(200, 200);
        Circle centerCirc = new Circle(center.getX(), center.getY(), 3);
        centerCirc.setStroke(Color.RED);
        drawPane.getChildren().addAll(rect, circ, line, tri, arrow, line2, centerCirc);

        VBox bottomPanel = new VBox();

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
        bottomPanel.getChildren().add(s);

        ComboBox<ArrowShape> startShape = new GraphicalComboBox<>(item -> {
            final ArrowStraightLine startLine =  new ArrowStraightLine(0, 0, 20, 0);
            startLine.setStartArrow(new ArrowStyle(item, 10, 30));
            startLine.setEndArrow(new ArrowStyle(ArrowShape.NONE, 10, 30));
            return startLine;
        });
        startShape.getItems().addAll(ArrowShape.values());
        startShape.getSelectionModel().selectedItemProperty().addListener(
                (source, oldItem, newItem) -> line2.setStartArrow(new ArrowStyle(newItem, 20, 30)) );

        ComboBox<LineDash> lineStyle = new GraphicalComboBox<>(item -> {
            final Line lineDash =  new Line(0, 0, 50, 0);
            lineDash.getStrokeDashArray().setAll(item.getDashArray());
            return lineDash;
        });
        lineStyle.getItems().addAll(LineDash.values());
        lineStyle.getSelectionModel().selectedItemProperty().addListener(
                (source, oldItem, newItem) -> line2.getStrokeDashArray().setAll(newItem.getDashArray()) );

        ComboBox<ArrowShape> endShape = new GraphicalComboBox<>(item -> {
            final ArrowStraightLine startLine =  new ArrowStraightLine(0, 0, 20, 0);
            startLine.setStartArrow(new ArrowStyle(ArrowShape.NONE, 10, 30));
            startLine.setEndArrow(new ArrowStyle(item, 10, 30));
            return startLine;
        });
        endShape.getItems().addAll(ArrowShape.values());
        endShape.getSelectionModel().selectedItemProperty().addListener(
                (source, oldItem, newItem) -> line2.setEndArrow(new ArrowStyle(newItem, 20, 30)) );

        ComboBox<Double> lineWidth = new GraphicalComboBox<>(item -> {
            final Line lineView=  new Line(0, 0, 50, 0);
            lineView.setStrokeWidth(item);
            return lineView;
        });
        lineWidth.getItems().addAll(1.0, 3.0, 5.0, 7.0, 10.0);
        lineWidth.getSelectionModel().selectedItemProperty().addListener(
                (source, oldItem, newItem) -> line2.setStrokeWidth(newItem));

        controls.getChildren().add(startShape);
        controls.getChildren().add(lineStyle);
        controls.getChildren().add(endShape);
        controls.getChildren().add(lineWidth);
        bottomPanel.getChildren().add(controls);

        mainLayout.setBottom(bottomPanel);
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
