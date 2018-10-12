package afester.javafx.examples.ttf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// Source:
// https://gist.github.com/stubbedtoe/8070228
// The Median Cut algorithm for colour quantization implemented in Processing. 
// The algorithm is implemented as described in the book "Principles of 
// digital image processing: Core algorithms" by Wilhelm Burger, Mark James Burge (Springer, 2009) pp 90-92.
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
        testFindMin();
        int[] repCols = findRepresentativeColors(orig, maxColors);
        return quantizeImage(orig, repCols);
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
    private Image quantizeImage(Image img, int[] newCols){
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
                for(int j=1;j<newCols.length;j++){
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
    private int[] findRepresentativeColors(Image orig, int maxColors){

        //get a reference to each colour in the original image
        Map<Integer, MyColor> origCols = findOriginalColors(orig);
        if(origCols.size() <= maxColors){
            //num of colours is less than or equal to the max num in the orig image
            //so simply return the colour in an int array
    
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
            //otherwise subdivide the box of colours until the required number 
            //has been reached
    
            ArrayList<ColorBox> colorBoxes = new ArrayList<>(); //where the boxes will be stored
            ColorBox first = new ColorBox(origCols, 0); //the largest box (level 0)
            colorBoxes.add(first);
            int k = 1; //we have one box
            boolean done = false;
    
            while(k<maxColors && !done){
    
                ColorBox next = findBoxToSplit(colorBoxes);
                if(next != null){
    
                    ColorBox [] boxes = splitBox(next);
    
                    if(colorBoxes.remove(next)){} //finds and removes an element in one
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
            for(int i=0; i<avgCols.length; i++){
                ColorBox cb = (ColorBox)colorBoxes.get(i);
                avgCols[i] = averageColor(cb);
            }
    
            return avgCols;
        }
    }

    //takes the list of all candidate boxes and returns the one to be split next.
    private ColorBox findBoxToSplit(ArrayList<ColorBox> listOfBoxes){
    
        ArrayList<ColorBox> canBeSplit = new ArrayList<>();
    
        for(int i = 0; i<listOfBoxes.size(); i++){
            ColorBox cb = listOfBoxes.get(i);

            //only boxes containing more than one colour can be split 
            if(cb.cols.size() > 1){
                canBeSplit.add(cb);
            }   
        }
    
        if(canBeSplit.size() == 0){
            return null; //a null will trigger the end of the subdividing loop
        }else{
    
            //use the 'level' of each box to ensure they are divided in the correct order.
            //the box with the lowest level is returned.
    
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
    
    }
    
    //divide a box along its longest RGB axis to create 2 smaller boxes and return these
    private ColorBox[] splitBox(ColorBox bx){
    
        int m = bx.level; //store the current 'level'
        int d = findMaxDimension(bx); //the dimension to split along
    
        //get the median only counting along the longest RGB dimension
        Map<Integer, MyColor> cols = bx.cols;
        Iterator<MyColor> it = cols.values().iterator();
        float c = 0.0F;
        while(it.hasNext()){
            MyColor mc = it.next();
            c += mc.rgb[d]; 
        }
    
        float median = c / (float)cols.size();
    
        //the two Hashmaps to contain all the colours in the original box
        HashMap<Integer, MyColor> left = new HashMap<>();
        HashMap<Integer, MyColor> right = new HashMap<>();
    
        Iterator<MyColor> itr = cols.values().iterator();
        while(itr.hasNext()){
            MyColor mc = itr.next();

            //putting each colour in the appropriate box
            if(mc.rgb[d] <= median){
                left.put(mc.col,mc);
            }else{
                right.put(mc.col,mc);
            }
        }
    
        ColorBox [] toReturn = new ColorBox [2];
        toReturn[0] = new ColorBox(left, m+1); //the 'level' has increased 
        toReturn[1] = new ColorBox(right, m+1);
    
        return toReturn;
    
    }
    
    //the method to find and return the longest axis of the box as the one to divide along.
    private int findMaxDimension(ColorBox bx){
    
        float [] dims = new float [3];
        //the length of each is measured as the (max value - min value)
        dims[0] = bx.rmax - bx.rmin;
        dims[1] = bx.gmax - bx.gmin;
        dims[2] = bx.bmax - bx.bmin;
    
        float sizeMax = findMinOrMax(dims, 1);
        if(sizeMax == dims[0]){
            return RED;
        }else if(sizeMax == dims[1]){
            return GREEN;
        }else{
            return BLUE;
        }
    }


    /**
     * Returns the average colour of all the colours contained by the given box.
     * 
     * @param bx The color box
     * 
     * @return The average color in the box.
     */
    private int averageColor(ColorBox bx){

        Map<Integer, MyColor> cols = bx.cols;
        Iterator<MyColor> it = cols.values().iterator();
        float [] rgb = {0.0F, 0.0F, 0.0F}; //start at zero
        while(it.hasNext()){
            MyColor mc = it.next();
            rgb[RED] += mc.rgb[RED];
            rgb[GREEN] += mc.rgb[GREEN];
            rgb[BLUE] += mc.rgb[BLUE];
//            for(int i = 0; i < 3; i++){
//                rgb[i] += mc.rgb[i]; //sum of each channel stored separately
//            }
        }

        float avgRed = rgb[RED] / (float) cols.size();
        float avgGreen = rgb[GREEN] / (float) cols.size();
        float avgBlue = rgb[BLUE] / (float) cols.size();

        return color(avgRed, avgGreen, avgBlue);
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


    private int blue(int col1) {
        return col1 & 0xff;
    }

    private int green(int col1) {
        return (col1 >> 8) & 0xff;
    }


    private int red(int col1) {
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
     * Get the minimum OR maximum from an array of floats
     * 
     * @param f
     * @param k Flag to determine whether to find the minimum or the maximum (0 = min, 1 = max)
     *
     * @return
     */
    private float findMinOrMax(float[] f, int k){
        if(f.length > 0) {
            float m = f[0];
            for(int i = 1; i < f.length; i++){
                //if k is 0 the minimum is required. Otherwise return the maximum. 
                if(k == 0){
                    m = Math.min(m,f[i]);
                }else{
                    m = Math.max(m,f[i]);
                }
            } 
            return m;
        }

        return 0.0F;
    }

    /**
     * @param f A list of Float values.
     * @return The smallest float value in the list or 0.0F if the list does not contain any element. 
     */
    private Float findMin(Collection<Float> f) {
        return f.stream().min((a, b) -> Float.compare(a.floatValue(), b.floatValue())).orElse(0.0F);
    }

    /**
     * @param f A list of Float values.
     * @return The largest float value in the list or 0.0F if the list does not contain any element. 
     */
    private Float findMax(Collection<Float> f) {
        return f.stream().max((a, b) -> Float.compare(a.floatValue(), b.floatValue())).orElse(0.0F);
    }

    private void testFindMin() {
        List<Float> l1 = Arrays.asList();
        System.err.printf("%s, %s, %s\n", l1, findMin(l1), findMax(l1));

        List<Float> l2 = Arrays.asList(5.0F);
        System.err.printf("%s, %s, %s\n", l2, findMin(l2), findMax(l2));

        List<Float> l3 = Arrays.asList(5.0F, 3.0F);
        System.err.printf("%s, %s, %s\n", l3, findMin(l3), findMax(l3));

        List<Float> l4 = Arrays.asList(5.0F, 2.0F, 3.0F, 2.5F);
        System.err.printf("%s, %s, %s\n", l4, findMin(l4), findMax(l4));

        List<Float> l5 = Arrays.asList(5.0F, 2.0F, 8.0F, 3.0F, 2.5F);
        System.err.printf("%s, %s, %s\n", l5, findMin(l5), findMax(l5));
    }

    private int color(float avgRed, float avgGreen, float avgBlue) {
        return ((int) avgRed << 16) + ((int) avgGreen << 8) + (int) avgBlue;
    }

    
    private int col2int(Color color) {
        int r = (int) (color.getRed()*255);
        int g = (int) (color.getGreen()*255);
        int b = (int) (color.getBlue()*255);
        return ( (r << 16) + (g << 8) + b);
    }

    //class to store colours for use in the ColorBoxes
    private class MyColor{
    
        int col;
        int count;
        float [] rgb = new float[3];
    
        //constructor takes the color as an int
        MyColor(int col){
            this.col = col;
            
            //bitshifting faster than red(col) etc. 
            rgb[RED]= (col >> 16) & 0xFF;
            rgb[GREEN]=(col >> 8) & 0xFF;
            rgb[BLUE]= col & 0xFF;
            
            count = 1;  // Note: in this algorithm, count (which determines the color spectrum) is not used...
        }
    
        void increment(){
            count++;
        }
    }
    

    /**
     * 
     */
    class ColorBox{
        float rmin, rmax, gmin, gmax, bmin, bmax;   // the bounds of the color box
        Map<Integer, MyColor> cols;                 // all colors which are inside the box boundaries
        int level;
    
        // constructor takes the colours contained by this box and its level of "depth"
        ColorBox(Map<Integer, MyColor> cols, int level){
            this.cols = cols;
            this.level = level;

/** Convert the Colors into their individual channels */
            // Compute the box boundaries.
            // 3 temporary arrays used for getting the min/max of each RGB channel
            float [] reds = new float [cols.size()];
            float [] greens = new float [cols.size()];
            float [] blues = new float [cols.size()];

            Iterator<MyColor> it = cols.values().iterator();
            int index = 0;
    
            while(it.hasNext()){
                MyColor mc = it.next();
                reds[index] = mc.rgb[RED];
                greens[index] = mc.rgb[GREEN];
                blues[index] = mc.rgb[BLUE];
                index++;
            }
/******************************************************/

            // we need the min/max to determine which axis to split along
            rmin = findMinOrMax(reds, 0);
            rmax = findMinOrMax(reds, 1);
            gmin = findMinOrMax(greens, 0);
            gmax = findMinOrMax(greens, 1);
            bmin = findMinOrMax(blues, 0);
            bmax = findMinOrMax(blues, 1);
        }
    }

}
