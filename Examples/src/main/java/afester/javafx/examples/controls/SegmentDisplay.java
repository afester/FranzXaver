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

package afester.javafx.examples.controls;

import afester.javafx.components.MultiSegmentPanel;
import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

@Example(desc = "Multi segment display panel", 
         cat  = "FranzXaver")
public class SegmentDisplay extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {

        MultiSegmentPanel s7Panel = new MultiSegmentPanel("14segment", 10, 0);
        s7Panel.setId("titlePanel");
        s7Panel.setOffColor(  new Color(0x1c / 255.0, 0x23 / 255.0, 0x23 / 255.0, 1.0));
        s7Panel.setOnColor(   new Color(0x8E / 255.0, 0xd2 / 255.0, 0xd5 / 255.0, 1.0));
        s7Panel.setPanelColor(new Color(0x17 / 255.0, 0x1B / 255.0, 0x1A / 255.0, 1.0));
        s7Panel.setText("FranzXaver");

        
        // slider position value panel example 
        MultiSegmentPanel panel2 = new MultiSegmentPanel("7segment", 5, 3);
        panel2.setId("sliderPanel");
        panel2.getTransforms().add(new Scale(0.5, 0.5));
        panel2.setValue(125);

        Slider s2 = new Slider(0.0, 12, 0.0);
        s2.valueProperty().addListener((obs, oldValue, newValue) -> {
            double val = newValue.doubleValue();
            if (val > 10) {
                panel2.setText("Err");
            } else {
                panel2.setValue(val);
            }
        } );

        HBox valueSlider = new HBox();
        valueSlider.setSpacing(20);
        valueSlider.getChildren().addAll(new Group(panel2), s2);
        HBox.setHgrow(s2, Priority.ALWAYS);


        // text input sample panel
        MultiSegmentPanel panel3 = new MultiSegmentPanel("14segment", 10);
        panel3.setId("textPanel");
        panel3.getTransforms().add(new Scale(0.5, 0.5));

        TextField textField = new TextField();
        textField.textProperty().addListener((obs, oldvalue, newvalue) -> {
            panel3.setText(newvalue);
        });

        HBox textInput = new HBox();
        textInput.setSpacing(20);
        textInput.getChildren().addAll(new Group(panel3), textField);
        HBox.setHgrow(textField, Priority.ALWAYS);


        VBox mainGroup = new VBox();
        mainGroup.setSpacing(10);
        mainGroup.getChildren().addAll(s7Panel, valueSlider, textInput);

        mainGroup.setPadding(new Insets(10, 10, 10, 10));

        Scene scene = new Scene(mainGroup);
        scene.getStylesheets().add(
                "/afester/javafx/examples/components/sevensegment/segmentdisplay.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
