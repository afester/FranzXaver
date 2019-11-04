package afester.javafx.examples.controls;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


@Example(desc = "ScrollPane",
         cat  = "JavaFX")
public class DrawingAreaExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX DrawingArea example");

        DrawingArea da = new DrawingArea();

        Menu viewMenu = new Menu("View");
        MenuItem viewItem1 = new MenuItem("Center");
        viewItem1.setOnAction(e -> da.centerContent());
        MenuItem viewItem2 = new MenuItem("Fit to Window");
        viewItem2.setOnAction(e -> da.fitContentToWindow());
        viewMenu.getItems().addAll(viewItem1, viewItem2);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(viewMenu);
        
        RectangleObject r1 = new RectangleObject("R1", Color.RED, new Point2D(-50, -50), 100, 30);
        RectangleObject r2 = new RectangleObject("R2", Color.BLUE, new Point2D(0, 0), 40, 40);
        RectangleObject r3 = new RectangleObject("R3", Color.GREEN, new Point2D(-50, 100), 50, 50);
//        RectangleObject r1 = new RectangleObject("R1", Color.RED, new Point2D(100, 100), 50, 50);
//        RectangleObject r2 = new RectangleObject("R2", Color.BLUE, new Point2D(20, 120), 50, 50);
//        RectangleObject r3 = new RectangleObject("R3", Color.GREEN, new Point2D(150, 150), 50, 50);
        RectangleObject r4 = new RectangleObject("R4", Color.ORANGE, new Point2D(130, 100), 50, 50);

        da.getPaper().getChildren().addAll(r1, r2, r3, r4);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(menuBar);
        mainLayout.setCenter(da);

        Scene scene = new Scene(mainLayout, 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
