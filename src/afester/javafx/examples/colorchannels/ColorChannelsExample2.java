package afester.javafx.examples.colorchannels;

import afester.javafx.tools.ColorSeparator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.effect.Blend;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Example for a Region border definition. Just launch this class using a Java8
 * runtime environment. No other dependencies required.
 *
 */
public class ColorChannelsExample2 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Image sampleImage = new Image(getClass().getResourceAsStream(
                "sample.png"));

        ColorSeparator cs = new ColorSeparator(sampleImage);
        Blend redBlend = cs.createColorBlend(Color.RED);
        Blend greenBlend = cs.createColorBlend(Color.LIME);
        Blend blueBlend = cs.createColorBlend(Color.BLUE);

        ImageView sourceView = new ImageView(sampleImage);

        ImageView redView = new ImageView(sampleImage);
        redView.setEffect(redBlend);

        ImageView greenView = new ImageView(sampleImage);
        greenView.setEffect(greenBlend);

        ImageView blueView = new ImageView(sampleImage);
        blueView.setEffect(blueBlend);

        // JavaFX boilerplate - add the images to a group and
        // setup and show the scene
        VBox mainGroup = new VBox();
        HBox colorImages = new HBox();
        colorImages.getChildren().addAll(redView, greenView, blueView);
        mainGroup.setPadding(new Insets(10));
        mainGroup.setSpacing(10);
        mainGroup.getChildren().addAll(sourceView, colorImages);
        Scene scene = new Scene(mainGroup, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
