package afester.javafx.examples.extendcontrol;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * Example for an extended Button control.
 * Just launch this class using a Java8 runtime environment. No other dependencies required.
 *
 * See ...
 */
public class ButtonExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button b1 = new QButton("Hello");
        Button b2 = new QButton("World");

        // JavaFX boilerplate - add the two buttons to a group and
        // setup and show the scene
        VBox mainGroup = new VBox();
        mainGroup.getChildren().addAll(b1, b2);
        Scene scene = new Scene(mainGroup, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
