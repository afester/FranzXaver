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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

    
    private ImageView screenShotView;
    private ExampleDef currentExample;
    private Stage thePrimaryStage;

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
        primaryStage.setTitle("FranzXaver - JavaFX Examples, Components, Tools");
        thePrimaryStage = primaryStage;

        // Create the launcher panel with the screenshot and the launch button
        VBox launcher = createPreviewPanel();
        
        // create the tab panel with the radio buttons to select the example
        TabPane tabPane = createSelectionPanel();

        // a horizontal box with the tab pane and the screenshot/launcher button area
        HBox mainGroup = new HBox();
        mainGroup.setPadding(new Insets(10));
        mainGroup.setSpacing(10);
        mainGroup.getChildren().addAll(tabPane, launcher);
        HBox.setHgrow(launcher, Priority.ALWAYS);

        Scene scene = new Scene(mainGroup);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    private TabPane createSelectionPanel() {
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-border-style:  solid; "
                       + "-fx-border-insets: 10px; "
                       + "-fx-border-color:  #b5b5b5");
        //tabPane.setSide(Side.LEFT);

        boolean firstTab = true;
        Map<String, List<ExampleDef>> allExamples = getExamples();
        Set<String> categories = allExamples.keySet();
        for (String category : categories) {

            Tab tab = new Tab();
            tab.setText(category);
            tabPane.getTabs().add(tab);

            final ToggleGroup group = new ToggleGroup();

            final VBox rbLayout = new VBox();
            rbLayout.setPadding(new Insets(10));
            rbLayout.setSpacing(10);
            tab.setContent(rbLayout);

            boolean first = true;
            List<ExampleDef> examples = allExamples.get(category);
            for (ExampleDef ed : examples) {

                // new radio button
                RadioButton rb = new RadioButton(ed.getDescription());
                rb.setToggleGroup(group);

                rbLayout.getChildren().add(rb);
                rb.setOnAction(e -> {
                    tab.setUserData(ed);
                    updatePreview(ed);
                });

                // Initialize the currently selected radio button in each tab.
                // TODO: This requires some refactoring ...
                if (first) {
                    rb.setSelected(true);
                    tab.setUserData(ed);
                    first = false;

                    if (firstTab) {
                        updatePreview(ed);
                        firstTab = false;
                    }
                }
            }
        }

        tabPane.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldValue, newValue) -> {
                ExampleDef ed = (ExampleDef) newValue.getUserData();
                updatePreview(ed);
            } );
        return tabPane;
    }

    
    private VBox createPreviewPanel() {
        // A vertical box with the screenshot and the launch button
        VBox launcher = new VBox();
        launcher.setSpacing(25);
        launcher.setPadding(new Insets(10));

        DropShadow borderGlow = new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.BLACK);
        borderGlow.setWidth(70);
        borderGlow.setHeight(70);

        screenShotView = new ImageView();
        screenShotView.setEffect(borderGlow);        

        launcher.setAlignment(Pos.TOP_CENTER);
        Button launchButton = new Button("Launch Example");
        launchButton.setOnAction(e -> currentExample.run());
        launcher.getChildren().addAll(screenShotView, launchButton);
        return launcher;
    }

    
    private void updatePreview(ExampleDef ed) {
        Image screenShot = ed.getScreenShot();
        screenShotView.setImage(screenShot);    // allows null
        currentExample = ed;
    
        // http://stackoverflow.com/questions/19670357/javafx-autoresize-stage-after-adding-child-to-root-parent
        thePrimaryStage.sizeToScene();
    }
}
