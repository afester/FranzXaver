package afester.javafx.examples.board;

import java.util.List;

import afester.javafx.examples.board.model.Part;
import afester.javafx.examples.board.view.BoardView;
import afester.javafx.examples.board.view.PartView;
import javafx.geometry.Point2D;


public class EditInteractor  extends MouseInteractor {

    public EditInteractor(BoardView boardView) {
        super(boardView);
    }

    @Override
    protected List<Interactable> pickObjects(Point2D mpos) {
        // return a list of all selectable objects at the given mouse position (Parts and Net segments) 
        return getBoardView().getPartsAndNets(mpos);
    }

    @Override
    protected void drag(Interactable obj, Point2D newPos) {
        obj.drag(getBoardView(), newPos);
    }

    @Override
    protected void rightClickObject(Interactable obj) {
        if (obj instanceof PartView) {
            PartView partView = (PartView) obj;
            Part part = partView.getPart();
            part.rotateClockwise();
        }
    }
    
    @Override
    public String toString() {
        return "EditInteractor";
    }
}
