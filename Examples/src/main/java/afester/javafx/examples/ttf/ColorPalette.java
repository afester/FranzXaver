package afester.javafx.examples.ttf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class ColorPalette {

    private Map<Color, Integer> colorMap = new HashMap<>();
    private int maxIdx = 0;

    public ColorPalette() {
    }
    
    /**
     * Adds all colors from a given image to this color palette.
     *
     * @param img
     */
    public void addColors(Image img) {
        PixelReader reader = img.getPixelReader();
        for (int y = 0; y < img.getHeight(); y++) {
           for (int x = 0; x < img.getWidth(); x++) {
              Color c = reader.getColor(x, y);
              addColor(c);
           }
        }
    }

    /**
     * Adds one single color to this color palette.
     *
     * @param c
     */
    public void addColor(Color c) {
        if (!colorMap.containsKey(c)) {
            colorMap.put(c,  maxIdx++);
        }
    }

    public int getSize() {
        return colorMap.size();
    }

    public Set<Color> getColors() {
        return colorMap.keySet();
    }

    class Tuple<A, B> {
        public Tuple(A a, B b) {
            
        }
    }

    /**
     * @return A list of colors sorted by their index.
     */
    public List<Color> getColorList() {
        
        // Create a list of Set entries
        List<Map.Entry<Color, Integer>> sorted = new ArrayList<>(); 
        colorMap.entrySet().forEach(e -> {
            sorted.add(e);
        });

        // sort the list according to the entry value
        Collections.sort(sorted, new Comparator<Map.Entry<Color, Integer>>() {

            @Override
            public int compare(Entry<Color, Integer> arg0, Entry<Color, Integer> arg1) {
                return Integer.compare(arg0.getValue(), arg1.getValue());
            }
        });

        // convert the list of Entries to a list of keys
        List<Color> result = new ArrayList<>();        
        sorted.forEach(e -> {
            result.add(e.getKey());
        });
        return result;
    }
    

    public List<Color> getSortedColors() {
        List<Color> result = new ArrayList<>();        
        colorMap.keySet().forEach(e -> {
            result.add(e);
        });

        Collections.sort(result, new Comparator<Color>() {
            private double step(Color arg0, int repetitions) {
                double lum = Math.sqrt( .241 * arg0.getRed() + .691 * arg0.getGreen() + .068 * arg0.getBlue() );

                float[] hsb = java.awt.Color.RGBtoHSB((int) (arg0.getRed() * 255), (int) (arg0.getGreen() * 255), (int) (arg0.getBlue() * 255), null);
                int h2 = (int) (hsb[0] * repetitions);
                int lum2 = (int) (lum * repetitions);
                double v2 = hsb[2] * repetitions;

                if (h2 % 2 == 1) {
                    v2 = repetitions - v2;
                    lum = repetitions - lum;
                }

                return v2;
            }

            @Override
            public int compare(Color arg0, Color arg1) {
                //return Double.compare(arg0.getBrightness(), arg1.getBrightness());
                return Double.compare(step(arg0, 8), step(arg1, 8));
            }

        });

        return result;
    }

}
