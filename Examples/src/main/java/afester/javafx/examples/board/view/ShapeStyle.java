package afester.javafx.examples.board.view;

import afester.javafx.shapes.LineDash;
import javafx.scene.paint.Color;

/**
 * A style which can be applied to shapes.
 * Essentially this class encapsulates all style related properties like
 * stroke color, stroke width, line pattern, opacity, ...  
 */
public class ShapeStyle {
    private final Color color;
    private final Double width;
    private final LineDash lineStyle;
    private final Double opacity;

    /**
     * Creates a new shape style with default values:
     * color=Color.BLACK, width=1.0, lineStyle=SOLID, opacity=1.0
     */
    public ShapeStyle() {
        this(Color.BLACK, 1.0, LineDash.SOLID, 1.0);
    }

    /**
     * Creates a new shape style with the given attributes.
     *
     * @param color  The stroke color
     * @param width  The stroke width
     * @param lineStyle The line style (solid, dashed, dotted, ...)
     * @param opacity   The opacity
     */
    public ShapeStyle(Color color, double width, LineDash lineStyle, double opacity) {
        this.color = color;
        this.width = width;
        this.lineStyle = lineStyle;
        this.opacity = opacity;
    }

    /**
     * @return The stroke color of this style.
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return The stroke width of this style.
     */
    public double getWidth() {
        return width;
    }

    /**
     * @return The line style of this style.
     */
    public LineDash getLineStyle() {
        return lineStyle;
    }

    /**
     * @return The opacity of this style.
     */
    public double getOpacity() {
        return opacity;
    }

    /**
     * @param newWidth The new line width for the ShapeStyle.
     * @return A new ShapeStyle which has the same style as the current one,
     *         but with a different line width.
     */
    public ShapeStyle modifiedWidth(double newWidth) {
        return new ShapeStyle(color, newWidth, lineStyle, opacity);
    }

    /**
     * @param newColor The new stroke color for the ShapeStyle.
     * @return A new ShapeStyle which has the same style as the current one,
     *         but with a different stroke color.
     */
    public ShapeStyle modifiedColor(Color newColor) {
        return new ShapeStyle(newColor, width, lineStyle, opacity);
    }

    /**
     * @param newLineStyle The new line style for the ShapeStyle.
     * @return A new ShapeStyle which has the same style as the current one,
     *         but with a different line style.
     */
    public ShapeStyle modifiedLineStyle(LineDash newLineStyle) {
        return new ShapeStyle(color, width, newLineStyle, opacity);
    }

    @Override
    public String toString() {
        return String.format("ShapeStyle[color:%s, width=%s, lineStyle:%s, opacity:%s]", 
                getColor(), getWidth(), getLineStyle(), getOpacity());
    }
}
