package afester.javafx.examples.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.example.hexdump.HexDump;

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

//        byte b = 0;
//        while(true) {
//            System.err.printf("%3d: %s\n", b & 0xff, toBinaryString(b));
//            b++;
//            if (b == 0) {
//                break;
//            }
//        }
//
//        short s = 0;
//        while(true) {
//            System.err.printf("%5d: %s\n", s & 0xffff, toBinaryString(s));
//            s++;
//            if (s == 0) {
//                break;
//            }
//        }

        byte b = (byte) 0xff;           // 0xff is an int literal!!
        System.err.printf("%3d: %s\n", b & 0xff, toBinaryString(b));

        short s = (short) 0xffff;       // 0xffff is an int literal!!
        System.err.printf("%5d: %s\n", s & 0xffff, toBinaryString(s, 8));

        int i = 0xffffffff;             // 0xffffffff is an int literal!!
        //System.err.printf("%10d: %s\n", i & 0xffffffffL, toBinaryString(i));
                                          //  ^^^^^^^^^ Need LONG here!
        System.err.printf("%10s: %s\n", Integer.toUnsignedString(i), toBinaryString(i, 8));

        long l = 0xffffffffffffffffL;   // 0xffffffffffffffffL is a long literal!!
        System.err.printf("%10s: %s\n", Long.toUnsignedString(l), toBinaryString(l, 8));

        System.err.println(insertSeparator("Hello World", "|", 2));

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
                System.err.printf("Bitmap16 %s = {%s, %s,\n", iv.getId(), width, height);
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
	            System.err.printf("Bitmap8 %s = {%s, %s,\n", iv.getId(), width, height);
	            ad.dumpAll(width, System.err);
	            System.err.println("};\n");
        	});

        	// dump the palette
        	System.err.print("uint16_t palette[] = {");
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
            //loadImage("C:\\temp\\rgb.png");
			loadImage("C:\\Users\\AFESTER\\Projects\\CodeSamples\\Embedded\\AVR\\ILI9481\\adRedBlack.png");
	        //loadImage("C:\\Users\\AFESTER\\Projects\\CodeSamples\\Embedded\\AVR\\ILI9481\\bcefRedBlack.png");
	        //loadImage("C:\\Users\\AFESTER\\Projects\\CodeSamples\\Embedded\\AVR\\ILI9481\\gRedBlack.png");
			//loadImage("/home/andreas/Projects/CodeSamples/Embedded/AVR/ILI9481/adRedBlack.png");
	        //loadImage("/home/andreas/Projects/CodeSamples/Embedded/AVR/ILI9481/bcefRedBlack.png");
            //loadImage("/home/andreas/Projects/CodeSamples/Embedded/AVR/ILI9481/gRedBlack.png");
        	
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
    }


    private void loadImage(String filePath) throws FileNotFoundException {
        Path p = Paths.get(filePath);

        ImageView imageView = new ImageView();
        String fileName = p.getFileName().toString();
        fileName = fileName.substring(0, fileName.length() - 4);
        imageView.setId(fileName);
        imageViews.add(imageView);
        mainGroup.getChildren().add(imageView);

        FileInputStream input = new FileInputStream(p.toFile());
        imageView.setImage(new Image(input));

        primaryStage.sizeToScene();
	}


	public byte[] getRGB565(Image img) {
		System.err.printf("0x%04x\n", fromRbgToGbr565(0xff0000));
		System.err.printf("0x%04x\n", fromRbgToGbr565(0x4b0506));

        int imgWidth = (int) img.getWidth();
        int imgHeight= (int) img.getHeight();

        PixelReader reader = img.getPixelReader();

        int bufsize = imgWidth * imgHeight * 4;
        //System.err.printf("Image size: %s x %s (Buffer size: %s bytes)\n",  imgWidth, imgHeight, bufsize);
        byte[]  buffer = new byte[bufsize];

        // NOTE: Order is Blue, Green, Red, Alpha!!!
        reader.getPixels(0, 0, imgWidth, imgHeight, PixelFormat.getByteBgraInstance(), buffer, 0, imgWidth * 4);
        
        int intBufsize = imgWidth * imgHeight;
        int[]  intBuffer = new int[intBufsize];
        reader.getPixels(0, 0, imgWidth, imgHeight, PixelFormat.getIntArgbInstance(), intBuffer, 0, imgWidth);

        HexDump hd = new HexDump(intBuffer);
        hd.dumpAll(System.err);

        int idx = 0;
        int expIdx = 0;

        int count = 0;
        byte[] rgb565 = new byte[imgWidth * imgHeight * 2];
        for (int y = 0;  y < imgHeight;  y++) {
            for (int x = 0;  x < imgWidth;  x++) {
                //buffer[idx+0];    // B
                //buffer[idx+1];    // G
                //buffer[idx+2];    // R
                //buffer[idx+3];    // A

                //System.err.printf("BUFFER: %s %s %s\n", toBinaryString(buffer[idx+0]), toBinaryString(buffer[idx+1]), toBinaryString(buffer[idx+2]));

                // [bbbbbbbb gggggggg rrrrrrrr aaaaaaaa]
                //  ^^^^^    ^^^^^^   ^^^^^

                // tft panel configured for BGR order:
                //          [bbbbbggg gggrrrrr]

                // tft panel configured for RGB order:
                //          [rrrrrggg gggbbbbb]

                byte upper = (byte) ((         buffer[idx + 0] & 0xf8) |
                                     ((short)  buffer[idx + 1] & 0xe0) >> 5);
                byte lower = (byte) ((((short) buffer[idx + 1] & 0x1c) << 3) | 
                                     ((short)  buffer[idx + 2] & 0xf8) >> 3);

//                String fromOrig = 
//                        String.format("%8s", Integer.toBinaryString(buffer[idx+0] & 0xff)).replace(' ', '0').substring(0, 5) +
//                        String.format("%8s", Integer.toBinaryString(buffer[idx+1] & 0xff)).replace(' ', '0').substring(0, 6) +
//                        String.format("%8s", Integer.toBinaryString(buffer[idx+2] & 0xff)).replace(' ', '0').substring(0, 5);
//                String fromOrig = 
//                		String.format("%8s", Integer.toBinaryString(buffer[idx+0] & 0xff)).replace(' ', '0') + " " +
//                		String.format("%8s", Integer.toBinaryString(buffer[idx+1] & 0xff)).replace(' ', '0') + " " +
//	              		String.format("%8s", Integer.toBinaryString(buffer[idx+2] & 0xff)).replace(' ', '0');
//                String fromConverted =
//                        String.format("%8s", Integer.toBinaryString(upper & 0xff)).replace(' ', '0')+
//                        String.format("%8s", Integer.toBinaryString(lower & 0xff)).replace(' ', '0');
//                System.err.printf("%s -> %s\n", fromOrig, fromConverted);
//                if (count++ > 5) return rgb565;
//
//                if (!fromOrig.equals(fromConverted)) {
//                    System.err.println("ERRRROR!!!!");
//                }
//                System.err.printf(" rrrrr    gggggg   bbbbb         rrrrrggg gggbbbbb\n\n");

                rgb565[expIdx + 0] = upper; // Attention: byte order!!!
                rgb565[expIdx + 1] = lower;

                expIdx += 2;
                idx += 4;
            }
        }

        //HexDump hd2 = new HexDump(rgb565);
        //hd2.dumpAll(System.err);

        return rgb565;
    }


	/**
	 * Splits a String into sub strings of a given size, starting from the right.
	 * The first element might contain less than groupSize characters. 
	 *
	 * @param base The string to split.
	 * @param groupSize The (maximum) number of characters in each part. 
	 *
	 * @return The subdivided string.
	 */
	private String[] splitString(String base, int groupSize) {
	    List<String> result = new ArrayList<>();

	    int idx = base.length();

	    while(true) {
    	    idx -= groupSize;
    	    if (idx < 0) {
    	        groupSize = groupSize + idx;
                idx = 0;
    	    }
    	    result.add(0, base.substring(idx, idx + groupSize));
    	    if (idx == 0) {
    	        break;
    	    }
	    }

	    return result.toArray(new String[0]);
	}

    /**
     * Splits a String into sub strings of a given size, starting from the 
     * right, and inserts a separator between the parts.
     * The first element might contain less than groupSize characters. 
     *
     * @param base The string to split.
     * @param separator The separator to insert.
     * @param groupSize The (maximum) number of characters in each part. 
     *
     * @return The subdivided string.
     */
    private String insertSeparator (String base, String separator, int groupSize) {
        StringBuffer result = new StringBuffer();
        for (String part : splitString(base, groupSize)) {
            if (result.length() != 0) {
                result.append(separator);
            }
            result.append(part);
        }
        return result.toString();
    }


    /**
     * @param value A 64 bit value to convert to a 64 bit binary string.
     *
     * @return The 64 bit binary string which corresponds to the given value.
     */
    private String toBinaryString(long value) {
        return String.format("%64s", Long.toBinaryString(value)).replace(' ', '0');
    }

    private String toBinaryString(long value, int groupSize) {
        String result = String.format("%64s", Long.toBinaryString(value)).replace(' ', '0');
        return insertSeparator(result, " ", groupSize);
    }

    /**
     * @param value A 32 bit value to convert to a 32 bit binary string.
     *
     * @return The 32 bit binary string which corresponds to the given value.
     */
    private String toBinaryString(int value) {
        return String.format("%32s", Integer.toBinaryString(value)).replace(' ', '0');
    }

    private String toBinaryString(int value, int groupSize) {
        String result = String.format("%32s", Integer.toBinaryString(value)).replace(' ', '0');
        return insertSeparator(result, " ", groupSize);
    }

    /**
     * @param s A 16 bit value to convert to a 16 bit binary string.
     *
     * @return The 16 bit binary string which corresponds to the given value.
     */
    private String toBinaryString(short value) {
        return String.format("%16s", Integer.toBinaryString(value & 0xffff)).replace(' ', '0');
    }

    private String toBinaryString(short value, int groupSize) {
        String result = String.format("%16s", Integer.toBinaryString(value & 0xffff)).replace(' ', '0');
        return insertSeparator(result, " ", groupSize);
    }

	/**
	 * @param b An 8 bit value to convert to a 8 bit binary string.
	 *
	 * @return The 8 bit binary string which corresponds to the given value.
	 */
	private String toBinaryString(byte value) {
	    return String.format("%8s", Integer.toBinaryString(value & 0xff)).replace(' ', '0');
    }

    private long fromRbgToRgb565(long rgbValue) {
//		System.err.printf("0x%08x\n", rgbValue );
//		System.err.printf("0x%08x\n", (rgbValue & 0xf80000) >> 8);
//		System.err.printf("0x%08x\n", (rgbValue & 0x00fc00) >> 5);
//		System.err.printf("0x%08x\n", (rgbValue & 0x0000f8) >> 3);

		long result = ((rgbValue & 0xf80000) >> 8) +
		              ((rgbValue & 0x00fc00) >> 5) +
		              ((rgbValue & 0x0000f8) >> 3);
		return result;
	}

	private long fromRbgToGbr565(long rgbValue) {
//		System.err.printf("0x%08x\n", rgbValue );
//		System.err.printf("0x%08x\n", (rgbValue & 0xf80000) >> 8);
//		System.err.printf("0x%08x\n", (rgbValue & 0x00fc00) >> 5);
//		System.err.printf("0x%08x\n", (rgbValue & 0x0000f8) >> 3);

		long result = ((rgbValue & 0xf80000) >> 19) +
		              ((rgbValue & 0x00fc00) >> 5) +
		              ((rgbValue & 0x0000f8) << 8);
		return result;
	}

    private short fromARGBToRGB565(long rgbValue) {
//	      System.err.printf("0x%08x\n", rgbValue );
//	      System.err.printf("0x%08x\n", (rgbValue & 0xf80000) >> 8);
//	      System.err.printf("0x%08x\n", (rgbValue & 0x00fc00) >> 5);
//	      System.err.printf("0x%08x\n", (rgbValue & 0x0000f8) >> 3);

        long result = ((rgbValue & 0xf80000) >> 19) +
                      ((rgbValue & 0x00fc00) >> 5) +
                      ((rgbValue & 0x0000f8) << 8);
        return result;
    }

    private short fromARGBToGBR565(long rgbValue) {
//      System.err.printf("0x%08x\n", rgbValue );
//      System.err.printf("0x%08x\n", (rgbValue & 0xf80000) >> 8);
//      System.err.printf("0x%08x\n", (rgbValue & 0x00fc00) >> 5);
//      System.err.printf("0x%08x\n", (rgbValue & 0x0000f8) >> 3);

      long result = ((rgbValue & 0xf80000) >> 19) +
                    ((rgbValue & 0x00fc00) >> 5) +
                    ((rgbValue & 0x0000f8) << 8);
      return result;
  }

}
