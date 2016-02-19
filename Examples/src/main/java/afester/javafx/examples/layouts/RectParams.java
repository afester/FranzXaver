package afester.javafx.examples.layouts;

import javafx.scene.paint.Color;

public class RectParams {
    private double width;
    private double height;
    private Color color;

    RectParams(double width, double height, Color col) {
        this.width = width;
        this.height = height;
        this.color = col;
    }

    double getWidth() {
        return width;
    }
    
    double getHeight() {
        return height;
    }

    Color getColor() {
        return color;
    }

}
