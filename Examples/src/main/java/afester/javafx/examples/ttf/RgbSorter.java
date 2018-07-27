package afester.javafx.examples.ttf;

import java.util.Comparator;

import javafx.scene.paint.Color;

public class RgbSorter implements Comparator<Color> {

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

}
