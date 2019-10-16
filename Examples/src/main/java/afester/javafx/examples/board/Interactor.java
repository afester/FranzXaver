package afester.javafx.examples.board;

import afester.javafx.examples.board.view.InteractableEvent;

public interface Interactor {

    void mousePressed(InteractableEvent e);

    void mouseDragged(InteractableEvent e);

    void mouseReleased(InteractableEvent e);
}
