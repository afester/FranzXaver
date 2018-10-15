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
        final long start = System.currentTimeMillis();

        int[] repCols = findRepresentativeColors(orig, maxColors);
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
    
        final List<Float> dims = new ArrayList<>(3);// Float[3];

        //the length of each is measured as the (max value - min value)
        dims.add(bx.rmax - bx.rmin);
        dims.add(bx.gmax - bx.gmin);
        dims.add(bx.bmax - bx.bmin);

        switch(findIndexOfMax(dims)) {
            case 0 : return RED;
            case 1 : return GREEN;
            default: return BLUE;
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
            
            // sum of each channel stored separately
            rgb[RED] += mc.rgb[RED];
            rgb[GREEN] += mc.rgb[GREEN];
            rgb[BLUE] += mc.rgb[BLUE];
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

    /**
     * @param f A list of Float values.
     * @return The index of the largest float value in the list or -1 if the list does not contain any element. 
     */
    private int findIndexOfMax(Collection<Float> f) {
        Float maxVal = Float.NEGATIVE_INFINITY;
        int result = -1;
        int idx = 0;
        for (Float val : f) {
            if (val > maxVal) {
                maxVal = val;
                result = idx;
            }
            idx++;
        }

        return result;
    }

    private void testFindMin() {
        List<Float> l1 = Arrays.asList();
        System.err.printf("%s, %s, %s (%s)\n", l1, findMin(l1), findMax(l1), findIndexOfMax(l1));

        List<Float> l2 = Arrays.asList(5.0F);
        System.err.printf("%s, %s, %s (%s)\n", l2, findMin(l2), findMax(l2), findIndexOfMax(l2));

        List<Float> l3 = Arrays.asList(5.0F, 3.0F);
        System.err.printf("%s, %s, %s (%s)\n", l3, findMin(l3), findMax(l3), findIndexOfMax(l3));

        List<Float> l4 = Arrays.asList(5.0F, 2.0F, 3.0F, 2.5F);
        System.err.printf("%s, %s, %s (%s)\n", l4, findMin(l4), findMax(l4), findIndexOfMax(l4));

        List<Float> l5 = Arrays.asList(5.0F, 2.0F, 8.0F, 3.0F, 2.5F);
        System.err.printf("%s, %s, %s (%s)\n", l5, findMin(l5), findMax(l5), findIndexOfMax(l5));
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
        final Float rmin, rmax, gmin, gmax, bmin, bmax;   // the bounds of the color box
        final Map<Integer, MyColor> cols;                 // all colors which are inside the box boundaries
        final int level;
    
        // constructor takes the colours contained by this box and its level of "depth"
        ColorBox(Map<Integer, MyColor> cols, int level){
            this.cols = cols;
            this.level = level;

/** Convert the Colors into their individual channels */
            // Compute the box boundaries.
            // 3 temporary arrays used for getting the min/max of each RGB channel
            List<Float> reds = new ArrayList<>(cols.size());
            List<Float> greens = new ArrayList<>(cols.size());
            List<Float> blues = new ArrayList<>(cols.size());

            cols.values().forEach(mc -> {
                reds.add(mc.rgb[RED]);
                greens.add(mc.rgb[GREEN]);
                blues.add(mc.rgb[BLUE]);
            });
/******************************************************/

            // we need the min/max to determine which axis to split along
            rmin = findMin(reds);
            rmax = findMax(reds);
            gmin = findMin(greens);
            gmax = findMax(greens);
            bmin = findMin(blues);
            bmax = findMax(blues);
        }
    }
}
