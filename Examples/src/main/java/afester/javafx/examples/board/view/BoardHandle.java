package afester.javafx.examples.board.view;

import afester.javafx.examples.board.model.Board;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

public class BoardHandle extends Circle implements Interactable {

    private final int cornerIdx;
    private final Board board;

    public BoardHandle(Double xpos, Double ypos, Board b, int cornerIdx) {
        super(xpos, ypos, 1.0);
        setPickOnBounds(true);

        this.board = b;
        this.cornerIdx = cornerIdx;
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

    @Override
    public void setSelected(BoardView bv, boolean isSelected) {
    }

    @Override
    public String getRepr() {
        return null;
    }

    @Override
    public void startDrag() {
    }

    @Override
    public void drag(BoardView bv, Point2D clickPos) {
        System.err.println("MOVE " + this + " to " + clickPos);
        board.setCornerPos(cornerIdx, clickPos);
    }

    public String toString() {
        return String.format("BoardHandle[center=%s, radius=%s, idx=%s]",
                              new Point2D(getCenterX(), getCenterY()), getRadius(), cornerIdx);
    }

}
