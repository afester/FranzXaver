package afester.javafx.components;

import java.io.InputStream;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ToolbarToggleButton extends ToggleButton {

    public ToolbarToggleButton(String toolTip, String fileName) {

        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        if (is != null) {
            ImageView image = new ImageView(new Image(is));
            setGraphic(image);
        } else {
            setText(toolTip);
        }
        setTooltip(new Tooltip(toolTip));
    }

}
