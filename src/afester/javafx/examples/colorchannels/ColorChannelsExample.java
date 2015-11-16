package afester.javafx.examples.colorchannels;

import afester.javafx.tools.ColorSeparator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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

        // JavaFX boilerplate - add the images to a group and
        // setup and show the scene
        VBox mainGroup = new VBox();
        HBox colorImages = new HBox();
        colorImages.getChildren().addAll(
                new ImageView(red),
                new ImageView(green),
                new ImageView(blue));
        mainGroup.setPadding(new Insets(10));
        mainGroup.setSpacing(10);
        mainGroup.getChildren().addAll(
                new ImageView(sampleImage),
                colorImages,
                new ImageView(blue1));
        Scene scene = new Scene(mainGroup, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
	}

}
