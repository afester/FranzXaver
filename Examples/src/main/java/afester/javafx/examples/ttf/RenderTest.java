package afester.javafx.examples.ttf;

import java.util.Random;

import afester.javafx.examples.image.ImageConverter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class RenderTest extends Application {

    public static void main(String[] args) {
        // The following properties are required on Linux for improved text rendering
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    private WritableImage img;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Color Reducer");

        // createImage();
        // createRandomImage();
        // createLineImage();
        createCircleImage();

        ImageView iv = new ImageView(img);
        HBox g = new HBox();
        g.getChildren().add(iv);

        // for (short rgb565 : Glyph.palette) {
        // int newColor = ImageConverter.fromRGB565ToRGB(rgb565);
        // Color color = new Color(MedianCut.red(newColor) / 255.0,
        // MedianCut.green(newColor) / 255.0,
        // MedianCut.blue(newColor) / 255.0, 1.0);
        //
        // Rectangle r = new Rectangle(20, 20);
        // r.setFill(color);
        // r.setStroke(null);
        // g.getChildren().add(r);
        // }
        //

        Scene scene = new Scene(g);

        stage.setScene(scene);
        stage.show();
    }

    private void createImage() {
        img = new WritableImage(Glyph.WIDTH, Glyph.HEIGHT);
        PixelWriter pw = img.getPixelWriter();

        int reader = 0;
        int count = 0;
        Color color = null;
        for (int y = 0; y < Glyph.HEIGHT; y++) {
            for (int x = 0; x < Glyph.WIDTH; x++) {

                if (count == 0) {
                    int value = (int) (Glyph.g0[reader++] & 0xff);
                    count = value >> 4;
                    int colorIdx = value & 0x0f;
                    short rgb565Color = Glyph.palette[colorIdx];

                    int newColor = ImageConverter.fromRGB565ToRGB(rgb565Color);
                    color = new Color(MedianCut.red(newColor) / 255.0, MedianCut.green(newColor) / 255.0,
                            MedianCut.blue(newColor) / 255.0, 1.0);
                }

                pw.setColor(x, y, color);
                count--;
            }
        }
    }

    private void createRandomImage() {
        img = new WritableImage(400, 400);
        PixelWriter pw = img.getPixelWriter();

        Random r = new Random();
        for (int y = 0; y < img.getWidth(); y++) {
            for (int x = 0; x < img.getHeight(); x++) {
                Color color = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat(), 1.0);
                pw.setColor(x, y, color);
            }
        }

    }

    private PixelWriter gpw;

    private void createLineImage() {
        img = new WritableImage(400, 400);
        PixelWriter pw = img.getPixelWriter();
        gpw = pw;

        for (int x = 10; x < 390; x += 10) {
            renderLine2(200, 390, x, 10);
        }

        for (int x = 10; x < 390; x += 10) {
            renderLine2(10, 200, 390, x);
        }

        for (int x = 10; x < 390; x += 10) {
            renderLine2(390, 200, 10, x);
        }

        for (int x = 10; x < 390; x += 10) {
            renderLine2(200, 10, x, 390);
        }

    }

    private void createCircleImage() {
        img = new WritableImage(480, 320);
        PixelWriter pw = img.getPixelWriter();
        gpw = pw;

//        final Random rnd = new Random();
//        setStroke(null);
//        for (int r = 150; r > 30; r -= 10) {
//            setFill(new Color(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat(), 1.0));
//            renderCircle(200, 200, r);
//        }

      setStroke(null);
      int red = 255;
      for (int r = 80; r > 10; r -= 5, red -= 16) {
    	  // float red = (float) (r - 10) / 80.0F;     // 0..1
    	  //System.err.printf("%s\n", red * 255);
          setFill(new Color(red / 255.0, 0.0, red / 255.0, 1.0));
          renderCircle(240, 160, r);
      }

//        setFill(Color.GREEN);
//        renderCircle(200, 200, 40);
    }

    private Color strokeColor = Color.BLACK;
    private Color fillColor = null;

    void setStroke(Color c) {
        strokeColor = c;
    }

    void setFill(Color c) {
        fillColor = c;
    }

    void setPixel(int x, int y) {
        gpw.setColor(x, y, strokeColor);
    }

    /**
     * An implementation of the Bresenham algorithm to render generic lines on
     * raster devices.
     *
     * @param x1
     *            X position of starting point
     * @param y1
     *            Y position of starting point
     * @param x2
     *            X position of destination point
     * @param y2
     *            Y position of destination point
     */
    private void renderLine(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;

        // determine sign of increment
        final int incx = Integer.signum(dx); // -1, 0, 1
        final int incy = Integer.signum(dy); // -1, 0, 1
        if (dx < 0) {
            dx = -dx;
        }
        if (dy < 0) {
            dy = -dy;
        }

        // Set the increment values, depending on which direction is "faster"
        int pdx, pdy, es, el;
        if (dx > dy) {
            // x is the faster direction

            // parallel step increments x only
            pdx = incx;
            pdy = 0;

            // error steps fast, slow
            es = dy;
            el = dx;
        } else {
            // y is the faster direction

            // parallel step increments y only
            pdx = 0;
            pdy = incy;

            // error steps fast, slow
            es = dx;
            el = dy;
        }

        // initialize loop variables
        int x = x1;
        int y = y1;
        int err = el / 2;

        setPixel(x, y);

        // t is counting pixels, el is also the number of iterations
        for (int t = 0; t < el; ++t) {
            // update error term
            err -= es;
            if (err < 0) {
                // correct error term (ensure >= 0)
                err += el;

                // step in slow direction (diagonal step)
                x += incx;
                y += incy;
            } else {
                // step in fast direction (parallel step)
                x += pdx;
                y += pdy;
            }

            setPixel(x, y);
        }
    }

    /**
     * A compact version of the Bresenham algorithm, without calling any runtime
     * library function
     * 
     * @param x1    X position of starting point
     * @param y1    Y position of starting point
     * @param x2    X position of destination point
     * @param y2    Y position of destination point
     */
    void renderLine2(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1; // dx = abs(x2 - x1)
        if (dx < 0) {
            dx = -dx;
        }
        int dy = y2 - y1; // dy = abs(y2 - y1)
        if (dy < 0) {
            dy = -dy;
        }
        final int sx = x1 < x2 ? 1 : -1; // sx = sgn(x2 - x1)
        final int sy = y1 < y2 ? 1 : -1; // sy = sgn(y2 - y1)

        int err = dx - dy;
        while (true) {
            setPixel(x1, y1);

            if (x1 == x2 && y1 == y2)
                break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    /**
     * A simple circle rendering function based on the Bresenham algorithm.
     *
     * @param x0
     * @param y0
     * @param radius
     */
    void renderCircle(int x0, int y0, int radius) {
        int f = 1 - radius;
        int ddF_x = 0;
        int ddF_y = -2 * radius;
        int x = 0;
        int y = radius;

        if (fillColor != null) {
            Color oldColor = strokeColor;
            strokeColor = fillColor;
            renderLine(x0 - radius, y0, x0 + radius, y0);
            strokeColor = oldColor;
        }

        if (strokeColor != null) {
            setPixel(x0, y0 + radius);
            setPixel(x0, y0 - radius);
            setPixel(x0 + radius, y0);
            setPixel(x0 - radius, y0);
        }

        while (x < y) {
            if (f >= 0) {
                y--;
                ddF_y += 2;
                f += ddF_y;
            }
            x++;
            ddF_x += 2;
            f += ddF_x + 1;

            if (fillColor != null) {
                Color oldColor = strokeColor;
                strokeColor = fillColor;
                renderLine(x0 + x, y0 - y, x0 - x, y0 - y);
                renderLine(x0 + y, y0 - x, x0 - y, y0 - x);
                renderLine(x0 + y, y0 + x, x0 - y, y0 + x);
                renderLine(x0 + x, y0 + y, x0 - x, y0 + y);
                strokeColor = oldColor;
            }

            if (strokeColor != null) {
                setPixel(x0 + x, y0 - y); // 1
                setPixel(x0 + y, y0 - x); // 2
                setPixel(x0 + y, y0 + x); // 3
                setPixel(x0 + x, y0 + y); // 4

                setPixel(x0 - x, y0 + y); // 5
                setPixel(x0 - y, y0 + x); // 6
                setPixel(x0 - y, y0 - x); // 7
                setPixel(x0 - x, y0 - y); // 8
            }
        }
    }
}
