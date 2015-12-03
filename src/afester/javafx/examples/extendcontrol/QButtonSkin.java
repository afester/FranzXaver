package afester.javafx.examples.extendcontrol;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

// DO NOT DO THIS - com.sun is not public!
// (However, it seems that there is currently no alternative besides duplicating the code)
// In Java 9, the skin classes will be public:
// http://download.java.net/jdk9/jfxdocs/javafx/scene/control/skin/ButtonSkin.html
class QButtonSkin extends com.sun.javafx.scene.control.skin.ButtonSkin {

    private Rectangle rect = new Rectangle(0., 0., 200., 200.);

    public QButtonSkin(Button button) {
        super(button);
    }

    @Override
    protected void updateChildren() {
        super.updateChildren();

        if (rect == null) {
            rect = new Rectangle(2, 2, 8, 8);
            rect.setFill(Color.RED);
        }
        getChildren().add(rect);
    }
}