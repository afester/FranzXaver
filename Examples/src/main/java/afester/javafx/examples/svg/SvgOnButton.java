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
        final SvgLoader loader = new SvgLoader();

//##############
        // load first svg file
        InputStream svgFile = getClass().getResourceAsStream(
                        "/afester/javafx/examples/data/Ghostscript_Tiger.svg");
        Group svgImage = loader.loadSvg(svgFile);

        // Scale the image and wrap it in a Group to make the button 
        // properly scale to the size of the image  
        svgImage.setScaleX(0.1);
        svgImage.setScaleY(0.1);
        Group graphic1 = new Group(svgImage);

        Button button1 = new Button();
        HBox.setMargin(button1, new Insets(10));
        button1.setGraphic(graphic1);
//##############
        // load second svg file
        svgFile = getClass().getResourceAsStream(
                        "/afester/javafx/examples/data/ellipse.svg");
        svgImage = loader.loadSvg(svgFile);

        // Scale the image and wrap it in a Group to make the button 
        // properly scale to the size of the image  
        svgImage.setScaleX(0.6);
        svgImage.setScaleY(0.6);
        Group graphic2 = new Group(svgImage);

        Button button2 = new Button();
        HBox.setMargin(button2, new Insets(10));
        button2.setGraphic(graphic2);
//###############

        // Add all buttons to a horizontal box as the main layout
        HBox layout = new HBox(button1, button2);

        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
