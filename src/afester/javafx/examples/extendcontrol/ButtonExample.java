package afester.javafx.examples.extendcontrol;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * Example for a Region border definition. Just launch this class using a Java8
 * runtime environment. No other dependencies required.
 *
 * See http://www.software-architect.net/blog/article/date/2015/11/14/9-patch-scaling-in-javafx.html
 */
public class ButtonExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button hello = new QButton("Hello");
        Button hello2 = new QButton("World");

        // JavaFX boilerplate - add the three buttons to a group and
        // setup and show the scene
        VBox mainGroup = new VBox();
        mainGroup.getChildren().addAll(hello, hello2);
        Scene scene = new Scene(mainGroup, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void start2(Stage primaryStage) throws Exception {
        Button hello = new QButton("Hello World");

        // JavaFX boilerplate - add the three buttons to a group and
        // setup and show the scene
        Group mainGroup = new Group();
        mainGroup.getChildren().add(hello);
        Scene scene = new Scene(mainGroup, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
