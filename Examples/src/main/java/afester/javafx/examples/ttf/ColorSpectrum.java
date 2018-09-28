package afester.javafx.examples.ttf;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class ColorSpectrum {

    private Map<Color, Integer> colorSpectrum = new HashMap<>();

    public ColorSpectrum() {
    }

    /**
     * Adds all colors from a given image to this color spectrum.
     *
     * @param img The image for which to add the color distribution. 
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
     * Adds one single color to this color spectrum.
     *
     * @param c
     */
    public void addColor(final Color c) {
    	Integer count = colorSpectrum.get(c);
        if (count == null) {
        	count = 1;
        } else {
        	count = count + 1;
        }
        colorSpectrum.put(c, count);
    }

    /**
     * Dumps the current spectrum of colors to an output stream.
     * The output contains a list of colors and the number of occurences of that color.
     * 
     * @param out The PrintStream where to dump the current spectrum.
     */
    public void dumpSpectrum(final PrintStream out) {
    	colorSpectrum.forEach((k, v) -> out.printf("  %s: %s\n", k, v));
    }
}
