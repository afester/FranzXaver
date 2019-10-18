package afester.javafx.examples.board;

import javafx.geometry.Point2D;

public interface Interactable {

	Point2D getPos();

    void setSelected(boolean isSelected);

    String getRepr();

    void moveToGrid(Point2D clickPos);
}
