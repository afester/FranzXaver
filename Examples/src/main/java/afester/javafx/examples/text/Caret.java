package afester.javafx.examples.text;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Caret extends Rectangle {

    public Caret() {
        setHeight(20);
        setWidth(2);
        setManaged(false);
        setX(55);
        setY(2);

        Duration sec = new Duration(500);
        EventHandler<ActionEvent> caretBlinker = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setVisible(!isVisible());
            }
        };
        KeyFrame keyFrame = new KeyFrame(sec, caretBlinker);

        // Build the time line animation.
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.playFromStart();
    }
}
