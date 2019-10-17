package afester.javafx.examples.board;

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
    private Interactable selectedNode = null;
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
    public final void mousePressed(List<Interactable> newPickedNodes, MouseEvent e) {
	    pos = new Point2D(e.getX(), e.getY());

        if (!newPickedNodes.equals(pickedNodes)) {
            pickedNodes = newPickedNodes;
            pickIndex = 0;
        }
	}

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!pickedNodes.isEmpty()) {
            selectedNode = pickedNodes.get(pickIndex);
            pickIndex++;
            if (pickIndex >= pickedNodes.size()) {
                pickIndex = 0;
            }

            offset = selectedNode.getPos().subtract(e.getX(), e.getY());

            if (e.getButton() == MouseButton.PRIMARY) {
              if (e.isControlDown()) {
              } else if (e.isAltDown()) {
              } else if (e.isShiftDown()) {
              } else if (e.isMetaDown()) {
              } else { // no modifiers pressed
                  clickObjectLeft(selectedNode);
              }
           } else if (e.getButton() == MouseButton.SECONDARY) {
              clickObjectRight(selectedNode);
           }
        } else {
            bv.clearSelection();
            selectedNode = null;
        }
    }


	@Override
    public final void mouseDragged(MouseEvent e) {
        pos = new Point2D(e.getX(), e.getY());

        if (!pickedNodes.isEmpty()) {
            if (selectedNode == null) {
                selectedNode = pickedNodes.get(pickIndex);
                
                offset = selectedNode.getPos().subtract(e.getX(), e.getY());

                if (e.getButton() == MouseButton.PRIMARY) {
                  if (e.isControlDown()) {
                  } else if (e.isAltDown()) {
                  } else if (e.isShiftDown()) {
                  } else if (e.isMetaDown()) {
                  } else { // no modifiers pressed
                      clickObjectLeft(selectedNode);
                  }
               }
            }

           Interactable currentObject = bv.getSelectedObject();
           if (!e.isControlDown() && e.isPrimaryButtonDown()) { // && currentObject != null) {
               dragObject(currentObject);
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

    
	protected void dragObject(Interactable obj) {
	}

    protected void clickObjectLeft(Interactable obj) {
    }

    protected void clickObjectRight(Interactable obj) {
    }
}
