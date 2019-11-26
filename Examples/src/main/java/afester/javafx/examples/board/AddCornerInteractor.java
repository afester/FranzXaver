package afester.javafx.examples.board;

import afester.javafx.examples.board.view.BoardView;
import javafx.geometry.Point2D;

public class AddCornerInteractor extends MouseInteractor {

    public AddCornerInteractor(BoardView boardView) {
        super(boardView);
    }

    
    @Override
    public String toString() {
        return "AddCornerInteractor";
    }


    @Override
    protected void clickPos(Point2D pos) {
        System.err.println("Clicked at pos " + pos);
        getBoardView().getBoard().addCorner(pos);
    }
}
