package afester.javafx.examples.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.Buffer;

import com.example.hexdump.HexDump;

import afester.javafx.examples.Example;
import afester.javafx.tools.ColorSeparator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
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
            Image img = imageView.getImage();
            System.err.printf("%s x %s\n",  img.getHeight(), img.getWidth());
            PixelReader reader = img.getPixelReader();
            
            WritablePixelFormat<java.nio.ByteBuffer> pixelformat = WritablePixelFormat.getByteBgraInstance();
            int bufsize = (int) (img.getHeight() * img.getWidth() * 4);
            System.err.printf("Image size: %s x %s (Buffer size: %s bytes)\n",  img.getHeight(), img.getWidth(), bufsize);

            byte[]  buffer = new byte[bufsize];
            reader.getPixels(0, 0, (int) img.getHeight(), (int) img.getWidth(), pixelformat, buffer, 0, (int) img.getWidth() * 4);

            HexDump hd = new HexDump(buffer);
            hd.dumpAll(System.err);
            
            int idx = 0;
            byte[] rgb565 = new byte[(int) (img.getHeight() * img.getWidth())];
            for (int y = 0;  y < (int) img.getHeight();  y++) {
                for (int x = 0;  x < (int) img.getWidth();  x++) {
                    // [rrrrrrrr gggggggg bbbbbbbb]
                    //  ^^^^^    ^^^^^^   ^^^^^
                    // [rrrrrggg gggbbbbb]
                    byte upper = (byte) ((        buffer[idx+0] & 0xf8) |
                                         ((short) buffer[idx+1] & 0xe0) >> 5);
                    byte lower = (byte) ((((short) buffer[idx+1] & 0x1c) << 3) | 
                                         ((short) buffer[idx+2] & 0xf8) >> 3);

                    //buffer[idx];      // R
                    //buffer[idx+1];    // G
                    //buffer[idx+2];    // B
                    //buffer[idx+3];    // A
                    System.err.printf("[%s %s %s] => [%s %s]",
                            String.format("%8s", Integer.toBinaryString(buffer[idx+0] & 0xff)).replace(' ', '0'),
                            String.format("%8s", Integer.toBinaryString(buffer[idx+1] & 0xff)).replace(' ', '0'),
                            String.format("%8s", Integer.toBinaryString(buffer[idx+2] & 0xff)).replace(' ', '0'),
                            String.format("%8s", Integer.toBinaryString(upper & 0xff)).replace(' ', '0'),
                            String.format("%8s", Integer.toBinaryString(lower & 0xff)).replace(' ', '0'));
                    idx += 4;
                    return;
                }
            }
        });
        mainGroup.getChildren().add(exportButton);

        imageView = new ImageView();
        mainGroup.getChildren().add(imageView);

        Scene scene = new Scene(mainGroup);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
