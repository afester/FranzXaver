package afester.javafx.examples.board;

import java.util.function.BiConsumer;

import afester.javafx.examples.board.tools.ColorChooser;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.util.Pair;

public class ColorSettings extends Dialog<Void> {

	private final ListView<Pair<ColorClass, Color>> componentList = new ListView<>();
	private final ColorChooser colorChooser = new ColorChooser();
	private final Slider widthSlider = new Slider(0, 3, 1.0);

    private BiConsumer<ColorClass, Color> consumer = null;
    private BiConsumer<ColorClass, Double> widthConsumer = null;

    @SafeVarargs
    public ColorSettings(Pair<ColorClass, Color>... colors) {
	    initModality(Modality.NONE);

		componentList.getItems().addAll(colors);

		colorChooser.customColorProperty().addListener((obj, oldValue, newValue) -> {
		   if (consumer != null) {
		       consumer.accept(componentList.getSelectionModel().getSelectedItem().getKey(), newValue);
		   }
		});

		componentList.getSelectionModel().selectedItemProperty().addListener((obj, oldValue, newValue) -> {
		   colorChooser.setCurrentColor(newValue.getValue()); 
		});
		componentList.getSelectionModel().select(0);

		// Width
        widthSlider.setShowTickMarks(true);
        widthSlider.setShowTickLabels(true);
        widthSlider.setMajorTickUnit(0.25f);
        widthSlider.setBlockIncrement(0.1f);
        widthSlider.valueProperty().addListener((obj, oldValue, newValue) -> {
            if (widthConsumer != null) {
                widthConsumer.accept(componentList.getSelectionModel().getSelectedItem().getKey(), 
                                     newValue.doubleValue());
            }
        });

		final var rightColumn = new VBox();
		rightColumn.getChildren().addAll(colorChooser, widthSlider);
		final var mainLayout = new HBox();
		mainLayout.getChildren().addAll(componentList, rightColumn);

		final var pane = getDialogPane();
	    pane.getButtonTypes().add(new ButtonType("Close", ButtonData.CANCEL_CLOSE));
		pane.setContent(mainLayout);
	}


    public void setOnColorChanged(BiConsumer<ColorClass, Color> consumer) {
        this.consumer = consumer;
    }


    public void setOnWidthChanged(BiConsumer<ColorClass, Double> consumer) {
        this.widthConsumer = consumer;
    }

}
