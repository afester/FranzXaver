package afester.javafx.examples.board;

import afester.javafx.components.GraphicalComboBox;
import afester.javafx.examples.board.tools.ColorChooser;
import afester.javafx.examples.board.view.ShapeStyle;
import afester.javafx.shapes.Line;
import afester.javafx.shapes.LineDash;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;


public class StylePanel extends VBox {

	private final ColorChooser colorChooser = new ColorChooser();
	private final Slider widthSlider = new Slider(0, 3, 1.0);
	private final ComboBox<LineDash> lineStyle; 

    // The current style as specified by this dialog
    private final ObjectProperty<ShapeStyle> shapeStyle = new SimpleObjectProperty<>(new ShapeStyle());
    public ObjectProperty<ShapeStyle> shapeStyleProperty() { return shapeStyle; }
    public ShapeStyle getShapeStyle() { return shapeStyle.get(); }
    public void setShapeStyle (ShapeStyle newStyle) { shapeStyle.set(newStyle); }

    public StylePanel() {
        
        // Color
        colorChooser.customColorProperty().addListener((obj, oldVal, newVal) -> {
            setShapeStyle(getShapeStyle().modifiedColor(newVal));
        });

        // Width
        widthSlider.setShowTickMarks(true);
        widthSlider.setShowTickLabels(true);
        widthSlider.setMajorTickUnit(0.25f);
        widthSlider.setBlockIncrement(0.1f);
        widthSlider.valueProperty().addListener((obj, oldVal, newVal) -> {
            setShapeStyle(getShapeStyle().modifiedWidth(newVal.doubleValue()));
        });

        // Line style
        lineStyle = new GraphicalComboBox<>(item -> {
            final Line lineDash =  new Line(0, 0, 50, 0);
            lineDash.getStrokeDashArray().setAll(item.getDashArray());
            return lineDash;
        });
        lineStyle.getItems().addAll(LineDash.values());

		lineStyle.getSelectionModel().selectedItemProperty().addListener((obj, oldVal, newVal) -> {
		    setShapeStyle(getShapeStyle().modifiedLineStyle(newVal));
		});

		// update ui components when the style changes
        shapeStyle.addListener((obj, oldVal, newVal) -> {
            colorChooser.customColorProperty().set(newVal.getColor());
            widthSlider.setValue(newVal.getWidth());
            lineStyle.getSelectionModel().select(newVal.getLineStyle());
        });

        getChildren().addAll(colorChooser, widthSlider, lineStyle);
	}
}
