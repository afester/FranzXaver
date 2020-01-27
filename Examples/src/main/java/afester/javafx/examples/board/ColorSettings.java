package afester.javafx.examples.board;

import java.util.Map;

import afester.javafx.examples.board.tools.ColorChooser;
import afester.javafx.examples.board.view.ShapeStyle;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;


public class ColorSettings extends Dialog<Void> {

	private final ListView<Map.Entry<StyleSelector, ShapeStyle>> componentList = new ListView<>();
	private final ColorChooser colorChooser = new ColorChooser();
	private final Slider widthSlider = new Slider(0, 3, 1.0);


    public ColorSettings(ApplicationProperties props) {
	    initModality(Modality.NONE);

	    var allStyles = props.getStyles();

		componentList.getItems().addAll(allStyles.entrySet());
		componentList.setCellFactory(selectorEntry -> {
            return new ListCell<>() {

                @Override
                protected void updateItem(Map.Entry<StyleSelector, ShapeStyle> item, boolean empty) {
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

		// Bind to the application property which corresponds to the current
		// selection in the list
		componentList.getSelectionModel().selectedItemProperty().addListener((obj, oldValue, newValue) -> {

		    // remove old bindings
		    if (oldValue != null) {
    		    ShapeStyle oldProp = oldValue.getValue();
                oldProp.colorProperty().unbind();
                oldProp.widthProperty().unbind();
		    }

            // bind properties of the selected style to the controls
		    ShapeStyle newProp = newValue.getValue();
		    colorChooser.setCustomColor(newProp.getColor()); // initial value
		    widthSlider.setValue(newProp.getWidth());        // initial value
	        newProp.colorProperty().bind(colorChooser.customColorProperty());
	        newProp.widthProperty().bind(widthSlider.valueProperty());
		});
		componentList.getSelectionModel().select(0);

		// Width
        widthSlider.setShowTickMarks(true);
        widthSlider.setShowTickLabels(true);
        widthSlider.setMajorTickUnit(0.25f);
        widthSlider.setBlockIncrement(0.1f);

		final var rightColumn = new VBox();
		rightColumn.getChildren().addAll(colorChooser, widthSlider);
		final var mainLayout = new HBox();
		mainLayout.getChildren().addAll(componentList, rightColumn);

		final var pane = getDialogPane();
	    pane.getButtonTypes().add(new ButtonType("Close", ButtonData.CANCEL_CLOSE));
		pane.setContent(mainLayout);
	}
}