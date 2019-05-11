package afester.javafx.examples.board;

import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public abstract class MouseInteractor implements Interactor {

    private Point2D offset; 
    private Point2D pos; 
    private BoardView bv;

    public MouseInteractor(BoardView boardView) {
    	bv = boardView;
	}

    // TODO: This is a mess.
    protected Interactable getClickedObject(MouseEvent e) {
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

    
    public BoardView getBoardView() {
        return bv;
    }

    public Point2D getOffset() {
        return offset;
    }

    public Point2D getPos() {
        return pos;
    }

	@Override
    public final void mousePressed(MouseEvent e) {
	    pos = new Point2D(e.getX(), e.getY());
        Interactable clickedObject = getClickedObject(e);
        if (clickedObject != null) {

            if (e.getButton() == MouseButton.PRIMARY) {
                if (e.isControlDown()) {
                } else if (e.isAltDown()) {
                } else if (e.isShiftDown()) {
                } else if (e.isMetaDown()) {
                } else { // no modifiers pressed
                    clickObjectLeft(clickedObject);
                }
            } else if (e.getButton() == MouseButton.SECONDARY) {
                clickObjectRight(clickedObject);
            }

            offset = clickedObject.getPos().subtract(e.getX(), e.getY());
          } else {
            bv.clearSelection();
        }
	}

	
	@Override
    public final void mouseDragged(MouseEvent e) {
	    pos = new Point2D(e.getX(), e.getY());
        Interactable currentObject = bv.getSelectedObject();
        if (!e.isControlDown() && e.isPrimaryButtonDown() && currentObject != null) {
            dragObject(currentObject);
        }
    }

	
    protected Point2D snapToGrid(Point2D pos, BoardView bv, Point2D offset) {                                                      
        // final double grid = 2.54;                                                                      
        final double grid = 1.27;       // for now, we also allow positions between pads - this is        
                                        // required to properly position the Eagle parts ...              

        double xpos = offset.getX() + pos.getX();                                                                        
        double ypos = offset.getY() + pos.getY();                                                                        

        xpos = (int) ( (xpos - bv.getPadOffset().getX()) / grid);                                         
        ypos = (int) ( (ypos - bv.getPadOffset().getY()) / grid);                                         
    
        xpos = xpos * grid + bv.getPadOffset().getX();                                                    
        ypos = ypos * grid + bv.getPadOffset().getY();                                                    
    
        return new Point2D(xpos, ypos);                                                                   
    }

    
    
	protected void dragObject(Interactable obj) {
	}

    protected void clickObjectLeft(Interactable obj) {
    }

    protected void clickObjectRight(Interactable obj) {
    }
}
