package afester.javafx.examples.board;

import javafx.scene.shape.Circle;

/**
 * A view for a single hole on the breadboard.
 */
public class HoleViewTop extends Circle {

    public HoleViewTop(double centerX, double centerY) {
        // hole (frontside of board)
        super(centerX, centerY, 0.4);
    }

    @Override
    public String toString() {
        return String.format("HoleView[centerX=%s, centerY=%s, radius=%s, fill=%s, stroke=%s, strokeWidth=%s]",
                              getCenterX(), getCenterY(), getRadius(), getFill(), getStroke(),getStrokeWidth());
    }
}
