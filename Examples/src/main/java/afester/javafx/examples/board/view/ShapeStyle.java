package afester.javafx.examples.board.view;

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

    // Opacity
    private final DoubleProperty opacity = new SimpleDoubleProperty(1.0);
    public DoubleProperty opacityProperty() { return opacity; }
    public Double getOpacity() { return opacity.get(); }
    public void setOpacity(Double newValue) { opacity.set(newValue); }
}
