package afester.javafx.examples.docbook;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class TitledToolbar extends VBox {

    HBox toolButtons = new HBox();

    public TitledToolbar(String title) {
        getStyleClass().add("toolArea");

        Text titleNode = new Text(title);
        HBox xy = new HBox();
        xy.getStyleClass().add("titleBg");
        xy.getChildren().add(titleNode);
        xy.setAlignment(Pos.TOP_CENTER);
        titleNode.getStyleClass().add("toolAreaTitle");

        toolButtons.setSpacing(5);
        toolButtons.setPadding(new Insets(5, 5, 5, 5));

        getChildren().addAll(xy, toolButtons);
    }

    
    public void addButtons(Node... buttons) {
        toolButtons.getChildren().addAll(buttons);
    }

}
