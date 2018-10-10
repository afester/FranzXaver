/*
 * Copyright 2017 Andreas Fester
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

package afester.javafx.examples.ttf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



enum ReductionResult implements RadioButtonValues {
    COL256("256", 256),
    COL16("16", 16),
    COL2("2", 2);


    private String label;
    private int depth;

    ReductionResult(String label, int depth) {
        this.label = label;
        this.depth = depth;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public int getDepth() {
        return depth;
    }
}


/**
 * Example for loading a custom true type font
 */
@Example(desc = "Custom True Type Font",
         cat  = "Basic JavaFX")
public class ColorReducer extends Application {
 
    private ImageView originalImage = new ImageView();
    private RadioButtonGroup<ReductionResult> reduction;
    private ImageView processedImage = new ImageView();
    private ColorPalette originalPalette;
    private Button convertButton = new Button("Convert");
    private PaletteView originalPaletteView = new PaletteView();
    private ColorPalette modifiedPalette;
    private PaletteView modifiedPaletteView = new PaletteView();
    private Stage theStage;
    private Scene scene;

    public static void main(String[] args) {
        // The following properties are required on Linux for improved text rendering
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        launch(args);
    }

    public void run() {
        start(new Stage());
    }
    
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("Color Reducer");

        GridPane gridPane = new GridPane();

        Button loadButton = new Button("Load...");
        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Image ...");
            File theFile = fileChooser.showOpenDialog(stage);
            if (theFile != null) {
                loadImage(theFile);
            }
        });

        VBox vbox = new VBox();
        reduction = new RadioButtonGroup<ReductionResult>(ReductionResult.values());
        reduction.selectedValueProperty().setValue(ReductionResult.COL256);
        convertButton.setDisable(true);
        convertButton.setOnAction(e -> doConversion()); 
        vbox.getChildren().addAll(reduction, convertButton);

        gridPane.add(loadButton, 0, 0);
        gridPane.add(originalImage, 0, 1);
        gridPane.add(vbox, 1, 1);
        gridPane.add(processedImage, 2, 1);
        gridPane.add(originalPaletteView, 0, 2);
        gridPane.add(modifiedPaletteView, 2, 2);

        Scene scene  = new Scene(gridPane);

        this.theStage = stage;
        stage.setScene(scene);
        stage.show();
        
        loadImage(new File("C:\\Users\\AFESTER\\Documents\\Andreas\\Bilder\\profile.jpg"));

    }

    private void loadImage(File theFile) {
        try {
            InputStream is = new FileInputStream(theFile);

            Image img = new Image(is);
            originalImage.setImage(img);

            originalPalette = new ColorPalette();
            originalPalette.addColors(img);
            originalPaletteView.setPalette(originalPalette);

            convertButton.setDisable(false);

            theStage.sizeToScene();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void doConversion() {
        System.err.println(reduction.getSelectedValue());

        // Create a converted image from the original image
        MedianCut mc = new MedianCut();
        Image convertedImage = mc.medianCut(originalImage.getImage(), reduction.getSelectedValue().getDepth());

        processedImage.setImage(convertedImage);

        modifiedPalette = new ColorPalette();
        modifiedPalette.addColors(convertedImage);
        modifiedPaletteView.setPalette(modifiedPalette);

        theStage.sizeToScene();
    }
}
