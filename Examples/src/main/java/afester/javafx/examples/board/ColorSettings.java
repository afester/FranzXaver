package afester.javafx.examples.board;

import afester.javafx.examples.board.tools.ColorChooser;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

public class ColorSettings extends Dialog<Void> {

	private final ListView<String> componentList = new ListView<>();
	private final ColorChooser colorChooser = new ColorChooser();

	public ColorSettings() {
		componentList.getItems().addAll("Trace", "Pad");
		
		final var mainLayout = new HBox();
		mainLayout.getChildren().addAll(componentList, colorChooser);

		final var pane = new DialogPane();
		pane.setContent(mainLayout);
        setDialogPane(pane);
	}

}
