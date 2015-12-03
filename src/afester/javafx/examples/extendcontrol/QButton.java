package afester.javafx.examples.extendcontrol;

import javafx.scene.control.Button;
import javafx.scene.control.Skin;

public class QButton extends Button {
    public QButton(String str) {
        super(str);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new QButtonSkin(this);
    }
}
