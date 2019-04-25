package afester.javafx.examples.board;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

public interface Interactable {

	Point2D getPos();

    void setSelected(boolean isSelected);

    void mouseDragged(MouseEvent e, BoardView bv, Point2D offset);

    void leftMouseAction(MouseEvent e, BoardView bv);

	void rightMouseAction(MouseEvent e);

    String getRepr();
}
