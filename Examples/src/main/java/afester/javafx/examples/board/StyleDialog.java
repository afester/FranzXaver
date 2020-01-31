package afester.javafx.examples.board;

import java.util.Map;

import afester.javafx.examples.board.view.ShapeStyle;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;


public class StyleDialog extends Dialog<Void> {

	private final ListView<Map.Entry<StyleSelector, ObjectProperty<ShapeStyle>>> componentList = new ListView<>();

    public StyleDialog(ApplicationProperties props) {
	    initModality(Modality.NONE);

	    final var rightColumn = new StylePanel();

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
            rightColumn.shapeStyleProperty().set(newProp.get());      // initial value!?!
		    newProp.bind(rightColumn.shapeStyleProperty());
		});
		componentList.getSelectionModel().select(0);

		final var mainLayout = new HBox();
		mainLayout.getChildren().addAll(componentList, rightColumn);

		final var pane = getDialogPane();
	    pane.getButtonTypes().add(new ButtonType("Close", ButtonData.CANCEL_CLOSE));
		pane.setContent(mainLayout);
	}
}
