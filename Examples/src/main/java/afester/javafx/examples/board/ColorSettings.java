package afester.javafx.examples.board;

import java.util.function.BiConsumer;

import afester.javafx.examples.board.tools.ColorChooser;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.util.Pair;

public class ColorSettings extends Dialog<Void> {

	private final ListView<Pair<ColorClass, Color>> componentList = new ListView<>();
	private final ColorChooser colorChooser = new ColorChooser();
    private BiConsumer<ColorClass, Color> consumer = null;

    @SafeVarargs
    public ColorSettings(Pair<ColorClass, Color>... colors) {
	    initModality(Modality.NONE);

		componentList.getItems().addAll(colors); // ColorClass.TRACE, ColorClass.PAD);

		colorChooser.customColorProperty().addListener((obj, oldValue, newValue) -> {
		   if (consumer != null) {
		       consumer.accept(componentList.getSelectionModel().getSelectedItem().getKey(), newValue);
		   }
		});

		componentList.getSelectionModel().selectedItemProperty().addListener((obj, oldValue, newValue) -> {
		   colorChooser.setCurrentColor(newValue.getValue()); 
		});
		componentList.getSelectionModel().select(0);

		final var mainLayout = new HBox();
		mainLayout.getChildren().addAll(componentList, colorChooser);

		final var pane = getDialogPane();
	    pane.getButtonTypes().add(new ButtonType("Close", ButtonData.CANCEL_CLOSE));
		pane.setContent(mainLayout);
	}


    public void setOnColorChanged(BiConsumer<ColorClass, Color> consumer) {
        this.consumer = consumer;
    }

}
