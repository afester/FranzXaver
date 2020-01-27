package afester.javafx.examples.board.view;

import afester.javafx.shapes.LineDash;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class ShapeStyle {

    // Color
    private final ObjectProperty<Color> color = new SimpleObjectProperty<Color>(Color.RED);
    public ObjectProperty<Color> colorProperty() { return color; }
    public Color getColor() { return color.get(); }
    public void setColor(Color newValue) { color.set(newValue); }

    // Width
    private final DoubleProperty width = new SimpleDoubleProperty(1.0);
    public DoubleProperty widthProperty() { return width; }
    public Double getWidth() { return width.get(); }
    public void setWidth(Double newValue) { width.set(newValue); }

    // Line style
    private final ObjectProperty<LineDash> lineStyle = new SimpleObjectProperty<LineDash>(LineDash.SOLID);
    public ObjectProperty<LineDash> lineStyleProperty() { return lineStyle; }
    public LineDash getLineStyle() { return lineStyle.get(); }
    public void setLineStyle(LineDash newValue) { lineStyle.set(newValue); }

    // Opacity
    private final DoubleProperty opacity = new SimpleDoubleProperty(1.0);
    public DoubleProperty opacityProperty() { return opacity; }
    public Double getOpacity() { return opacity.get(); }
    public void setOpacity(Double newValue) { opacity.set(newValue); }
    
    @Override
    public String toString() {
        return String.format("ShapeStyle[color:%s, width=%s, lineStyle:%s, opacity:%s]", getColor(), getWidth(), getLineStyle(), getOpacity());
    }
}
