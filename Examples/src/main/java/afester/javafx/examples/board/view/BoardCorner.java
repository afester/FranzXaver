package afester.javafx.examples.board.view;

import afester.javafx.examples.board.Interactable;
import afester.javafx.examples.board.model.Board;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class BoardCorner extends Circle implements Interactable {

    private final int cornerIdx;
    private final Board board;

    public BoardCorner(Double xpos, Double ypos, Board b, int cornerIdx) {
        super(xpos, ypos, 0.5);
        this.board = b;
        this.cornerIdx = cornerIdx;

        setFill(Color.GREENYELLOW);     // Hack to make the whole circle selectable
        setStroke(Color.RED);
        setStrokeWidth(0.3);
    }

    public Board getBoard() {
        return board;
    }
    public int getCornerIdx() {
        return cornerIdx;
    }

    @Override
    public Point2D getPos() {
        return new Point2D(getCenterX(), getCenterY());
    }

    public void setPos(Point2D newPos) {
        board.setCornerPos(cornerIdx, newPos);
    }

    @Override
    public void setSelected(boolean isSelected) {
    }

    @Override
    public String getRepr() {
        return null;
    }

    public String toString() {
        return String.format("BoardHandle[centerX=%s, centerY=%s, radius=%s, fill=%s, stroke=%s, strokeWidth=%s]",
                              getCenterX(), getCenterY(), getRadius(), getFill(), getStroke(),getStrokeWidth());
    }
}
