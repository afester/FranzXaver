package afester.javafx.examples.board;

import afester.javafx.examples.board.model.Board;
import afester.javafx.examples.board.view.BoardCorner;
import afester.javafx.examples.board.view.BoardView;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

public class EditShapeInteractor extends MouseInteractor {

    private int cornerToMove = -1;

    public EditShapeInteractor(BoardView boardView) {
        super(boardView);
    }

    @Override
    protected void clickObjectLeft(Interactable obj) {
        if (obj instanceof BoardCorner) {
            System.err.println("Clicked: " + obj);
            cornerToMove = ((BoardCorner) obj).getCornerIdx();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        cornerToMove = -1;
    }

    
    @Override
    protected void dragObject(Interactable obj) {
        if (cornerToMove != -1) {
            Board b = this.getBoardView().getBoard();
            System.err.printf("Moving %s in %s\n", cornerToMove, b);
            Point2D newPos = this.getClickPos().add(this.getOffset());
            b.setCornerPos(cornerToMove, newPos);
        }
    }

    @Override
    public String toString() {
        return "EditShapeInteractor";
    }
}
