package afester.javafx.examples.border;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Example for a Region border definition.
 * Just launch this class using a Java8 runtime environment.
 * No other dependencies required.
 *
 * See http://www.software-architect.net/blog/article/date/2015/11/14/9-patch-scaling-in-javafx.html
 */
public class BorderExample extends Application {

	public static void main(String[] args) {
        launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
        Region hello = new Button("Hello World");
        hello.setId("hello");

        Region hello2 = new Button("Hello World");
        hello2.setId("hello2");

        Region hello3 = new Button("Hello World");
        hello3.setId("hello3");

        // JavaFX boilerplate - add the three buttons to a group and
        // setup and show the scene
        VBox mainGroup = new VBox();
        mainGroup.setPadding(new Insets(10));
        mainGroup.setSpacing(10);
        mainGroup.getChildren().addAll(hello, hello2, hello3);
        Scene scene = new Scene(mainGroup, 800, 600);
        scene.getStylesheets().add("/afester/javafx/examples/border/panelexample.css");

        primaryStage.setScene(scene);
        primaryStage.show();
	}

}
