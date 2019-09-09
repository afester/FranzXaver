package afester.javafx.examples.board;

import javafx.scene.shape.Circle;

/**
 * A view for a single hole on the breadboard as seen from the bottom.
 */
public class HoleViewBottom extends Circle {

    public HoleViewBottom(double centerX, double centerY) {
        // hole and copper (backside of the board)
        super(centerX, centerY, 0.7);
    }

    @Override
    public String toString() {
        return String.format("HoleViewBottom[centerX=%s, centerY=%s, radius=%s, fill=%s, stroke=%s, strokeWidth=%s]",
                              getCenterX(), getCenterY(), getRadius(), getFill(), getStroke(),getStrokeWidth());
    }
}
