package afester.javafx.examples.ttf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import afester.javafx.examples.ttf.MedianCut.MyColor;


/**
 * 
 */
public class ColorBox{
    final int RED = 0;
    final int GREEN = 1;
    final int BLUE = 2;

    final Float rmin, rmax, gmin, gmax, bmin, bmax;   // the bounds of the color box
    final Map<Integer, MyColor> cols;                 // all colors which are inside the box boundaries
    final int level;

    /**
     * Creates a new ColorBox.
     * 
     * @param cols  The colors which are contained in this box.
     * @param level The depth of this ColorBox instance.
     */
    public ColorBox(Map<Integer, MyColor> cols, int level){
        this.cols = cols;
        this.level = level;

        // Compute the box boundaries

        // Convert the Colors into their individual channels
        List<Float> reds = new ArrayList<>(cols.size());
        List<Float> greens = new ArrayList<>(cols.size());
        List<Float> blues = new ArrayList<>(cols.size());

        cols.values().forEach(mc -> {
            reds.add(mc.rgb[RED]);
            greens.add(mc.rgb[GREEN]);
            blues.add(mc.rgb[BLUE]);
        });

        // we need the min/max to determine which axis to split along
        rmin = FloatArrayTools.findMin(reds);
        rmax = FloatArrayTools.findMax(reds);
        gmin = FloatArrayTools.findMin(greens);
        gmax = FloatArrayTools.findMax(greens);
        bmin = FloatArrayTools.findMin(blues);
        bmax = FloatArrayTools.findMax(blues);
    }


    /**
     * Determines the longest axis of this color box.
     *
     * @return The index (RED, GREEN, BLUE) of the longest axis of this ColorBox.
     */
    public int findMaxDimension(){

        final List<Float> dims = new ArrayList<>(3);

        // the length of each axis is measured as the (max value - min value)
        dims.add(rmax - rmin);
        dims.add(gmax - gmin);
        dims.add(bmax - bmin);

        switch(FloatArrayTools.findIndexOfMax(dims)) {
            case 0 : return RED;
            case 1 : return GREEN;
            default: return BLUE;
        }
    }
    
    
    /**
     * Divides a box along its longest RGB axis to create 2 smaller boxes and return these
     * 
     * @return
     */
    public ColorBox[] splitBox(){
        final int d = this.findMaxDimension(); // the dimension to split along

        // get the median only counting along the longest RGB dimension
        float c = 0.0F;
        for (MyColor mc : cols.values()) {
            c += mc.rgb[d]; 
        }

        final float median = c / (float) cols.size();

        // the two Hashmaps to contain all the colours in the original box
        Map<Integer, MyColor> left = new HashMap<>();
        Map<Integer, MyColor> right = new HashMap<>();

        for (MyColor mc : cols.values()) {
            // putting each colour in the appropriate box
            if (mc.rgb[d] <= median){
                left.put(mc.col, mc);
            } else {
                right.put(mc.col, mc);
            }
        }

        ColorBox [] toReturn = new ColorBox [2];
        toReturn[0] = new ColorBox(left, level + 1); // the 'level' has increased 
        toReturn[1] = new ColorBox(right, level + 1);

        return toReturn;
    }


    /**
     * Returns the average colour of all the colours contained in this box.
     * 
     * @return The average color in this box.
     */
    public int averageColor(){
        float [] rgb = {0.0F, 0.0F, 0.0F}; //start at zero

        for (MyColor mc : cols.values()) {
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


    private int color(float avgRed, float avgGreen, float avgBlue) {
        return ((int) avgRed << 16) + ((int) avgGreen << 8) + (int) avgBlue;
    }
}
