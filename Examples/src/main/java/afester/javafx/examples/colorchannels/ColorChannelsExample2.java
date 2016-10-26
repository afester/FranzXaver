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

        ImageView redView = new ImageView(sampleImage);
        redView.setEffect(redBlend);

        ImageView greenView = new ImageView(sampleImage);
        greenView.setEffect(greenBlend);

        ImageView blueView = new ImageView(sampleImage);
        blueView.setEffect(blueBlend);

        HBox colorImages = new HBox();
        colorImages.getChildren().addAll(redView, greenView, blueView);

        ImageView sourceView = new ImageView(sampleImage);

        // JavaFX boilerplate - add the images to a group and
        // setup and show the scene
        VBox mainGroup = new VBox();
        mainGroup.setPadding(new Insets(10));
        mainGroup.setSpacing(10);
        mainGroup.getChildren().addAll(sourceView, colorImages);
        Scene scene = new Scene(mainGroup, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
