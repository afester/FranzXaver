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

package afester.javafx.examples;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class AllExamples extends Application {


    private Map<String, List<ExampleDef>> getExamples() {
        Map<String, List<ExampleDef>> result = new HashMap<>();

        InputStream is = getClass().getResourceAsStream("examples.lst");
        BufferedReader bir = new BufferedReader(new InputStreamReader(is));

        String className = null;
        try {
            while ( (className = bir.readLine()) != null) {
                Class<?> clazz = Class.forName(className);
                Example[] annos = clazz.getAnnotationsByType(Example.class);
                if (annos.length > 0) {
                    Example exampleAnnotation = annos[0];
                    String description = exampleAnnotation.desc();
                    String category = exampleAnnotation.cat();

                    List<ExampleDef> exampleList = result.get(category);
                    if (exampleList == null) {
                        exampleList = new ArrayList<>();
                        result.put(category, exampleList);
                    }
                    exampleList.add(new ExampleDef(className, description));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox mainGroup = new VBox();
        mainGroup.setPadding(new Insets(10));
        mainGroup.setSpacing(10);
        mainGroup.setFillWidth(true);

        Map<String, List<ExampleDef>> allExamples = getExamples();
        Set<String> categories = allExamples.keySet();
        for (String category : categories) {
            mainGroup.getChildren().add(new Text(category));
            List<ExampleDef> examples = allExamples.get(category);
            for (ExampleDef ed : examples) {
                Button launcher = new Button(ed.getDescription());
                launcher.setMaxWidth(Double.MAX_VALUE);
                launcher.setOnAction(e -> ed.run());
                mainGroup.getChildren().add(launcher);
            }
        }

        Scene scene = new Scene(mainGroup);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
