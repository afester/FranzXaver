package afester.javafx.examples.coordinates;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

class Panel extends Pane {

    private boolean isPanelAction(MouseEvent e) {
        return (e.isControlDown() && e.isShiftDown()); 
    }

    private Rectangle r;

    public Panel() {
//        setWidth(800);
//        setHeight(600);
        setLayoutX(100);
        setLayoutY(100);
        setStyle("-fx-border-color: red; -fx-border-style: solid; -fx-border-width: 1px;");

        setOnMousePressed(e -> {
            if (isPanelAction(e)) {
                System.err.println(this.getBoundsInLocal());
                System.err.println(this.getLayoutBounds());
                r.setLayoutX(this.getBoundsInLocal().getMinX());
                r.setLayoutY(this.getBoundsInLocal().getMinY());
                r.setWidth(this.getBoundsInLocal().getWidth());
                r.setHeight(this.getBoundsInLocal().getHeight());
            }
        });

        setManaged(false);  // Required to keep the size of the panel - otherwise the size is adjusted to the bounding box of its children!
        r = new Rectangle();
        r.setStroke(Color.GREEN);
        r.setFill(null);
        getChildren().add(r);
    }

    @Override
    public String toString() {
        return String.format("Panel", getId());
    }

}


class ShapeObject extends Rectangle {

    private boolean isPanelAction(MouseEvent e) {
        return (e.isControlDown() && e.isShiftDown()); 
    }

    public ShapeObject(String id, Color col, Point2D pos, int width, int height) {
        super(width, height, col);
        setLayoutX(pos.getX());
        setLayoutY(pos.getY());
        setId(id);
        
        setOnMousePressed(e -> {
            if (!isPanelAction(e)) {
                System.err.println("---------------------\n" + this);
            }
        });
    }

    @Override
    public String toString() {
        return String.format("ShapeObject[%s]", getId());
    }
}


/**
 * Simple dynamic table.
 * Uses the DynamicTable component from FranzXaver.
 */
@Example(desc = "Events",
         cat  = "JavaFX")
public class CoordinatesExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX coordinates example");

        Panel p = new Panel();
        p.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
        ShapeObject r1 = new ShapeObject("R1", Color.RED, new Point2D(100, 100), 100, 50);
        ShapeObject r2 = new ShapeObject("R2", Color.YELLOW, new Point2D(130, 130), 120, 100);
        ShapeObject r3 = new ShapeObject("R3", Color.BLUE, new Point2D(-50, -50), 120, 100);
        p.getChildren().addAll(r1, r2, r3);

        Pane rootNode = new Pane();
        rootNode.setStyle("-fx-border-color: red; -fx-border-style: solid; fx-border-width: 2px;");
        rootNode.getChildren().addAll(p);

        // show the generated scene graph
        // "If a resizable node (layout Region or Control) is set as the root, then the root's size will track the scene's size."
        Scene scene = new Scene(rootNode, 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
