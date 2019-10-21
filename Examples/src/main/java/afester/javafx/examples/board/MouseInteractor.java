package afester.javafx.examples.board;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import afester.javafx.examples.board.view.BoardView;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;


public abstract class MouseInteractor implements Interactor {


    private Point2D pickPos;
    private BoardView bv;

    private List<Interactable> pickedNodes;
    protected final Map<Interactable, Point2D> selectedNodes = new HashMap<>();
    private int pickIndex = 0;

    public MouseInteractor(BoardView boardView) {
    	bv = boardView;
	}
    
    public BoardView getBoardView() {
        return bv;
    }


	@Override
    public final void mousePressed(MouseEvent e) {
	    pickPos = new Point2D(e.getX(), e.getY());

        // find all Interactable nodes at the specified position. This is necessary to 
        // allow selecting nodes further down in the Z order.
        final Point2D mpos = new Point2D(e.getSceneX(), e.getSceneY());
        final List<Interactable> newPickedNodes = pickObjects(mpos);

        if (!newPickedNodes.equals(pickedNodes)) {
            pickedNodes = newPickedNodes;
            pickIndex = 0;
        }
	}

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!pickedNodes.isEmpty()) {
            final Interactable selectedNode = pickedNodes.get(pickIndex);
            pickIndex++;
            if (pickIndex >= pickedNodes.size()) {
                pickIndex = 0;
            }

            if (e.getButton() == MouseButton.PRIMARY) {
              if (e.isControlDown()) {
                  selectedNode.setSelected(true);
                  selectedNodes.put(selectedNode, selectedNode.getPos());
              } else if (e.isAltDown()) {
              } else if (e.isShiftDown()) {
              } else if (e.isMetaDown()) {
              } else { // no modifiers pressed
                  selectedNodes.keySet().forEach(node -> node.setSelected(false));
                  selectedNodes.clear();

                  selectedNode.setSelected(true);
                  selectedNodes.put(selectedNode, selectedNode.getPos());
                  //selectObject(selectedNode);
              }
           } else if (e.getButton() == MouseButton.SECONDARY) {
              rightClickObject(selectedNode);
           }
        } else if (!e.isControlDown()) {
            // bv.clearSelection();
            selectedNodes.keySet().forEach(node -> node.setSelected(false));
            selectedNodes.clear();
        }
    }


	@Override
    public final void mouseDragged(MouseEvent e) {
	    final Point2D newPos = new Point2D(e.getX(), e.getY()); 
	    final Point2D delta = newPos.subtract(pickPos);


        if (!pickedNodes.isEmpty()) {
            if (selectedNodes.isEmpty()) {
                final Interactable selectedNode = pickedNodes.get(pickIndex);

                if (e.getButton() == MouseButton.PRIMARY) {
                  if (e.isControlDown()) {
                  } else if (e.isAltDown()) {
                  } else if (e.isShiftDown()) {
                  } else if (e.isMetaDown()) {
                  } else { // no modifiers pressed
                      selectedNode.setSelected(true);
                      selectedNodes.put(selectedNode, selectedNode.getPos());
                  }
               }
            }

           if (!e.isControlDown() && e.isPrimaryButtonDown()) { // && currentObject != null) {
               selectedNodes.forEach((node, pos)  -> {
                   final Point2D newObjPos = pos.add(delta);
                   moveObject(node, newObjPos); 
               });
           }
        } 
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

    protected void selectObject(Interactable obj) {
    }

    protected void moveObject(Interactable obj, Point2D newPos) {
    }

    protected void rightClickObject(Interactable obj) {
    }
}
