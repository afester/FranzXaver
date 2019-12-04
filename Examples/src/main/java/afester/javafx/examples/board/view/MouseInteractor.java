package afester.javafx.examples.board.view;

import java.util.Collections;
import java.util.List;

import afester.javafx.components.Interactor;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;


public abstract class MouseInteractor implements Interactor {

    private BoardView bv;       // The BoardView to which this interactor is attached

    private Point2D pickPos;                // the position where the mouse click occurred (in BoardView coordinates)
    private List<Interactable> pickedNodes = Collections.emptyList(); // the list of objects at the mouse click position 
    private int pickIndex = 0;              // The current index in the pickedNodes list

    // The handle which is currently dragged
    private Interactable handleToDrag = null;
    private Point2D handlePos = null;

    public MouseInteractor(BoardView boardView) {
    	bv = boardView;
	}

    public BoardView getBoardView() {
        return bv;
    }

	@Override
    public final void mousePressed(MouseEvent e) {
	    pickPos = bv.sceneToLocal(e.getSceneX(), e.getSceneY());
        final Point2D mpos = new Point2D(e.getSceneX(), e.getSceneY());

        // check for Handles
	    List<Interactable> handles = bv.getHandleGroup().pickAll(mpos);
	    if (!handles.isEmpty()) {
	        handleToDrag = handles.get(0);
	        handlePos = handleToDrag.getPos();
	    } else {
            // find all Interactable nodes at the specified position. This is necessary to 
            // allow selecting nodes further down in the Z order.
            final List<Interactable> newPickedNodes = pickObjects(mpos);

            // If the list of objects has changed, then reset the Z order iterator
            if (!newPickedNodes.equals(pickedNodes)) {
                if (!e.isControlDown()) {
                    bv.clearSelection();
                }

                pickedNodes = newPickedNodes;

                // Calculate the initial pick index - this is the last object in the list of picked objects
                // which is currently NOT selected
                var selNodes = bv.getSelectedObjects();
                pickIndex = 0;
                for (int loopIdx = 0;  loopIdx < pickedNodes.size();  loopIdx++) {
                    if (selNodes.contains(pickedNodes.get(loopIdx))) {
                        pickIndex = loopIdx + 1;
                    }
                }
                if (pickIndex >= pickedNodes.size()) {
                    pickIndex = 0;
                }
            }

            if (newPickedNodes.isEmpty()) {
                clickPos(pickPos);
            }
	    }
	}

    @Override
    public final void mouseReleased(MouseEvent e) {
        if (handleToDrag != null) {
            handleToDrag = null;
        } else {
            if (!pickedNodes.isEmpty()) {

                final Interactable selectedNode = pickedNodes.get(pickIndex);
                pickIndex++;
                if (pickIndex >= pickedNodes.size()) {
                    pickIndex = 0;
                }
    
                if (e.getButton() == MouseButton.PRIMARY) {

                  if (e.isControlDown() && !e.isShiftDown()) {
                      selectObject(selectedNode);
                  } else if (e.isAltDown()) {
                  } else if (e.isShiftDown()) {
                  } else if (e.isMetaDown()) {
                  } else { // no modifiers pressed

                      // TODO: This conflicts with the multi selection mode!!!!
                      // the multi selection is resetted when the mouse button is released! 
                      bv.clearSelection();
                      selectObject(selectedNode);
                  }
               } else if (e.getButton() == MouseButton.SECONDARY) {
                  rightClickObject(selectedNode);
               }
            } else if (!e.isControlDown()) {
                bv.clearSelection();
            }
        }
    }


	@Override
    public final void mouseDragged(MouseEvent e) {
	    final Point2D newPos = bv.sceneToLocal(e.getSceneX(), e.getSceneY());
	    final Point2D delta = newPos.subtract(pickPos);
	    
	    if (handleToDrag != null) {
            final Point2D newObjPos = handlePos.add(delta);
            drag(handleToDrag, newObjPos); 
	    } else if (!pickedNodes.isEmpty()) {

            if (bv.getSelectedObjects().isEmpty()) {
                final Interactable selectedNode = pickedNodes.get(pickIndex);

                if (e.getButton() == MouseButton.PRIMARY) {
                  if (e.isControlDown()) {
                  } else if (e.isAltDown()) {
                  } else if (e.isShiftDown()) {
                  } else if (e.isMetaDown()) {
                  } else { // no modifiers pressed
                      selectObject(selectedNode);
                  }
               }
            }

            if (!e.isControlDown() && e.isPrimaryButtonDown()) {
                // Note: delta is the delta to the original mouse position!
                // However, depending on the grid, the real object position might or might not change - hence,
                // the current position of the object is incompatible with the delta value!
                // we always need to be able to calculate the *possible* new node position...
                // This requires the original position of the node as well as the delta of the mouse movement.
               bv.getSelectedObjects().forEach(node  -> {
                   drag(node, delta);
               });
           }
        }
    }


	private void selectObject(Interactable node) {
	    System.err.printf("Select: %s\n", node);
        node.setSelected(bv, true);

        // TODO: Probably move the list of currently selected objects to the Board
        // Then, the BoardView can monitor this list and update its view accordingly
        bv.selectedObjectsProperty().add(node);     // Update selected nodes in view

        node.startDrag();
        clickObject(node);
	}


    /* Interactor specific high level functions */

    /**
     * @return A list of potentially selectable objects at the specified position.
     * 
     * @param mpos The position for which to return the objects.
     */
    protected List<Interactable> pickObjects(Point2D mpos) {
        return Collections.emptyList();
    }

    protected void startDrag(Interactable obj) {
    }

    protected void drag(Interactable obj, Point2D delta) {
    }

    protected void endDrag(Interactable obj) {
    }

    protected void clickObject(Interactable obj) {
    }

    protected void clickPos(Point2D pos) {
    }

    protected void rightClickObject(Interactable obj) {
    }
}
