package afester.javafx.components;

import java.io.InputStream;

import javafx.scene.control.RadioButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * https://stackoverflow.com/questions/33786146/how-to-make-a-radiobutton-look-like-regular-button-in-javafx
 * https://stackoverflow.com/questions/46835087/prevent-a-toggle-group-from-not-having-a-toggle-selected-java-fx
 */
public class ToolbarToggleButton extends RadioButton {

    
    public ToolbarToggleButton(String toolTip, String fileName) {

        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        if (is != null) {
            ImageView image = new ImageView(new Image(is));
            setGraphic(image);
        } else {
            setText(toolTip);
        }
        setTooltip(new Tooltip(toolTip));
        
        getStyleClass().remove("radio-button");
        getStyleClass().add("toggle-button");
    }

}
