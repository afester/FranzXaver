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

import afester.javafx.examples.Example;
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
 * Example which shows how to separate color channels of an image. 
 * Just launch this class using a Java8 runtime environment. 
 * No other dependencies required.
 */
@Example(desc = "Color channel separation",
         cat  = "FranzXaver")
public class ColorChannelsExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        Image sampleImage = new Image(getClass().getResourceAsStream("sample.png"));

        ColorSeparator cs = new ColorSeparator(sampleImage);
        Image red = cs.getRedChannel();
        Image green = cs.getGreenChannel();
        Image blue = cs.getBlueChannel();

        // JavaFX boilerplate - add the images to a group and
        // setup and show the scene
        HBox colorImages = new HBox();
        colorImages.getChildren().addAll(
                new ImageView(red),
                new ImageView(green),
                new ImageView(blue));

        Image blue1 = cs.getBlueChannel1();
        VBox mainGroup = new VBox();
        mainGroup.setPadding(new Insets(10));
        mainGroup.setSpacing(10);
        mainGroup.getChildren().addAll(
                new ImageView(sampleImage), 
                colorImages,
                new ImageView(blue1));
        Scene scene = new Scene(mainGroup);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
