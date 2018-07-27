package afester.javafx.examples.ttf;

import java.math.BigInteger;
import java.util.Comparator;

import javafx.scene.paint.Color;

public class HilbertSorter implements Comparator<Color> {
    private HilbertCurve hc = HilbertCurve.bits(8).dimensions(3);

    @Override
    public int compare(Color arg0, Color arg1) {
        long[] coord0 = new long[] {(int) (arg0.getRed() * 255), (int) (arg0.getGreen() * 255), (int) (arg0.getBlue() * 255)};
        long[] coord1 = new long[] {(int) (arg1.getRed() * 255), (int) (arg1.getGreen() * 255), (int) (arg1.getBlue() * 255)};

        BigInteger idx0 = hc.index(coord0);
        BigInteger idx1 = hc.index(coord1);
        
        return Long.compare(idx0.longValue(), idx1.longValue());
    }
}
