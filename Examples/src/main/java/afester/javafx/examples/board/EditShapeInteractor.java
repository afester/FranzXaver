package afester.javafx.examples.board;

import java.util.List;

import afester.javafx.examples.board.view.BoardView;
import javafx.geometry.Point2D;

public class EditShapeInteractor extends MouseInteractor {

    public EditShapeInteractor(BoardView boardView) {
        super(boardView);
    }

    @Override
    protected List<Interactable> pickObjects(Point2D mpos) {
        // return a list of all selectable objects at the given mouse position (Parts and Net segments)
        return getBoardView().getBoardHandleGroup().pickAll(mpos);
    }

    @Override
    protected void moveObject(Interactable obj, Point2D newPos) {
        obj.moveToGrid(getBoardView(), newPos);
    }

    @Override
    public String toString() {
        return "EditShapeInteractor";
    }
}
