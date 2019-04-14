package afester.javafx.examples.board;

import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MouseInteractor implements Interactor {

    private Point2D offset; 
    private BoardView bv;

    public MouseInteractor(BoardView boardView) {
    	bv = boardView;
	}

    // TODO: This is a mess.
    private Interactable getClickedObject(MouseEvent e) {
        Interactable result = null;
        EventTarget target = e.getTarget();
        if (target instanceof Interactable) {
            result = (Interactable) target;
        } else if (target instanceof SelectionShape) {
            SelectionShape s = (SelectionShape) target;
            Node parent = s.getParent();
            if (parent instanceof Interactable) {
                result = (Interactable) parent;
            }
        }

        return result;
    }

	@Override
    public void mousePressed(MouseEvent e) {
        Interactable clickedObject = getClickedObject(e);
        if (clickedObject != null) {

            if (e.getButton() == MouseButton.PRIMARY) {
                if (e.isControlDown()) {
                } else if (e.isAltDown()) {
                } else if (e.isShiftDown()) {
                } else if (e.isMetaDown()) {
                } else { // no modifiers pressed
                    clickedObject.leftMouseAction(e, bv);
                }
            } else if (e.getButton() == MouseButton.SECONDARY) {
                clickedObject.rightMouseAction(e);
            }

            offset = clickedObject.getPos().subtract(e.getX(), e.getY());
          } else {
            bv.clearSelection();
        }
	}



//	private void updateSelectedObjects(MouseEvent e) {


	
	@Override
    public void mouseDragged(MouseEvent e) {
        Interactable currentObject = bv.getSelectedObject();
        if (!e.isControlDown() && e.isPrimaryButtonDown() && currentObject != null) {
            currentObject.mouseDragged(e, bv, offset);
        }
    }

}
