package afester.javafx.examples.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import afester.javafx.examples.Example;
import afester.javafx.tools.ColorSeparator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


/**
 * Example which shows how to read an image from a file (like .png or .jpg)
 * and how to access the frame buffer for that image  
 * Just launch this class using a Java8 runtime environment. 
 * No other dependencies required.
 */
@Example(desc = "Image converter",
         cat  = "FranzXaver")
public class ImageConverter extends Application {

    
    private ImageView imageView;
    
    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
       
        VBox mainGroup = new VBox();

        Button loadButton = new Button("Load...");
        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image file ...");
            File theFile = fileChooser.showOpenDialog(primaryStage);
            if (theFile != null) {
                try {
                    FileInputStream input = new FileInputStream(theFile.getAbsolutePath());
                    imageView.setImage(new Image(input));
                    primaryStage.sizeToScene();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                
            }
        });
        mainGroup.getChildren().add(loadButton);

        Button exportButton = new Button("Export ...");
        exportButton.setOnAction(e -> {
            System.err.printf("%s x %s",  img.getHeight(), img.getWidth());
            Image img = imageView.getImage();
            PixelReader reader = img.getPixelReader();
            reader.getPixels(x, y, w, h, pixelformat, buffer, offset, scanlineStride);
            
        });
        mainGroup.getChildren().add(exportButton);

        imageView = new ImageView();
        mainGroup.getChildren().add(imageView);

        Scene scene = new Scene(mainGroup);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
