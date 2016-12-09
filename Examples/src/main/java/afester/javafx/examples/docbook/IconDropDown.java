package afester.javafx.examples.docbook;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class IconDropDown extends VBox {
    private final ComboBox<String> cmb = new ComboBox<>();

    
    public IconDropDown(String iconName) {
        new Label("X");
        
//        Image image = new Image(getClass().getResourceAsStream("icons/" + iconName + ".png"));
//        ImageView imageView = new ImageView(image);
        Label label = new Label("       "); // , imageView);
        label.getStyleClass().add(iconName);

        setSpacing(5);
        getChildren().addAll(label, cmb);
    }

    public void addItem(String title) {
        cmb.getItems().add(title);
    }
    
}
