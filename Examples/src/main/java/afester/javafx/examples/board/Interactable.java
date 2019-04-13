package afester.javafx.examples.board;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

public interface Interactable {


    void setSelected(boolean isSelected);

    void mouseDragged(MouseEvent e, BoardView bv, Point2D offset);

    double getLayoutX();

    double getLayoutY();

    void leftMouseAction(MouseEvent e, BoardView bv);

	void rightMouseAction(MouseEvent e);
}
