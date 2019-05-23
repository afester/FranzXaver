package afester.javafx.examples.board;

import javafx.geometry.Point2D;

public class SelectInteractor extends MouseInteractor {

    
    public SelectInteractor(BoardView boardView) {
        super(boardView);
    }

    @Override
    protected void clickObjectLeft(Interactable obj) {
        final BoardView bv = getBoardView();

        Interactable currentSelection = bv.getSelectedObject();
        if (currentSelection != obj) {
            if (currentSelection != null) {
                currentSelection.setSelected(false);
            }

            obj.setSelected(true);
            bv.setSelectedObject(obj);
        }
    }
    
    @Override
    protected void clickObjectRight(Interactable obj) {
        if (obj instanceof PartView) {
            PartView part = (PartView) obj;
            part.rotatePart();
        }
    }


    @Override
    protected void dragObject(Interactable obj) {
        if (obj instanceof PartView) {
            PartView partView = (PartView) obj;
            // System.err.println("MOVE: " + obj);
    
            // Snap to center of part
            // (this is also what the Eagle board editor does)
            Point2D snapPos = snapToGrid(getClickPos(), getBoardView(), getOffset());
            partView.move(snapPos);
        }
    }
}
