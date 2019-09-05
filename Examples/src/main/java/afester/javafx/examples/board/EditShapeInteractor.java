package afester.javafx.examples.board;

import afester.javafx.examples.board.model.Board;

public class EditShapeInteractor extends MouseInteractor {

    private int cornerToMove = 0;

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
    protected void dragObject(Interactable obj) {
        Board b = this.getBoardView().getBoard();
        System.err.printf("Moving %s in %s\n", cornerToMove, b);
    }
    
}
