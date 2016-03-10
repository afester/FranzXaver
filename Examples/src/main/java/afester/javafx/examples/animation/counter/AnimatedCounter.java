package afester.javafx.examples.animation.counter;

import afester.javafx.examples.Example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;


@Example(desc = "Animating a sequence of bitmap images", 
         cat  = "Basic JavaFX")
public class AnimatedCounter extends Application {
    private int idx = 0;

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    private static final String[] IMAGE_NAMES = 
        {"1.png", "2.png", "3.png", "4.png", "5.png"}; 

    @Override
    public void start(Stage stage) {
        // create a list of Image objects to animate
        final List<Image> imageList = new ArrayList<>();
        for (String imageName : IMAGE_NAMES) {
            imageList.add(new Image(getClass().getResourceAsStream(imageName)));
        }

        // create the container for the image
        final VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        final ImageView imageView = new ImageView();
        vBox.getChildren().add(imageView);

        // setup a frame handler
        Duration sec = new Duration(250);
        EventHandler<ActionEvent> image = new  EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                imageView.setImage(imageList.get(idx++));
                if (idx >= imageList.size()) {
                    idx = 0;
                }
            }
        };

        KeyFrame keyFrame = new KeyFrame(sec, image);

        // Build the time line animation.
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);

        // Set the stage and show and play the animation
        stage.setScene(new Scene(vBox, 250, 300));
        stage.setTitle("Animation Counter");
        stage.show();
        timeline.playFromStart();
    }
}
