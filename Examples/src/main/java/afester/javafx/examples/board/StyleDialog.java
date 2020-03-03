package afester.javafx.examples.board;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import afester.javafx.examples.board.view.ShapeStyle;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;

class Node {
    public String theLabel;
    public Map.Entry<StyleSelector, ObjectProperty<ShapeStyle>> theSelector;

    public Node(String label) {
        this(label, null);
    }

    public Node(String label, Map.Entry<StyleSelector, ObjectProperty<ShapeStyle>> entry) {
        theLabel = label;
        theSelector = entry;
    }
    
    public String toString() {
        return String.format("Node[label=%s, entry=%s", theLabel, theSelector);
    }
}


public class StyleDialog extends Dialog<Void> {

	//private final ListView<Map.Entry<StyleSelector, ObjectProperty<ShapeStyle>>> componentList = new ListView<>();
    private final TreeView<Node> componentList = new TreeView<>();

    public StyleDialog(ApplicationProperties props) {
	    initModality(Modality.NONE);

	    final var rightColumn = new StylePanel();

	    var allStyles = props.getStyles();

	    TreeItem<Node> topNode = new TreeItem<>(new Node("Top view"));
        TreeItem<Node> bottomNode = new TreeItem<>(new Node("Bottom view"));

        // get lists of top and bottom colors, and sort them
        List<Map.Entry<StyleSelector, ObjectProperty<ShapeStyle>>> topColors =
                allStyles.entrySet()
                         .stream()
                         .filter(entry -> entry.getKey().name().startsWith("TOP" ))
                         .collect(Collectors.toList());
        topColors.sort((a, b) -> a.getKey().getName().compareTo(b.getKey().getName()));
        topColors.forEach(entry -> {
            TreeItem<Node> node = new TreeItem<>(new Node(entry.getKey().getName().substring(4), entry));
            topNode.getChildren().add(node);
        });

        List<Map.Entry<StyleSelector, ObjectProperty<ShapeStyle>>> bottomColors =
                allStyles.entrySet()
                         .stream()
                         .filter(entry -> entry.getKey().name().startsWith("BOTTOM" ))
                         .collect(Collectors.toList());
        bottomColors.sort((a, b) -> a.getKey().getName().compareTo(b.getKey().getName()));
        bottomColors.forEach(entry -> {
            TreeItem<Node> node = new TreeItem<>(new Node(entry.getKey().getName().substring(7), entry));
            bottomNode.getChildren().add(node);
        });

        TreeItem<Node> rootNode = new TreeItem<>(new Node("Board Colors", null));
        rootNode.getChildren().addAll(topNode, bottomNode);
		componentList.setRoot(rootNode);

		componentList.setCellFactory(selectorEntry -> {
            return new TreeCell<>() {

                @Override
                protected void updateItem(Node item, boolean empty) {
                    // calling super here is very important - don't skip this!
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item.theLabel);
                    } else {
                        setText("");
                    }
                }
            };
		});

		// Bind to the application property which corresponds to the current
		// selection in the list
		componentList.getSelectionModel().selectedItemProperty().addListener((obj, oldValue, newValue) -> {
		    Node node = obj.getValue().getValue();
		    if (node.theSelector == null) {
		        rightColumn.setVisible(false);
		    } else {
		        rightColumn.setVisible(true);
		    }

            // remove old bindings
		    if (oldValue != null) {
		        var oldNode = oldValue.getValue();
		        if (oldNode.theSelector != null) {
		            ObjectProperty<ShapeStyle> oldProp = oldNode.theSelector.getValue();
		            oldProp.unbind();
		        }
		    }

		    // bind properties of the selected style to the controls
		    var newNode = newValue.getValue();
            System.err.println("NEW NODE:" + newNode);

            ObjectProperty<ShapeStyle> newProp = newNode.theSelector.getValue(); // prop to update
            rightColumn.shapeStyleProperty().set(newProp.get());      // initial value!?!
            newProp.bind(rightColumn.shapeStyleProperty());
		});

		// select the first entry - this calls the selection listener above!
		componentList.getSelectionModel().select(0);

		final var mainLayout = new HBox();
		mainLayout.getChildren().addAll(componentList, rightColumn);

		final var pane = getDialogPane();
	    pane.getButtonTypes().add(new ButtonType("Close", ButtonData.CANCEL_CLOSE));
		pane.setContent(mainLayout);
	}
}
