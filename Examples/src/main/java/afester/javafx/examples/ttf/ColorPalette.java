package afester.javafx.examples.ttf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class ColorPalette {

    private Map<Color, Integer> colorMap = new HashMap<>();
    private int maxIdx = 0;

    public ColorPalette() {
    }
    
    public void addColors(Image img) {
        PixelReader reader = img.getPixelReader();
        for (int y = 0; y < img.getHeight(); y++) {
           for (int x = 0; x < img.getWidth(); x++) {
              // int argb = reader.getArgb(x, y);
              Color c = reader.getColor(x, y);
              if (!colorMap.containsKey(c)) {
                  colorMap.put(c,  maxIdx++);
              }
           }
        }
    }

    public int getSize() {
        return colorMap.size();
    }

    public Set<Color> getColors() {
        return colorMap.keySet();
    }

}
