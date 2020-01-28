package afester.javafx.examples.board;

import java.util.Map;

import afester.javafx.components.GraphicalComboBox;
import afester.javafx.examples.board.tools.ColorChooser;
import afester.javafx.examples.board.view.ShapeStyle;
import afester.javafx.shapes.Line;
import afester.javafx.shapes.LineDash;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;


public class ColorSettings extends Dialog<Void> {

	private final ListView<Map.Entry<StyleSelector, ObjectProperty<ShapeStyle>>> componentList = new ListView<>();
	private final ColorChooser colorChooser = new ColorChooser();
	private final Slider widthSlider = new Slider(0, 3, 1.0);
	private final ComboBox<LineDash> lineStyle; 

    // The current style as specified by this dialog
    private final ObjectProperty<ShapeStyle> shapeStyle = new SimpleObjectProperty<>(new ShapeStyle());
    public ObjectProperty<ShapeStyle> shapeStyleProperty() { return shapeStyle; }
    public ShapeStyle getShapeStyle() { return shapeStyle.get(); }
    public void setShapeStyle (ShapeStyle newStyle) { shapeStyle.set(newStyle); }

    public ColorSettings(ApplicationProperties props) {
	    initModality(Modality.NONE);

	    var allStyles = props.getStyles();

		componentList.getItems().addAll(allStyles.entrySet());
		componentList.setCellFactory(selectorEntry -> {
            return new ListCell<>() {

                @Override
                protected void updateItem(Map.Entry<StyleSelector, ObjectProperty<ShapeStyle>> item, boolean empty) {
                    // calling super here is very important - don't skip this!
                    super.updateItem(item, empty);

                    if (item != null) {
                        setText(item.getKey().getName());
                    } else {
                        setText("");
                    }
                }
            };
		});


        // Line style
        lineStyle = new GraphicalComboBox<>(item -> {
            final Line lineDash =  new Line(0, 0, 50, 0);
            lineDash.getStrokeDashArray().setAll(item.getDashArray());
            return lineDash;
        });
        lineStyle.getItems().addAll(LineDash.values());

		// Bind to the application property which corresponds to the current
		// selection in the list
		componentList.getSelectionModel().selectedItemProperty().addListener((obj, oldValue, newValue) -> {

		    // remove old bindings
		    if (oldValue != null) {
    		    ObjectProperty<ShapeStyle> oldProp = oldValue.getValue();
    		    oldProp.unbind();
		    }

            // bind properties of the selected style to the controls
		    ObjectProperty<ShapeStyle> newProp = newValue.getValue(); // prop to update
            shapeStyle.set(newProp.get());// initial value!?!
		    newProp.bind(shapeStyle);
		});
		componentList.getSelectionModel().select(0);

		shapeStyle.addListener((obj, oldVal, newVal) -> {
		   colorChooser.customColorProperty().set(newVal.getColor());
		   widthSlider.setValue(newVal.getWidth());
		   lineStyle.getSelectionModel().select(newVal.getLineStyle());
		});

		colorChooser.customColorProperty().addListener((obj, oldVal, newVal) -> {
		    setShapeStyle(getShapeStyle().modifiedColor(newVal));
		});

		lineStyle.getSelectionModel().selectedItemProperty().addListener((obj, oldVal, newVal) -> {
		    setShapeStyle(getShapeStyle().modifiedLineStyle(newVal));
		});

		// Width
        widthSlider.setShowTickMarks(true);
        widthSlider.setShowTickLabels(true);
        widthSlider.setMajorTickUnit(0.25f);
        widthSlider.setBlockIncrement(0.1f);
        widthSlider.valueProperty().addListener((obj, oldVal, newVal) -> {
            setShapeStyle(getShapeStyle().modifiedWidth(newVal.doubleValue()));
        });

		final var rightColumn = new VBox();
		rightColumn.getChildren().addAll(colorChooser, widthSlider, lineStyle);
		final var mainLayout = new HBox();
		mainLayout.getChildren().addAll(componentList, rightColumn);

		final var pane = getDialogPane();
	    pane.getButtonTypes().add(new ButtonType("Close", ButtonData.CANCEL_CLOSE));
		pane.setContent(mainLayout);
	}
}
