package afester.javafx.examples.board.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;

/**
 * A ColorGroup contains the visualization of all shapes which share the same
 * style.
 */
public class StyleGroup extends Group {

    private final ObjectProperty<ShapeStyle> shapeStyle = new SimpleObjectProperty<ShapeStyle>(new ShapeStyle());
    public ObjectProperty<ShapeStyle> shapeStyleProperty() { return shapeStyle; }
    public ShapeStyle getShapeStyle() { return shapeStyle.get(); }
    public void setShapeStyle(ShapeStyle newValue) { shapeStyle.set(newValue); }

    public StyleGroup() {
        shapeStyle.addListener((obj, oldValue, newValue) -> {
            getChildrenUnmodifiable().forEach(child -> {
                TraceView s = (TraceView) child;
                s.setShapeStroke(newValue.getColor());
                s.setStrokeWidth(newValue.getWidth()); // .doubleValue());
                s.getStrokeDashArray().addAll(newValue.getLineStyle().getDashArray());
            });

        });
    }
}
