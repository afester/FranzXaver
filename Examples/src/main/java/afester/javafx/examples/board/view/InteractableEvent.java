package afester.javafx.examples.board.view;

import afester.javafx.examples.board.Interactable;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class InteractableEvent {

    private final Interactable target;
    private final MouseEvent event;

    public InteractableEvent(Interactable target, MouseEvent e) {
        this.target = target;
        this.event = e;
    }

    public Object getSource() {
        return event.getSource();
    }

    public Interactable getTarget() {
        return target;
    }

    public Point2D getPos() {
        return new Point2D(event.getX(), event.getY());
    }

    public MouseButton getButton() {
        return event.getButton();
    }

    public boolean isControlDown() {
        return event.isControlDown();
    }

    public boolean isAltDown() {
        return event.isAltDown();
    }

    public boolean isShiftDown() {
        return event.isShiftDown();
    }

    public boolean isMetaDown() {
        return event.isMetaDown();
    }

    public boolean isPrimaryButtonDown() {
        return event.isPrimaryButtonDown();
    }

}
