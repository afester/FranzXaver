package afester.javafx.components;

import java.io.InputStream;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ToolbarButton extends Button {

    public ToolbarButton(String toolTip, String fileName) {

        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        if (is != null) {
            ImageView image = new ImageView(new Image(is));
            setGraphic(image);
        } else {
            setText(toolTip);
        }
        setTooltip(new Tooltip(toolTip));
    }


    public ToolbarButton(String toolTip, Image img) {
        ImageView image = new ImageView(img);
        setGraphic(image);
        setTooltip(new Tooltip(toolTip));
    }
}
