package afester.javafx.components;

import javafx.scene.input.MouseEvent;

public interface Interactor {

    void mousePressed(MouseEvent e);

    void mouseDragged(MouseEvent e);

    void mouseReleased(MouseEvent e);
}
