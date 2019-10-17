package afester.javafx.examples.board;

import java.util.List;

import javafx.scene.input.MouseEvent;

public interface Interactor {

    void mousePressed(List<Interactable> pickedObjects, MouseEvent e);

    void mouseDragged(MouseEvent e);

    void mouseReleased(MouseEvent e);
}
