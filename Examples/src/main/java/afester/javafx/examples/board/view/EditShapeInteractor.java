package afester.javafx.examples.board.view;

import java.util.List;

import javafx.geometry.Point2D;

public class EditShapeInteractor extends MouseInteractor {

    public EditShapeInteractor(BoardView boardView) {
        super(boardView);
    }

    @Override
    protected List<Interactable> pickObjects(Point2D mpos) {
        // return a list of all selectable objects at the given mouse position (Board handles)
        return getBoardView().getBoardHandleGroup().pickAll(mpos);
    }

//    @Override
//    protected void moveObject(Interactable obj, Point2D newPos) {
//        obj.move(getBoardView(), newPos);
//    }

    @Override
    public String toString() {
        return "EditShapeInteractor";
    }
}
