package afester.javafx.examples.text.skin;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.util.Duration;

public class Caret extends Path {

    // flag to indicate whether the caret is currently showing or not
    private final BooleanProperty showing = new SimpleBooleanProperty(false);
    public final boolean isShowing() { return showing.getValue(); }
    public final BooleanProperty showingProperty() { return showing; }

    public Caret() {
//        setHeight(20);
//        setWidth(2);
        setManaged(false);
//        setX(55);
//        setY(2);

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

        showing.addListener((obs, oldValue, newValue) -> {
            if (newValue == true) {
                timeline.playFromStart();
                setVisible(true);
            } else {
                timeline.stop();
                setVisible(false);
            }
        });
        
        setVisible(false);
    }

    public void setShape(PathElement[] caretShape) {
        getElements().setAll(caretShape);
    }
}
