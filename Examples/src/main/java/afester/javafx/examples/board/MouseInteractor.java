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
        } else if (target instanceof Handle) {
            result = (Interactable) target;
        }

        return result;
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
        Interactable clickedObject = getClickedObject(e);

        if (clickedObject != null) {

            offset = clickedObject.getPos().subtract(e.getX(), e.getY());

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
