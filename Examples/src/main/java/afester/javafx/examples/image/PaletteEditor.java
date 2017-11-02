package afester.javafx.examples.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

//import com.example.hexdump.HexDump;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
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
public class PaletteEditor extends Application {

    
    private ImageView imageView = new ImageView();
    private VBox mainGroup = new VBox();
    private Stage primaryStage;
    private VBox indexedImages = new VBox();

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage ps) {
       
    	this.primaryStage = ps;

        Button loadButton = new Button("Load...");
        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image file ...");
            File theFile = fileChooser.showOpenDialog(primaryStage);
            if (theFile != null) {
                try {
                	loadImage(theFile.getAbsolutePath());
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                
            }
        });
        mainGroup.getChildren().add(loadButton);

        HBox images = new HBox();
        images.getChildren().add(imageView);
        images.getChildren().add(indexedImages);

        mainGroup.getChildren().add(images);

        Scene scene = new Scene(mainGroup);

        addIndexedImage();
        addIndexedImage();
        addIndexedImage();

        primaryStage.setScene(scene);
        primaryStage.show();

        try {
			loadImage("C:\\temp\\test.png");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
    }


    private void addIndexedImage() {
//        imageView.getImage().;
//        new Image()
        ImageView indexedImage = new ImageView(imageView.getImage());
        indexedImages.getChildren().add(indexedImage);
    }

    private void loadImage(String filePath) throws FileNotFoundException {
        Path p = Paths.get(filePath);

        imageView.setId(p.getFileName().toString());

        FileInputStream input = new FileInputStream(p.toFile());
        imageView.setImage(new Image(input));

        primaryStage.sizeToScene();
	}


	public byte[] getRGB565(Image img) {
        int imgWidth = (int) img.getWidth();
        int imgHeight= (int) img.getHeight();

        PixelReader reader = img.getPixelReader();

        int bufsize = imgWidth * imgHeight * 4;
        //System.err.printf("Image size: %s x %s (Buffer size: %s bytes)\n",  imgWidth, imgHeight, bufsize);
        byte[]  buffer = new byte[bufsize];

        reader.getPixels(0, 0, imgWidth, imgHeight, PixelFormat.getByteBgraInstance(), buffer, 0, imgWidth * 4);

        //HexDump hd = new HexDump(buffer);
        //hd.dumpAll(System.err);

        int idx = 0;
        int expIdx = 0;

        byte[] rgb565 = new byte[imgWidth * imgHeight * 2];
        for (int y = 0;  y < imgHeight;  y++) {
            for (int x = 0;  x < imgWidth;  x++) {
                //buffer[idx];      // R
                //buffer[idx+1];    // G
                //buffer[idx+2];    // B
                //buffer[idx+3];    // A

                // [rrrrrrrr gggggggg bbbbbbbb]
                //  ^^^^^    ^^^^^^   ^^^^^
                // [rrrrrggg gggbbbbb]
                byte upper = (byte) ((         buffer[idx + 0] & 0xf8) |
                                     ((short)  buffer[idx + 1] & 0xe0) >> 5);
                byte lower = (byte) ((((short) buffer[idx + 1] & 0x1c) << 3) | 
                                     ((short)  buffer[idx + 2] & 0xf8) >> 3);

//                String fromOrig = 
//                        String.format("%8s", Integer.toBinaryString(buffer[idx+0] & 0xff)).replace(' ', '0').substring(0, 5) +
//                        String.format("%8s", Integer.toBinaryString(buffer[idx+1] & 0xff)).replace(' ', '0').substring(0, 6) +
//                        String.format("%8s", Integer.toBinaryString(buffer[idx+2] & 0xff)).replace(' ', '0').substring(0, 5);
//                String fromConverted =
//                        String.format("%8s", Integer.toBinaryString(upper & 0xff)).replace(' ', '0')+
//                        String.format("%8s", Integer.toBinaryString(lower & 0xff)).replace(' ', '0');
//
//                if (!fromOrig.equals(fromConverted)) {
//                    System.err.println("ERRRROR!!!!");
//                }
//                System.err.printf(" rrrrr    gggggg   bbbbb         rrrrrggg gggbbbbb\n\n");

                rgb565[expIdx + 0] = upper;
                rgb565[expIdx + 1] = lower;

                expIdx += 2;
                idx += 4;
            }
        }

        //HexDump hd2 = new HexDump(rgb565);
        //hd2.dumpAll(System.err);

        return rgb565;
    }
}
