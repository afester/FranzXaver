package afester.javafx.tools;

import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ColorSeparator {

    private Image sourceImage;
    
    public ColorSeparator(Image image) {
        sourceImage = image;
    }

    /**
     * Creates a color mask in the size of the given image.
     * 
     * @param source The source image
     * @param color  The color to use for the mask
     *
     * @return A color mask with the given color and the size of the given image. 
     */
    private ColorInput createColorMask(Image source, Color color) {
        ColorInput colorInput = new ColorInput();
        colorInput.setPaint(color);
        colorInput.setX(0);
        colorInput.setY(0);
        colorInput.setWidth(source.getWidth());
        colorInput.setHeight(source.getHeight());
        return colorInput;
    }

    /**
     * 
     * @param source
     * @param color
     * @return
     */
    private Image getChannel(Image source, Color color) {
        Node img = new ImageView(sourceImage);

        ColorInput mask = createColorMask(sourceImage, color);
        Blend blend = new Blend();
        blend.setMode(BlendMode.MULTIPLY);
        blend.setTopInput(mask);
        img.setEffect(blend);

        SnapshotParameters params = new SnapshotParameters();
        Image result = img.snapshot(params, null);

        return result;
    }

    /**
     * 
     * @return
     */
    public Image getRedChannel() {
        return getChannel(sourceImage, Color.RED);  // #ff0000
    }

    /**
     * 
     * @return
     */
    public Image getGreenChannel() {
        return getChannel(sourceImage, Color.LIME); // #00ff00
    }

    /**
     * 
     * @return
     */
    public Image getBlueChannel() {
        return getChannel(sourceImage, Color.BLUE); // #0000ff
    }

    /**
     * 
     * @return
     */
//    public Image getAlphaChannel() {
//        return getChannel(sourceImage, new Color(0.0, 0.0, 0.0, 1.0)); // #000000ff
//    }

    /**
     * @deprecated Use getBlueChannel instead
     *
     * @return
     */
    public Image getBlueChannel1() {
        PixelReader pr = sourceImage.getPixelReader();
        int width = (int) sourceImage.getWidth();
        int height = (int) sourceImage.getHeight();

        WritableImage result = new WritableImage(width, height);
        PixelWriter pw = result.getPixelWriter();
        for (int x = 0;  x < width;  x++) {
            for (int y = 0; y < height;  y++) {
                Color col = pr.getColor(x, y);
                pw.setColor(x, y, new Color(0, 0, col.getBlue(), 1.0));    
            }
        }

        return result;
    }

}
