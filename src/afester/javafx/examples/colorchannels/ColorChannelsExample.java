package afester.javafx.examples.colorchannels;

import afester.javafx.tools.ColorSeparator;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Example for a Region border definition.
 * Just launch this class using a Java8 runtime environment.
 * No other dependencies required.
 *
 */
public class ColorChannelsExample extends Application {

	public static void main(String[] args) {
        launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
	    Image sampleImage = new Image(getClass().getResourceAsStream("sample.png"));

        ColorSeparator cs = new ColorSeparator(sampleImage);
        Image red = cs.getRedChannel();
        Image green = cs.getGreenChannel();
        Image blue = cs.getBlueChannel();
        Image blue1 = cs.getBlueChannel1();

        // JavaFX boilerplate - add the images buttons to a group and
        // setup and show the scene
        VBox mainGroup = new VBox();
        mainGroup.setPadding(new Insets(10));
        mainGroup.setSpacing(10);
        mainGroup.getChildren().addAll(
                new ImageView(sampleImage),
                new ImageView(red),
                new ImageView(green),
                new ImageView(blue),
                new ImageView(blue1));
        Scene scene = new Scene(mainGroup, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
	}

}
