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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import afester.javafx.components.SnapSlider;
import afester.javafx.examples.Example;
import afester.javafx.svg.GradientPolicy;
import afester.javafx.svg.SvgLoader;
import afester.javafx.svg.SvgNode;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

@Example(desc = "Using SvgLoader to render an SVG file", 
         cat  = "FranzXaver")
public class SvgLoaderResultViewer extends Application {

    // The name of the data package (to avoid hard coding of package name)
    // it seems that "../data" does not work with WebStart - 
    // an absolute path name works, though.
    private static String DATA_PACKAGE;

    {
        String pkgName = SvgLoaderResultViewer.class.getPackage().getName();
        int last = pkgName.lastIndexOf('.');
        if (last > 0) {
            pkgName = pkgName.substring(0, last);
        }
        pkgName = "/" + pkgName.replace('.',  '/') + "/data";
        DATA_PACKAGE = pkgName;
    }


    // The SVG image's top node, as returned from the SvgLoader
    private SvgNode svgNode = new SvgNode();

    /* in order to properly scale the svgImage node, we need an additional
     * intermediate node 
     */
    private Group imageLayout = new Group();

    private final HBox mainLayout = new HBox();
    private SvgLoader loader = new SvgLoader();
    private String currentFile;

    public static void main(String[] args) {
        launch(args);
    }

    public void run()  {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SVGLoader result viewer");

        ListView<String> listPanel = createListPanel();
        Pane optionsPanel = createOptionsPanel();

        VBox leftPanel = new VBox();
        leftPanel.getChildren().add(listPanel);
        leftPanel.getChildren().add(optionsPanel);

        mainLayout.getChildren().add(leftPanel);
        mainLayout.getChildren().add(imageLayout);

        listPanel.getSelectionModel().select(0);

        // show the generated scene graph
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private ListView<String> createListPanel() {

        ObservableList<String> ol = FXCollections.observableList(getTestFiles());
        ListView<String> listView = new ListView<String>(ol);
        listView.getSelectionModel().getSelectedItems().addListener(
            new ListChangeListener<String>() {

                @Override
                public void onChanged(
                        javafx.collections.ListChangeListener.Change<? extends String> change) {
                    selectFile(change.getList().get(0));
                }
            }
        );
        
        return listView;
    }

    private Pane createOptionsPanel() {
        // gradient policy
        HBox gradientPolicy = new HBox();
        gradientPolicy.setSpacing(10);
        gradientPolicy.getChildren().add(new Label("Gradient Transformation Policy:"));

        ComboBox<GradientPolicy> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(GradientPolicy.values());
        comboBox.getSelectionModel().select(GradientPolicy.USE_SUPPORTED);
        comboBox.setOnAction(e -> {
            loader.setGradientTransformPolicy(comboBox.getSelectionModel().getSelectedItem());
            selectFile(currentFile);
        }); 
        gradientPolicy.getChildren().add(comboBox);

        // show/hide viewport rectangle
        CheckBox showViewport = new CheckBox("Show Viewport");
        showViewport.setOnAction(e -> {
            loader.setAddViewboxRect(showViewport.isSelected());
            selectFile(currentFile);
        } );

        // Zoom
        SnapSlider zoomSlider = new SnapSlider(0.25, 2.0, 1.0);
        zoomSlider.setShowTickLabels(true);
        zoomSlider.setShowTickMarks(true);
        zoomSlider.setMajorTickUnit(0.25);
        zoomSlider.setMinorTickCount(0);
        zoomSlider.setLabelFormatter(new StringConverter<Double>() {

            @Override
            public String toString(Double value) {
                return String.format("%d %%", (int) (value * 100));
            }

            @Override
            public Double fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });

        zoomSlider.finalValueProperty().addListener( (val, oldVal, newVal) -> {
            setScale((Double) newVal);
        });

        HBox zoomControl = new HBox();
        zoomControl.setSpacing(10);
        zoomControl.getChildren().add(new Label("Zoom:"));
        zoomControl.getChildren().add(zoomSlider);
        HBox.setHgrow(zoomSlider, Priority.ALWAYS);

        // build control panel
        VBox controlPanel = new VBox();
        controlPanel.setSpacing(10);
        controlPanel.setPadding(new Insets(10, 10, 10, 10));
        controlPanel.getChildren().add(gradientPolicy);
        controlPanel.getChildren().add(showViewport);
        controlPanel.getChildren().add(zoomControl);
        
        return controlPanel;
    }

    private void setScale(Double scale) {
        svgNode.setScaleX(scale);
        svgNode.setScaleY(scale);
    }


    private void selectFile(String fileName) {
        currentFile = fileName;
        InputStream svgFile = 
                getClass().getResourceAsStream(DATA_PACKAGE + "/" + fileName);

        imageLayout.getChildren().remove(svgNode);
        svgNode.stopAnimations();
        svgNode = loader.loadSvg(svgFile);
        svgNode.startAnimations();
        imageLayout.getChildren().add(svgNode);
    }


    private List<String> getTestFiles() {
        List<String> result = new ArrayList<>();

        InputStream dataLst = 
                getClass().getResourceAsStream(DATA_PACKAGE + "/data.lst");
        BufferedReader dataFile = new BufferedReader(new InputStreamReader(dataLst));
        String fileName = null;
        try {
            while ( (fileName = dataFile.readLine()) != null) {
                result.add(fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
