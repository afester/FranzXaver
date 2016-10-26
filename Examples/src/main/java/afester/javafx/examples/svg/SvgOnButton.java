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

package afester.javafx.examples.svg;

import afester.javafx.examples.Example;
import afester.javafx.svg.SvgLoader;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.InputStream;

@Example(desc = "Using SvgLoader to render an image for a button",
         cat  = "FranzXaver")
public class SvgOnButton extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SVG Display sample");

        // load the sample svg file
        InputStream svgFile = 
              getClass().getResourceAsStream("/afester/javafx/examples/data/Ghostscript_Tiger.svg");
        SvgLoader loader = new SvgLoader();
        Group svgImage = loader.loadSvg(svgFile);

        // Scale the image and wrap it in a Group to make the button 
        // properly scale to the size of the image  
        svgImage.setScaleX(0.1);
        svgImage.setScaleY(0.1);
        Group graphic = new Group(svgImage);

        Button button = new Button();
        button.setGraphic(graphic);

        HBox layout = new HBox(button);
        HBox.setMargin(button, new Insets(10));

        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
