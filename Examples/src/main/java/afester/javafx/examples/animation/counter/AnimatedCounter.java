/*
 * Copyright 2016 Andreas Fester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package afester.javafx.examples.animation.counter;

import java.util.ArrayList;
import java.util.List;

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
        stage.setScene(new Scene(vBox, 100, 100));
        stage.setTitle("Animation Counter");
        stage.show();
        timeline.playFromStart();
    }
}
