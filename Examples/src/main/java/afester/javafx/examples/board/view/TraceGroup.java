package afester.javafx.examples.board.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class TraceGroup extends Group {

    // The color of the traces
    private final ObjectProperty<Color> color = new SimpleObjectProperty<Color>(Color.BLACK);
    public ObjectProperty<Color> colorProperty() { return color; }
    public Color getColor() { return color.get(); }
    public void setColor(Color newColor) { color.set(newColor); }

    // The width of the traces (in mm)
    private final DoubleProperty width = new SimpleDoubleProperty(1.0);
    public DoubleProperty widthProperty() { return width; }
    public Double getWidth() { return width.get(); }
    public void setWidth(Double newWidth) { width.set(newWidth); }

    public TraceGroup() {
        
        color.addListener((obj, oldValue, newValue) -> {
           getChildrenUnmodifiable().forEach(c -> {
               Shape shape = (Shape) c;
               shape.setStroke(newValue);
           });
        });
        
        width.addListener((obj, oldValue, newValue) -> {
            getChildrenUnmodifiable().forEach(c -> {
                Shape shape = (Shape) c;
                shape.setStrokeWidth(newValue.doubleValue());
            });
         });
    }
}
