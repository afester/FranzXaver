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

package afester.javafx.examples.border;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Example for a Region border definition. Just launch this class using a Java8
 * runtime environment. No other dependencies required.
 * See http://www.software-architect.net/blog/article/date/2015/11/14/9-patch-scaling-in-javafx.html
 */
@Example(desc = "Border definition of a Region",
         cat  = "Basic JavaFX")
public class BorderExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        Region hello = new Button("Hello World");
        hello.setId("hello");

        Region hello2 = new Button("Hello World");
        hello2.setId("hello2");

        Region hello3 = new Button("Hello World");
        hello3.setId("hello3");

        // JavaFX boilerplate - add the three buttons to a group and
        // setup and show the scene
        VBox mainGroup = new VBox();
        mainGroup.setPadding(new Insets(10));
        mainGroup.setSpacing(10);
        mainGroup.getChildren().addAll(hello, hello2, hello3);
        Scene scene = new Scene(mainGroup);

        URL styleSheet = getClass().getResource("panelexample.css");
        scene.getStylesheets().add(styleSheet.toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
