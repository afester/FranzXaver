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

package afester.javafx.examples.components.meter;

import afester.javafx.components.Meter;
import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


@Example(desc = "Analog meter", 
         cat  = "FranzXaver")
public class MeterSample extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    
    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        
        Meter theMeter = new Meter();
        theMeter.setUnitText("V");

        Slider valueSlider = new Slider(0.0, 1.5, 0.0);
        valueSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            double val = newValue.doubleValue();
            if (val > 1.0) {
                theMeter.setValue(1.0);
            } else {
                theMeter.setValue(val);
            }
        } );

        VBox mainGroup = new VBox();
        mainGroup.getChildren().addAll(theMeter, valueSlider);
        mainGroup.setSpacing(10);
        mainGroup.setPadding(new Insets(10, 10, 10, 10));

        Scene scene = new Scene(mainGroup);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
