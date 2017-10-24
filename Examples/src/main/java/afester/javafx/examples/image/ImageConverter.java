package afester.javafx.examples.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

//import com.example.hexdump.HexDump;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
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

    
    private List<ImageView> imageViews = new ArrayList<>();
    private VBox mainGroup = new VBox();
    private Stage primaryStage;

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

        Button exportButton = new Button("Export ...");
        exportButton.setOnAction(e -> {
        	imageViews.forEach(iv -> {
                Image img = iv.getImage();
                byte[] rgb565 = getRGB565(img);
                ArrayDump ad = new ArrayDump(rgb565);
                ad.dumpAll((int)img.getWidth()*2, System.err);
        	});
        });
        mainGroup.getChildren().add(exportButton);

        Button exportStructButton = new Button("Export struct Bitmap ...");
        exportStructButton.setOnAction(e -> {
        	imageViews.forEach(iv -> {
                Image img = iv.getImage();
                byte[] rgb565 = getRGB565(img);

                ArrayDump ad = new ArrayDump(rgb565);
                int width = (int) img.getWidth();
                int height = (int) img.getHeight();
                System.err.printf("Bitmap %s = {%s, %s,\n", iv.getId(), width, height);
                ad.dumpAll16(System.err, width);
                System.err.println("};\n");
        	});        	
            
        });
        mainGroup.getChildren().add(exportStructButton);

        Button exportStructPaletteButton = new Button("Export struct Bitmap with Palette ...");
        exportStructPaletteButton.setOnAction(e -> {
        	
        	// use the same palette for all images
            List<Integer> palette = new ArrayList<>();
        	imageViews.forEach(iv -> {
	            Image img = iv.getImage();
	
	            byte[] rgb565 = getRGB565(img);
	
	            // convert bitmap to indexed bitmap
	            byte[] bitmap = new byte[rgb565.length / 2];
	            int bitmapIdx = 0;
	            for (int idx = 0;  idx < rgb565.length;  ) {
	                int value = (short) rgb565[idx] & 0xff;
	                value = value | ((short) rgb565[idx+1] & 0xff) << 8;
	                idx += 2;
	
	                // lookup value index
	                int colorIdx = palette.indexOf(value);
	                if (colorIdx == -1) {
	                	colorIdx = palette.size();
	                	palette.add(value);
	                }
	
	                bitmap[bitmapIdx++] = (byte) colorIdx;	// TODO: max. 256 colors
	            }

	            ArrayDump ad = new ArrayDump(bitmap);
	            int width = (int) img.getWidth();
	            int height = (int) img.getHeight();
	            System.err.printf("Bitmap %s = {%s, %s,\n", iv.getId(), width, height);
	            ad.dumpAll(width, System.err);
	            System.err.println("};\n");
        	});

        	// dump the palette
        	System.err.print("unsigned int[] palette = {");
        	String prefix = "";
        	for (int v : palette) {
                System.err.printf("%s0x%04x", prefix, v);
                prefix = ", ";
        	}
            System.err.println("};\n");
        });
        mainGroup.getChildren().add(exportStructPaletteButton);

        Scene scene = new Scene(mainGroup);

        primaryStage.setScene(scene);
        primaryStage.show();
        
        try {
			loadImage("C:\\Users\\AFESTER\\Projects\\CodeSamples\\Embedded\\AVR\\ILI9481\\adRedBlack.png");
	        loadImage("C:\\Users\\AFESTER\\Projects\\CodeSamples\\Embedded\\AVR\\ILI9481\\bcefRedBlack.png");
	        loadImage("C:\\Users\\AFESTER\\Projects\\CodeSamples\\Embedded\\AVR\\ILI9481\\gRedBlack.png");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
    }


    private void loadImage(String filePath) throws FileNotFoundException {
        Path p = Paths.get(filePath);

        ImageView imageView = new ImageView();
        imageView.setId(p.getFileName().toString());
        imageViews.add(imageView);
        mainGroup.getChildren().add(imageView);

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
