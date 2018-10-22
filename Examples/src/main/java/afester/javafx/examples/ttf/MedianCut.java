package afester.javafx.examples.ttf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


/**
 * An implementation of the Median Cut algorithm for colour quantization as 
 * described in the book "Principles of digital image processing: Core algorithms" 
 * by Wilhelm Burger, Mark James Burge (Springer, 2009) pp 90-92.
 * 
 * Source: https://gist.github.com/stubbedtoe/8070228
 */
public class MedianCut { 
    final int RED = 0;
    final int GREEN = 1;
    final int BLUE = 2;

    /**
     * Converts an image into the same image with the number of colors reduced to the given amount.
     *
     * @param orig The original image
     * @param maxColors The maximum colors to use in the output image
     * @return The color-reduced image.
     */
    public Image medianCut(Image orig, int maxColors){
        final long start = System.currentTimeMillis();

        // Step 1: Get a map of all colors in the original image
        Map<Integer, MyColor> origCols = findOriginalColors(orig);

        // Step 2: find the colors which best match all the original colors in the reduced palette
        int[] repCols = findRepresentativeColors(origCols, maxColors);

        // Step 3: Quantize the image with the new set of colors
        Image result = quantizeImage(orig, repCols);

        final long stop = System.currentTimeMillis();
        System.err.printf("Time taken: %s sec.\n", (stop - start) / 1000.0F);

        return result;
    }


    /**
     * Takes the original image and an array of new colours of size maxColors and
     * returns the quantized image.
     * 
     * @param img The original image.
     * @param newCols The palette with the new colors.
     *
     * @return The quantized image.
     */
    public Image quantizeImage(Image img, int[] newCols){
        //the new image should be the same size as the original
        WritableImage newImg = new WritableImage((int) img.getWidth(), (int) img.getHeight());
        PixelReader reader = img.getPixelReader();
        PixelWriter writer = newImg.getPixelWriter();

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int c = col2int(reader.getColor(x, y));

                // find the closest colour in the array to the original pixel's colour
                int index = 0;
                float closest = distanceToRGB(c, newCols[0]);
                for(int j = 1; j < newCols.length; j++){
                    if(closest > distanceToRGB(c, newCols[j])){
                        closest = distanceToRGB(c, newCols[j]);
                        index = j;
                    }
                }

                //set the new image's pixel to the closest colour in the array
                writer.setColor(x,  y, new Color(red(newCols[index]) / 255.0, green(newCols[index]) / 255.0, blue(newCols[index])  / 255.0, 1.0));
            }
        }

        return newImg;
    }

    /**
     * The main loop to subdivide the large colour space into the required amount 
     * of smaller boxes. Return the array of the average colour for each of these boxes.
     *
     * @param orig
     * @param maxColors
     *
     * @return
     */
    public int[] findRepresentativeColors(Map<Integer, MyColor> origCols, int maxColors){

        if(origCols.size() <= maxColors){
            // num of colours is less than or equal to the max num in the orig image
            // so simply return the colour in an int array

            int[]toReturn = new int[origCols.size()];
            Iterator<MyColor> it = origCols.values().iterator();
            int index = 0;
            while(it.hasNext()){
                MyColor mc = (MyColor)it.next();
                toReturn[index] = mc.col;
                index++;
            }
            return toReturn;
   
        }else{
            // otherwise subdivide the box of colours until the required number 
            // has been reached
            ArrayList<ColorBox> colorBoxes = new ArrayList<>(); //where the boxes will be stored
            ColorBox first = new ColorBox(origCols, 0); //the largest box (level 0)
            colorBoxes.add(first);
            int k = 1; //we have one box
            boolean done = false;
            while(k < maxColors && !done){
    
                ColorBox next = findBoxToSplit(colorBoxes);
                if(next != null){
    
                    ColorBox [] boxes = next.splitBox();
    
                    if (colorBoxes.remove(next)){} //finds and removes an element in one

                    //replaced with the two smaller boxes that make it up 
                    colorBoxes.add(boxes[0]);
                    colorBoxes.add(boxes[1]);   
                    k++; //we have one more box
    
                }else{
                    done = true;
                }
            }
    
            //get the average colour from each of the boxes in the arraylist
            int [] avgCols = new int [colorBoxes.size()];
            for(int i = 0; i < avgCols.length; i++){
                ColorBox cb = colorBoxes.get(i);
                avgCols[i] = cb.averageColor();
            }
    
            return avgCols;
        }
    }

    //takes the list of all candidate boxes and returns the one to be split next.
    private ColorBox findBoxToSplit(ArrayList<ColorBox> listOfBoxes){
    
        ArrayList<ColorBox> canBeSplit = new ArrayList<>();
        for(int i = 0; i < listOfBoxes.size(); i++){
            ColorBox cb = listOfBoxes.get(i);

            // only boxes containing more than one colour can be split 
            if(cb.cols.size() > 1){
                canBeSplit.add(cb);
            }   
        }
    
        if(canBeSplit.size() == 0){
            return null; //a null will trigger the end of the subdividing loop
        }
        
        // use the 'level' of each box to ensure they are divided in the correct order.
        // the box with the lowest level is returned.

        ColorBox minBox = canBeSplit.get(0);
        int minLevel = minBox.level;

        for(int i = 1; i < canBeSplit.size(); i++){
            ColorBox test = canBeSplit.get(i);
            if(minLevel > test.level){
                minLevel = test.level;
                minBox = test;
            }
        }

        return minBox;
    }


    /**
     * Gets the "distance" between two colours in Euclidian space.
     * This is not the "real" distance (which would be sqrt((r1-r2)^2 + (g1-g2)^2 + (b1 - b2)^2) )
     * but only a "measurement" for the distance.
     *
     * See https://code.i-harness.com/de/q/899aa0
     *  "den durchschnittlichen Farbunterschied in Prozent ."
     *
     * @param col1 The first color
     * @param col2 The second color
     *
     * @return The distance between the colors
     */
    private float distanceToRGB(int col1, int col2){
        float redDiff = Math.abs(red(col1) - red(col2));
        float greenDiff = Math.abs(green(col1) - green(col2));
        float blueDiff = Math.abs(blue(col1) - blue(col2));

        return ((redDiff + greenDiff + blueDiff) / 3.0F);
    }


    public static int blue(int col1) {
        return col1 & 0xff;
    }

    public static int green(int col1) {
        return (col1 >> 8) & 0xff;
    }


    public static int red(int col1) {
        return (col1 >> 16) & 0xff;
    }


    /**
     * Called at the very start to determine the colours in the original image
     * (returns the palette for the image)
     * 
     * @param img The image for which to return the colors.
     * @return
     */
    private Map<Integer, MyColor> findOriginalColors(final Image img){

        //entries in the hashmap are stored in (key,value) pairs
        //key = int colour , value = MyColor colorObject 
        Map<Integer, MyColor> toReturn = new HashMap<>();

        PixelReader reader = img.getPixelReader();
        for (int y = 0; y < img.getHeight(); y++) {
           for (int x = 0; x < img.getWidth(); x++) {
               int c = col2int(reader.getColor(x, y));

               if(toReturn.containsKey(c)){ 
                   MyColor temp = (MyColor)toReturn.get(c);
                   temp.increment(); //increase its 'count' value (NOTE: Not used anywhere!)
               }else{
                   MyColor toAdd = new MyColor(c);
                   toReturn.put(c, toAdd);
               }
           }
        }

        return toReturn;
    }

    /**
     * Gets the set of representative colors from a List of images
     * @param img
     * @return
     */
    public Map<Integer, MyColor> findOriginalColors(final List<Image> imageList){

        //entries in the hashmap are stored in (key,value) pairs
        //key = int colour , value = MyColor colorObject 
        Map<Integer, MyColor> toReturn = new HashMap<>();

        for (Image img : imageList) {
            PixelReader reader = img.getPixelReader();
            for (int y = 0; y < img.getHeight(); y++) {
               for (int x = 0; x < img.getWidth(); x++) {
                   int c = col2int(reader.getColor(x, y));
    
                   if(toReturn.containsKey(c)){ 
                       MyColor temp = toReturn.get(c);
                       temp.increment(); //increase its 'count' value (NOTE: Not used anywhere!)
                   }else{
                       MyColor toAdd = new MyColor(c);
                       toReturn.put(c, toAdd);
                   }
               }
            }
        }

        return toReturn;
    }

    
    
    /**
     * Converts a Color into the corresponding integer value
     * @param color
     * @return
     */
    public static int col2int(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return ( (r << 16) + (g << 8) + b);
    }


    // class to store colours for use in the ColorBoxes
    class MyColor{
    
        int col;
        int count;
        float [] rgb = new float[3];
    
        // constructor takes the color as an int
        MyColor(int col){
            this.col = col;
            
            // bitshifting faster than red(col) etc. 
            rgb[RED]= (col >> 16) & 0xFF;
            rgb[GREEN]=(col >> 8) & 0xFF;
            rgb[BLUE]= col & 0xFF;
            
            count = 1;  // Note: in this algorithm, count (which determines the color spectrum) is not used...
        }
    
        void increment(){
            count++;
        }
    }
}
