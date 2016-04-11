/*
 * Copyright 2016 Andreas Fester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

/**
 * Utility class to extract red, green and blue color channels from an image. 
 */
public class ColorSeparator {

    /** The source image. */
    private Image sourceImage;

    /**
     * Creates a ColorSeparator and specified the input image. 
     *
     * @param image The input image to use.
     */
    public ColorSeparator(Image image) {
        sourceImage = image;
    }

    /**
     * Creates a color mask in the size of the source image.
     * 
     * @param color  The color to use for the mask
     *
     * @return A color mask with the given color and the size of the source image. 
     */
    public ColorInput createColorMask(Color color) {
        ColorInput colorInput = new ColorInput();
        colorInput.setPaint(color);
        colorInput.setX(0);
        colorInput.setY(0);
        colorInput.setWidth(sourceImage.getWidth());
        colorInput.setHeight(sourceImage.getHeight());
        return colorInput;
    }

    
    /**
     * Create a color Blend effect for the source image.
     *
     * @param color The color to use for the top source area.
     *
     * @return A Blend effect of type MULTIPLY and with a top source which 
     *         is a solid area of the given color. 
     */
    public Blend createColorBlend(Color color) {
        ColorInput mask = createColorMask(color);
        Blend blend = new Blend();
        blend.setMode(BlendMode.MULTIPLY);
        blend.setTopInput(mask);
        return blend;
    }

    
    /**
     * Returns a color channel for the given color. 
     *
     * @param color The color to use for the mask.
     *
     * @return An Image where the given color has been filtered from the source
     *         image.   
     */
    private Image getChannel(Color color) {
        Node img = new ImageView(sourceImage);

        Blend blend = createColorBlend(color);
        img.setEffect(blend);

        SnapshotParameters params = new SnapshotParameters();
        Image result = img.snapshot(params, null);

        return result;
    }

    /**
     * @return An image which is identical to the input image, but
     *         contains the red color channel only. 
     */
    public Image getRedChannel() {
        return getChannel(Color.RED);  // #ff0000
    }

    /**
     * @return An image which is identical to the input image, but
     *         contains the green color channel only. 
     */
    public Image getGreenChannel() {
        return getChannel(Color.LIME); // #00ff00
    }

    /**
     * @return An image which is identical to the input image, but
     *         contains the blue color channel only. 
     */
    public Image getBlueChannel() {
        return getChannel(Color.BLUE); // #0000ff
    }

    /**
     * Brain-dead implementation of a color separator which returns the
     * blue color channel of an image.
     *
     * @return An image which is identical to the source image, but
     *         contains the blue color channel only. 
     * 
     * @deprecated Use getBlueChannel instead
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
