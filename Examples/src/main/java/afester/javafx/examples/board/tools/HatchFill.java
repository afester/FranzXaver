package afester.javafx.examples.board.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class HatchFill extends Application {

    @Override
    public void start(Stage primaryStage) {
        ImagePattern hatch = createHatch();

//        Canvas canvas = new Canvas(600, 600);
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        gc.setFill(hatch);
//        gc.fillRect(0, 0, 600, 600);

        final double width = 400;
        final double height = 600;

        Pane pane = new Pane();
        pane.setMinSize(width,  height);
        pane.setMaxSize(width,  height);
        pane.setPrefSize(width,  height);
        
        createDiagonalHatch(pane, width, height, 5, Color.LIGHTGRAY, 1.0);
        
//        Rectangle r1 = new Rectangle(0, 0, 100, 400);
//        r1.setFill(Color.LIGHTGREY);
//        Rectangle r2 = new Rectangle(100, 0, 100, 400);
//        r2.setFill(Color.DARKGRAY);
//        Rectangle r3 = new Rectangle(200, 0, 100, 400);
//        r3.setFill(Color.GREY);
//        pane.getChildren().addAll(r1, r2, r3);
        
        //Rectangle content = new Rectangle(0, 0, 600, 600);
        //content.setFill(hatch);
        // pane.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, new CornerRadii(0), new Insets(0))));

        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
        pane.setEffect(new DropShadow(2d, 10, 10, Color.GRAY));
        
        Rectangle area = new Rectangle(20, 20, 360, 560);
        area.setFill(Color.WHITE);
        pane.getChildren().add(area);

        primaryStage.setScene(new Scene(new StackPane(pane)));
        primaryStage.show();
    }

    // NOTE: Currently only works properly if the width/height is a multiple of dist!
    public static void createDiagonalHatch(Pane pane, final double width, final double height, int dist, Color color,
                                           double strokeWidth) {
        int y = 0;
        int x2 = 0;
        if (height > width) {
            for (int x = dist;  x < width+height;  x += dist) {
    
                Line line = null;
                if (x < width) {
                    line = new Line(0, x, x, 0);
                    line.setStroke(color);
                } else if (x < height){
                    line = new Line(0, x, width, y);
                    line.setStroke(color);
                    y += dist;
                } else {
                    line = new Line(x2, height, width, y);
                    line.setStroke(color);
                    y += dist;
                    x2 += dist;
                }
    
                line.setStrokeWidth(strokeWidth);
                pane.getChildren().add(line);
            }
        } else {
            for (int x = dist;  x < width+height;  x += dist) {
    
                Line line = null;
                if (x < height) {
                    line = new Line(0, x, x, 0);
                    line.setStroke(color);
                } else if (x < width){
                    line = new Line(y, height, x, 0);
                    line.setStroke(color);
                    y += dist;
                } else {
                    line = new Line(y, height, width, x2);
                    line.setStroke(color);
                    y += dist;
                    x2 += dist;
                }

                line.setStrokeWidth(strokeWidth);
                pane.getChildren().add(line);
            }
        }
    }

    /**
     * @return An 20x20 pixel image pattern with a specific hatch style
     */
    private ImagePattern createHatch() {
        // Create the pattern
        Pane pane = new Pane();
        pane.setPrefSize(20, 20);

        Line fw = new Line(25, -5, -5, 25); // -5, -5, 25, 25);
        fw.setStroke(Color.DARKGRAY);
        fw.setStrokeWidth(2);

//        Line bw = new Line(-5, 25, 25, -5);
//        bw.setStroke(Color.ALICEBLUE);
//        bw.setStrokeWidth(5);

        pane.getChildren().addAll(fw); // , bw);

        new Scene(pane);
        pane.snapToPixelProperty().set(true);
        Image hatch = pane.snapshot(null, null);

        File outputFile = new File("hatch.png");
        BufferedImage bImage = SwingFXUtils.fromFXImage(hatch, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImagePattern result = new ImagePattern(hatch, 5, 5, 20, 20, false);
        return result;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
