package afester.javafx.svg;

import javafx.scene.Group;
import javafx.scene.text.Text;

public class SvgTextBox extends Group {

    public int getTextSpanCount() {
        return getChildren().size();
    }

    public Text getTextSpan(int idx) {
        return (Text) getChildrenUnmodifiable().get(idx);
    }
}