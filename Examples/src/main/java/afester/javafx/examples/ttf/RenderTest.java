package afester.javafx.examples.ttf;

import afester.javafx.examples.image.ImageConverter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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

        img = new WritableImage(Glyph.WIDTH, Glyph.HEIGHT);
        createImage();

        ImageView iv = new ImageView(img);
        HBox g = new HBox();
        g.getChildren().add(iv);
        
        
        for (short rgb565 : Glyph.palette) {
            int newColor = ImageConverter.fromRGB565ToRGB(rgb565);
            Color color = new Color(MedianCut.red(newColor) / 255.0, MedianCut.green(newColor) / 255.0,
                                    MedianCut.blue(newColor) / 255.0, 1.0);
            
            Rectangle r = new Rectangle(20, 20);
            r.setFill(color);
            r.setStroke(null);
            g.getChildren().add(r);
        }
        
        
        Scene scene = new Scene(g);

        stage.setScene(scene);
        stage.show();
    }

    private void createImage() {
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
}
