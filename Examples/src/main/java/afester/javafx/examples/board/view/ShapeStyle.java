package afester.javafx.examples.board.view;

import afester.javafx.shapes.LineDash;
import javafx.scene.paint.Color;

public class ShapeStyle {
    private final Color color;
    private final Double width;
    private final LineDash lineStyle;
    private final Double opacity;

    public ShapeStyle() {
        this(Color.BLACK, 1.0, LineDash.SOLID, 1.0);
    }

    public ShapeStyle(Color color, double width, LineDash lineStyle, double opacity) {
        this.color = color;
        this.width = width;
        this.lineStyle = lineStyle;
        this.opacity = opacity;
    }

    public Color getColor() {
        return color;
    }

    public double getWidth() {
        return width;
    }

    public LineDash getLineStyle() {
        return lineStyle;
    }

    public double getOpacity() {
        return opacity;
    }

    public ShapeStyle modifiedWidth(double newWidth) {
        return new ShapeStyle(color, newWidth, lineStyle, opacity);
    }

    public ShapeStyle modifiedColor(Color newColor) {
        return new ShapeStyle(newColor, width, lineStyle, opacity);
    }

    public ShapeStyle modifiedLineStyle(LineDash newVal) {
        return new ShapeStyle(color, width, newVal, opacity);
    }

    @Override
    public String toString() {
        return String.format("ShapeStyle[color:%s, width=%s, lineStyle:%s, opacity:%s]", getColor(), getWidth(),
                getLineStyle(), getOpacity());
    }
}
