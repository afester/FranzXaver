package afester.javafx.examples.board;

import afester.javafx.examples.board.view.BoardView;
import javafx.geometry.Point2D;

public interface Interactable {

	Point2D getPos();

    void setSelected(BoardView bv, boolean isSelected);

    String getRepr();

    void startDrag();

    void drag(BoardView boardView, Point2D newPos);

    // void move(BoardView bv, Point2D pos);

}
