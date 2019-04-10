package afester.javafx.examples.board;

import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Node;
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
            clickedObject.mousePressed(e, bv);
            offset = new Point2D(clickedObject.getLayoutX() - e.getX(), clickedObject.getLayoutY() - e.getY());
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
