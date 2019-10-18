package afester.javafx.examples.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import afester.javafx.examples.board.view.BoardView;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;


public abstract class MouseInteractor implements Interactor {

    private Point2D offset; 
    private Point2D pos; 
    private BoardView bv;

    private List<Interactable> pickedNodes;
    protected final List<Interactable> selectedNodes = new ArrayList<>();
    private int pickIndex = 0;

    public MouseInteractor(BoardView boardView) {
    	bv = boardView;
	}
    
    public BoardView getBoardView() {
        return bv;
    }

    public Point2D getOffset() {
        return offset;
    }

    /**
     * @return The position where the mouse click occurred.
     */
    public final Point2D getClickPos() {
        return pos;
    }

	@Override
    public final void mousePressed(MouseEvent e) {
	    pos = new Point2D(e.getX(), e.getY());

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

            offset = selectedNode.getPos().subtract(e.getX(), e.getY());

            if (e.getButton() == MouseButton.PRIMARY) {
              if (e.isControlDown()) {
                  selectedNode.setSelected(true);
                  selectedNodes.add(selectedNode);
              } else if (e.isAltDown()) {
              } else if (e.isShiftDown()) {
              } else if (e.isMetaDown()) {
              } else { // no modifiers pressed
                  selectedNodes.forEach(node -> node.setSelected(false));
                  selectedNodes.clear();

                  selectedNode.setSelected(true);
                  selectedNodes.add(selectedNode);
                  //selectObject(selectedNode);
              }
           } else if (e.getButton() == MouseButton.SECONDARY) {
              rightClickObject(selectedNode);
           }
        } else if (!e.isControlDown()) {
            // bv.clearSelection();
            selectedNodes.forEach(node -> node.setSelected(false));
            selectedNodes.clear();
        }
    }


	@Override
    public final void mouseDragged(MouseEvent e) {
        pos = new Point2D(e.getX(), e.getY());

        if (!pickedNodes.isEmpty()) {
            if (selectedNodes.isEmpty()) {
                final Interactable selectedNode = pickedNodes.get(pickIndex);
                
                offset = selectedNode.getPos().subtract(e.getX(), e.getY());

                if (e.getButton() == MouseButton.PRIMARY) {
                  if (e.isControlDown()) {
                  } else if (e.isAltDown()) {
                  } else if (e.isShiftDown()) {
                  } else if (e.isMetaDown()) {
                  } else { // no modifiers pressed
                      selectedNode.setSelected(true);
                      selectedNodes.add(selectedNode);
                      // selectObject(selectedNode);
                  }
               }
            }

           Interactable currentObject = bv.getSelectedObject();
           if (!e.isControlDown() && e.isPrimaryButtonDown()) { // && currentObject != null) {
               moveSelectedObjects();
           }
        } 
    }
	

	// private final static double GRID = 2.54;
	private final static double GRID = 1.27;   // for now, we also allow positions between pads - this is        
                                               // required to properly position the Eagle parts ...


    @Deprecated
    protected Point2D snapToGrid(Point2D pos, BoardView bv, Point2D offset) {                                                      
        System.err.println("OFFSET:" + offset);
        System.err.println("POS   :" + pos);
        double xpos = offset.getX() + pos.getX();                                                                        
        double ypos = offset.getY() + pos.getY();                                                                        

        xpos = (int) ( (xpos - bv.getPadOffset().getX()) / GRID);                                         
        ypos = (int) ( (ypos - bv.getPadOffset().getY()) / GRID);                                         
    
        xpos = xpos * GRID + bv.getPadOffset().getX();                                                    
        ypos = ypos * GRID + bv.getPadOffset().getY();                                                    
    
        return new Point2D(xpos, ypos);                                                                   
    }


    protected Point2D snapToGrid(Point2D clickPos, boolean useOffset){
        if (useOffset) {
            System.err.println("OFFSET:" + offset);
            System.err.println("POS   :" + pos);
            double xpos = offset.getX() + pos.getX();                                                                        
            double ypos = offset.getY() + pos.getY();                                                                        
    
            xpos = (int) ( (xpos - bv.getPadOffset().getX()) / GRID);                                         
            ypos = (int) ( (ypos - bv.getPadOffset().getY()) / GRID);                                         
        
            xpos = xpos * GRID + bv.getPadOffset().getX();                                                    
            ypos = ypos * GRID + bv.getPadOffset().getY();                                                    
        
            return new Point2D(xpos, ypos);
        } else {
            System.err.println("POS   :" + pos);
            double xpos = pos.getX();                                                                        
            double ypos = pos.getY();                                                                        
    
            xpos = (int) ( (xpos - bv.getPadOffset().getX()) / GRID);                                         
            ypos = (int) ( (ypos - bv.getPadOffset().getY()) / GRID);                                         
        
            xpos = xpos * GRID + bv.getPadOffset().getX();                                                    
            ypos = ypos * GRID + bv.getPadOffset().getY();                                                    

            return new Point2D(xpos, ypos);
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

    protected void moveSelectedObjects() {
    }

    protected void rightClickObject(Interactable obj) {
    }
}
