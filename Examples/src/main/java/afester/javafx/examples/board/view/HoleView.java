package afester.javafx.examples.board.view;

import javafx.scene.shape.Circle;

/**
 * A view for a single hole on the breadboard.
 */
public class HoleView extends Circle {

    public HoleView(double centerX, double centerY, boolean isBottom) {
        super(centerX, centerY, 0.4);
        
        if (isBottom) {
            setRadius(0.7);
        }
    }

    @Override
    public String toString() {
        return String.format("HoleView[centerX=%s, centerY=%s, radius=%s, fill=%s, stroke=%s, strokeWidth=%s]",
                              getCenterX(), getCenterY(), getRadius(), getFill(), getStroke(),getStrokeWidth());
    }
}
