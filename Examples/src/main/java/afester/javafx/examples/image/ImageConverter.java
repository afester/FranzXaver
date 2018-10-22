package afester.javafx.examples.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

//import com.example.hexdump.HexDump;

import afester.javafx.examples.Example;
import afester.javafx.examples.ttf.ColorPalette;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

    	
//    	byte[] data = {0, 0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
//    	HexDump hd = new HexDump(data);
//    	hd.dumpAll(System.err);
//    	
//    	int x = rleEncode(data);
//    	System.err.println(x);
//
//    	hd = new HexDump(data);
//    	hd.dumpAll(System.err);
    	
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
//
//        byte b = (byte) 0xff;           // 0xff is an int literal!!
//        System.err.printf("%3d: %s\n", b & 0xff, toBinaryString(b));
//
//        short s = (short) 0xffff;       // 0xffff is an int literal!!
//        System.err.printf("%5d: %s\n", s & 0xffff, toBinaryString(s, 8));
//
//        int i = 0xffffffff;             // 0xffffffff is an int literal!!
//        //System.err.printf("%10d: %s\n", i & 0xffffffffL, toBinaryString(i));
//                                          //  ^^^^^^^^^ Need LONG here!
//        System.err.printf("%10s: %s\n", Integer.toUnsignedString(i), toBinaryString(i, 8));
//
//        long l = 0xffffffffffffffffL;   // 0xffffffffffffffffL is a long literal!!
//        System.err.printf("%10s: %s\n", Long.toUnsignedString(l), toBinaryString(l, 8));
//
//        System.err.println(insertSeparator("Hello World", "|", 2));

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

//        Button exportButton = new Button("Export ...");
//        exportButton.setOnAction(e -> {
//        	imageViews.forEach(iv -> {
//                Image img = iv.getImage();
//                byte[] rgb565 = getRGB565(img);
//                ArrayDump ad = new ArrayDump(rgb565);
//                ad.dumpAll((int)img.getWidth()*2, System.err);
//        	});
//        });
//        mainGroup.getChildren().add(exportButton);

        Button exportStructButton = new Button("Export struct Bitmap ...");
        exportStructButton.setOnAction(e -> {
        	
        	imageViews.forEach(iv -> {
                Image img = iv.getImage();
                short[] rgb565 = getRGB565(img);

                ArrayDump ad = new ArrayDump(rgb565);
                int width = (int) img.getWidth();
                int height = (int) img.getHeight();
                System.err.printf("Bitmap16 %s = {%s, %s,\n", iv.getId(), width, height);
                ad.dumpAll(width, System.err);
                System.err.println("};\n");
        	});        	

        });
        mainGroup.getChildren().add(exportStructButton);

        Button exportStructPaletteButton = new Button("Export struct Bitmap with Palette ...");
        exportStructPaletteButton.setOnAction(e -> {
            try (OutputStream fos = new FileOutputStream("result.c")) {
                RleEncoder rle = new RleEncoder();

            	PrintStream out = new PrintStream(fos);
	
	        	// use the same palette for all images
	            List<Short> palette = new ArrayList<>();
	        	imageViews.forEach(iv -> {
		            Image img = iv.getImage();
		
		            short[] rgb565 = getRGB565(img);
	
		            // convert bitmap to indexed bitmap
		            byte[] bitmap = new byte[rgb565.length];
		            for (int idx = 0;  idx < rgb565.length;  idx++) {
		            	short value = rgb565[idx];
		
		                // lookup value index
		                int colorIdx = palette.indexOf(value);
		                if (colorIdx == -1) {
		                	colorIdx = palette.size();
		                	palette.add(value);
		                }
	
		                bitmap[idx] = (byte) colorIdx;	// TODO: max. 256 colors
		            }
	
		            int newLength = rle.rleEncode_4plus4(bitmap);
		            byte[] bitmapRle = new byte[newLength];
		            System.arraycopy(bitmap, 0, bitmapRle, 0, newLength);
		            
		            ArrayDump ad = new ArrayDump(bitmapRle);
		            int width = (int) img.getWidth();
		            int height = (int) img.getHeight();

		            out.printf("const Bitmap8 %s PROGMEM = {%s, %s,\n", iv.getId(), width, height);
		            ad.dumpAll(width, out);
		            out.println("};\n");
	        	});
	
	        	// dump the palette
	        	out.print("uint16_t palette[] = {");
	        	String prefix = "";
	        	for (int v : palette) {
	                out.printf("%s0x%04x", prefix, v & 0xffff);
	                prefix = ", ";
	        	}
	            out.println("};\n");
            
            } catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

        });
        mainGroup.getChildren().add(exportStructPaletteButton);

        Scene scene = new Scene(mainGroup);

        primaryStage.setScene(scene);
        primaryStage.show();
        
        try {
            //loadImage("C:\\temp\\rgb.png");
			//loadImage("C:\\Users\\AFESTER\\Projects\\CodeSamples\\Embedded\\AVR\\ILI9481\\adRedBlack.png");
	        //loadImage("C:\\Users\\AFESTER\\Projects\\CodeSamples\\Embedded\\AVR\\ILI9481\\bcefRedBlack.png");
	        //loadImage("C:\\Users\\AFESTER\\Projects\\CodeSamples\\Embedded\\AVR\\ILI9481\\gRedBlack.png");
			//loadImage("/home/andreas/Projects/CodeSamples/Embedded/AVR/ILI9481/adRedBlack.png");
	        //loadImage("/home/andreas/Projects/CodeSamples/Embedded/AVR/ILI9481/bcefRedBlack.png");
            //loadImage("/home/andreas/Projects/CodeSamples/Embedded/AVR/ILI9481/gRedBlack.png");
           
//        	loadImage("/home/andreas/Projects/CodeSamples/Embedded/AVR/ILI9481/null.png");
//            loadImage("/home/andreas/Projects/CodeSamples/Embedded/AVR/ILI9481/eins.png");
//            loadImage("/home/andreas/Projects/CodeSamples/Embedded/AVR/ILI9481/zwei.png");
//            loadImage("/home/andreas/Projects/CodeSamples/Embedded/AVR/ILI9481/drei.png");
//            loadImage("/home/andreas/Projects/CodeSamples/Embedded/AVR/ILI9481/vier.png");
//            loadImage("/home/andreas/Projects/CodeSamples/Embedded/AVR/ILI9481/fuenf.png");
//            loadImage("/home/andreas/Projects/CodeSamples/Embedded/AVR/ILI9481/sechs.png");
//            loadImage("/home/andreas/Projects/CodeSamples/Embedded/AVR/ILI9481/sieben.png");
//            loadImage("/home/andreas/Projects/CodeSamples/Embedded/AVR/ILI9481/acht.png");
//            loadImage("/home/andreas/Projects/CodeSamples/Embedded/AVR/ILI9481/neun.png");
          loadImage("null.png");
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


    /**
     * 
     * @param img
     * @return An RGB565 format bitmap from the given image. 
     */
	public short[] getRGB565(Image img) {
        int imgWidth = (int) img.getWidth();
        int imgHeight= (int) img.getHeight();

        PixelReader reader = img.getPixelReader();

        // get pixel data as ARGB into an integer buffer
        int intBufsize = imgWidth * imgHeight;
        int[] intBuffer = new int[intBufsize];
        reader.getPixels(0, 0, imgWidth, imgHeight, PixelFormat.getIntArgbInstance(), intBuffer, 0, imgWidth);

        // convert the integer buffer into a short buffer with format rgb565
        short[] result = new short[imgWidth * imgHeight];
        for (int idx = 0;  idx < intBufsize; idx++) {
        	short value = fromARGBToGBR565(intBuffer[idx]);

        	// change byte order - this is required because the
        	// TFT display receives the HIGH byte during the first transfer 
        	// on the 8 bit bus, followed by the LOW byte!
        	// Hence the high byte must be stored at the lower address (Big Endian format).
        	// we could also leave it as little endian here, and change
        	// the code in the MCU which writes frame data to the TFT display.
        	value = (short) (((value & 0xff) << 8) | ((value & 0xff00) >> 8));
        	result[idx] = value;
        }

        return result;
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
     * @param value A 16 bit value to convert to a 16 bit binary string.
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


	/**
	 * Converts an ARGB value, stored in a 32 bit long value, into
	 * an RGB565 value, stored in a 16 bit short value.
	 * <code>
	 * aaaaaaaa rrrrrrrr gggggggg bbbbbbbb
	 *          ^^^^^    ^^^^^^   ^^^^^
	 *
	 *                   rrrrrggg gggbbbbb
	 * </code>
	 *
     * @param rgbValue The (a)rgb value as a long
     * @return The converted rgb565 value, in rgb order!
	 */
    public static short fromARGBToRGB565(long rgbValue) {
        long result = ((rgbValue & 0x00f80000) >> 8) +
                      ((rgbValue & 0x0000fc00) >> 5) +
                      ((rgbValue & 0x000000f8) >> 3);
        return (short) (result & 0xffff);
    }


    /**
     * Converts an RGB565 value, stored in a 16 bit short value, into
     * an RGB value, stored in a 32 bit long value.
     * <code>
     *                   rrrrrggg gggbbbbb
     *
     * 00000000 rrrrr000 gggggg00 bbbbb000
     *
     * </code>
     *
     * @param rgb565Value The rgb565 value as a short
     * @return The converted rgb value, in RGB order!
     */
    public static int fromRGB565ToRGB(short rgb565Value) {
        int result = ((rgb565Value & 0xf800) << 8) +
                     ((rgb565Value & 0x07e0) << 5) +
                     ((rgb565Value & 0x001f) << 3);
        return result;
    }


	/**
	 * Converts an ARGB value, stored in a 32 bit long value, into
	 * an GBR565 value, stored in a 16 bit short value.
	 * <code>
	 * aaaaaaaa rrrrrrrr gggggggg bbbbbbbb
	 *          ^^^^^    ^^^^^^   ^^^^^
	 *
	 *                   bbbbbggg gggrrrrr
	 * </code>
	 * 
     * @param rgbValue The (a)rgb value as a long
     * @return The converted rgb565 value, in GBR order!
     */
    public static short fromARGBToGBR565(long rgbValue) {
        long result = ((rgbValue & 0x00f80000) >> 19) +
                      ((rgbValue & 0x0000fc00) >> 5) +
                      ((rgbValue & 0x000000f8) << 8);
        return (short) (result & 0xffff);
    }

	public byte[] getRGB565asByte(Image img) {
        int imgWidth = (int) img.getWidth();
        int imgHeight= (int) img.getHeight();

        PixelReader reader = img.getPixelReader();

        //int bufsize = imgWidth * imgHeight * 4;
        //System.err.printf("Image size: %s x %s (Buffer size: %s bytes)\n",  imgWidth, imgHeight, bufsize);
        //byte[]  buffer = new byte[bufsize];

        // NOTE: Order is Blue, Green, Red, Alpha!!!
        //reader.getPixels(0, 0, imgWidth, imgHeight, PixelFormat.getByteBgraInstance(), buffer, 0, imgWidth * 4);

        // get pixel data as ARGB into an integer buffer
        int intBufsize = imgWidth * imgHeight;
        int[] intBuffer = new int[intBufsize];
        reader.getPixels(0, 0, imgWidth, imgHeight, PixelFormat.getIntArgbInstance(), intBuffer, 0, imgWidth);

        //HexDump hd = new HexDump(intBuffer);
        //hd.dumpAll(System.err);

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

            	short value= fromARGBToGBR565(intBuffer[idx]);
            	rgb565[idx+0] = (byte) (value & 0xff);
            	rgb565[idx+1] = (byte) ((value & 0xff00) >> 8);

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

                // Big endian ! Why?
                //rgb565[idx
                //rgb565[expIdx + 1] = (byte) (bgr565 & 0xff);

                //expIdx += 2;
                idx++;
            }
        }

        //HexDump hd2 = new HexDump(rgb565);
        //hd2.dumpAll(System.err);

        return rgb565;	
    }

	/**
	 * @param image The image to return a bitmap from. 
	 * @param pal The palette to use.
	 * @return A bitmap of the given image using the given palette.
	 *         Note that the maximum colors in the palette is 256.
	 */
    public byte[] getIndexed(Image image, ColorPalette cp) {
        int imgWidth = (int) image.getWidth();
        int imgHeight= (int) image.getHeight();

        byte[] result = new byte[imgWidth * imgHeight];
        int destIdx = 0;
        PixelReader reader = image.getPixelReader();
        for (int y = 0; y < imgHeight;  y++) {
            for (int x = 0; x < imgWidth;  x++) {
                Color pixel = reader.getColor(x, y);
                int idx = cp.indexOf(pixel);
                result[destIdx++] = (byte) idx; // 0 .. 15
            }
        }

        return result;
    }
}
