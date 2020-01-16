package afester.javafx.examples.board;

import afester.javafx.examples.board.tools.ColorChooser;
import afester.javafx.examples.board.view.ShapeStyle;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Pair;

public class ColorSettings extends Dialog<Void> {

	private final ListView<Pair<StyleSelector, ShapeStyle>> componentList = new ListView<>();
	private final ColorChooser colorChooser = new ColorChooser();
	private final Slider widthSlider = new Slider(0, 3, 1.0);

    @SafeVarargs
    public ColorSettings(Pair<StyleSelector, ShapeStyle>... colors) {
	    initModality(Modality.NONE);

		componentList.getItems().addAll(colors);

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
