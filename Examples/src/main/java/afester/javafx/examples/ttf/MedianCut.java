package afester.javafx.examples.ttf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    
    //takes the original image and amount of colours required in the quantized image.
    //returns the quantized image.
    public Image medianCut(Image orig, int maxColors){
        int[] repCols = findRepresentativeColors(orig, maxColors);
        System.err.println(repCols.length);
        return quantizeImage(orig, repCols);
    }


//    //takes the original image and an array of new colours of size maxColors.
//    //returns the quantized image.
    Image quantizeImage(Image img, int [] newCols){
        //the new image should be the same size as the original
        WritableImage newImg = new WritableImage((int) img.getWidth(), (int) img.getHeight());
        PixelReader reader = img.getPixelReader();
        PixelWriter writer = newImg.getPixelWriter();

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int c = col2int(reader.getColor(x, y)); // img.pixels[i]

//        //find the closest colour in the array to the original pixel's colour
//        for(int i=0; i<img.pixels.length; i++){
                int index = 0;
                float closest = distanceToRGB(c, newCols[0]);
            for(int j=1;j<newCols.length;j++){
                if(closest > distanceToRGB(c,newCols[j])){
                    closest = distanceToRGB(c,newCols[j]);
                    index = j;
                }
            }

//            //set the new image's pixel to the closest colour in the array
//            newImg.pixels[i] = newCols[index];
                writer.setColor(x,  y, new Color(red(newCols[index]) / 255.0, green(newCols[index]) / 255.0, blue(newCols[index])  / 255.0, 1.0));
            }
        }

//        img.updatePixels();
//        newImg.updatePixels();

        return newImg;
    }

    //the main loop to subdivide the large colour space into the required amount 
    //of smaller boxes. return the array of the average colour for each of these boxes.
    int [] findRepresentativeColors(Image orig, int maxColors){
    
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
    
            ArrayList colorBoxes = new ArrayList(); //where the boxes will be stored
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
    ColorBox findBoxToSplit(ArrayList listOfBoxes){
    
        ArrayList canBeSplit = new ArrayList();
    
        for(int i = 0; i<listOfBoxes.size(); i++){
            ColorBox cb = (ColorBox)listOfBoxes.get(i);
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
    
            ColorBox minBox = (ColorBox)canBeSplit.get(0);
            int minLevel = minBox.level;
    
            for(int i=1; i<canBeSplit.size(); i++){
                ColorBox test = (ColorBox)canBeSplit.get(i);
                if(minLevel > test.level){
                    minLevel = test.level;
                    minBox = test;
                }
            }
    
            return minBox;
    
        }
    
    }
    
    //divide a box along its longest RGB axis to create 2 smaller boxes and return these
    ColorBox [] splitBox(ColorBox bx){
    
        int m = bx.level; //store the current 'level'
        int d = findMaxDimension(bx); //the dimension to split along
    
        //get the median only counting along the longest RGB dimension
        Map cols = bx.cols;
        Iterator it = cols.values().iterator();
        float c = 0.0F;
        while(it.hasNext()){
            MyColor mc = (MyColor)it.next();
            c += mc.rgb[d]; 
        }
    
        float median = c / (float)cols.size();
    
        //the two Hashmaps to contain all the colours in the original box
        HashMap left = new HashMap();
        HashMap right = new HashMap();
    
        Iterator itr = cols.values().iterator();
        while(itr.hasNext()){
            MyColor mc = (MyColor)itr.next();
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
    int findMaxDimension(ColorBox bx){
    
        float [] dims = new float [3];
        //the length of each is measured as the (max value - min value)
        dims[0] = bx.rmax - bx.rmin;
        dims[1] = bx.gmax - bx.gmin;
        dims[2] = bx.bmax - bx.bmin;
    
        float sizeMax = findMinMax(dims,1);
        if(sizeMax == dims[0]){
            return RED;
        }else if(sizeMax == dims[1]){
            return GREEN;
        }else{
            return BLUE;
        }
    }
    
    //get the average colour of all the colours contained by the given box
    int averageColor(ColorBox bx){
    
        Map cols = bx.cols;
        Iterator it = cols.values().iterator();
        float [] rgb = {0.0F, 0.0F, 0.0F}; //start at zero
        while(it.hasNext()){
            MyColor mc = (MyColor)it.next();
            for(int i=0; i<3; i++){
                rgb[i] += mc.rgb[i]; //sum of each channel stored separately
            }
        }
        float avgRed = rgb[RED] / (float)cols.size();
        float avgGreen = rgb[GREEN] / (float)cols.size();
        float avgBlue = rgb[BLUE] / (float)cols.size();
    
        return color(avgRed,avgGreen,avgBlue);
    }
    
    /**
        Some helper functions
    */
    
    //get the distance between two colours in Euclidian space
    float distanceToRGB(int col1, int col2){
        float redDiff = Math.abs(red(col1)-red(col2));
        float greenDiff = Math.abs(green(col1)-green(col2));
        float blueDiff = Math.abs(blue(col1)-blue(col2));

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


    //called at the very start to determine the colours in the original image
    Map<Integer, MyColor> findOriginalColors(Image orig){

        //entries in the hashmap are stored in (key,value) pairs
        //key = int colour , value = MyColor colorObject 
        Map<Integer, MyColor> toReturn = new HashMap<>();

        PixelReader reader = orig.getPixelReader();
        for (int y = 0; y < orig.getHeight(); y++) {
           for (int x = 0; x < orig.getWidth(); x++) {
               int c = col2int(reader.getColor(x, y)); // orig.pixels[i]

               if(toReturn.containsKey(c)){ 
                   MyColor temp = (MyColor)toReturn.get(c); // orig.pixels[i]);
                   temp.increment(); //increase its 'count' value
               }else{
                   MyColor toAdd = new MyColor(c); // orig.pixels[i]);
                   toReturn.put(c, toAdd);
               }
           }
        }

        return toReturn;
    }

    //get the minimum OR maximum from an array of floats
    float findMinMax(float [] f, int k){
        if(f.length>0){
            float m = f[0];
            for(int i =1; i<f.length; i++){
                //if k is 0 the minimum is required. Otherwise return the maximum. 
                if(k==0){
                    m = Math.min(m,f[i]);
                }else{
                    m = Math.max(m,f[i]);
                }
            } 
            return m;
        }else{
            return 0.0F;
        }
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
    class MyColor{
    
        int col, count;
        float [] rgb = new float[3];
    
        //constructor takes the color as an int
        MyColor(int col){
            this.col = col;
            
            //bitshifting faster than red(col) etc. 
            rgb[RED]= (col >> 16) & 0xFF;
            rgb[GREEN]=(col >> 8) & 0xFF;
            rgb[BLUE]= col & 0xFF;
            
            count = 1;
        }
    
        void increment(){
            count++;
        }
    }
    
    //the box class containing VERY USEFUL values
    class ColorBox{
    
        float rmin,rmax,gmin,gmax,bmin,bmax;
        Map cols;
        int level;
    
        //constructor takes the colours contained by this box and its level of "depth"
        ColorBox(Map cols, int level){
            this.cols = cols;
            this.level = level;
    
            //3 temporary arrays used for getting the min/max of each RGB channel
            float [] reds = new float [cols.size()];
            float [] greens = new float [cols.size()];
            float [] blues = new float [cols.size()];
    
            Iterator it = cols.values().iterator();
            int index = 0;
    
            while(it.hasNext()){
                MyColor mc = (MyColor)it.next();
                reds[index] = mc.rgb[RED];
                greens[index] = mc.rgb[GREEN];
                blues[index] = mc.rgb[BLUE];
                index++;
            }
    
            //we need the min/max to determine which axis to split along
            rmin = findMinMax(reds,0);
            rmax = findMinMax(reds,1);
            gmin = findMinMax(greens,0);
            gmax = findMinMax(greens,1);
            bmin = findMinMax(blues,0);
            bmax = findMinMax(blues,1);
    
        }
    
    }

}
