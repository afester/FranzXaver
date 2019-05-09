package afester.javafx.examples.board;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class TraceInteractor extends MouseInteractor {

    public TraceInteractor(BoardView boardView) {
        super(boardView);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO: Move this fine grained event dispatching into a more generic MouseInteractor
        if (e.getButton() == MouseButton.PRIMARY) {
            if (e.isControlDown()) {
            } else if (e.isAltDown()) {
            } else if (e.isShiftDown()) {
            } else if (e.isMetaDown()) {
            } else { // no modifiers pressed
                leftMouseClicked(e);
            }
        } else if (e.getButton() == MouseButton.SECONDARY) {
        }
    }

    private void leftMouseClicked(MouseEvent e) {
        Interactable clickedObject = getClickedObject(e);
        if (clickedObject != null && clickedObject instanceof AirWire) {
            AirWire aw = (AirWire) clickedObject;       // TODO: How to remove this cast and the instanceof
            aw.convertToStraightTrace();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

}
