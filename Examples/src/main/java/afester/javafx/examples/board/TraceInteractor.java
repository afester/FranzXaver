package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AirWire;

public class TraceInteractor extends MouseInteractor {

    public TraceInteractor(BoardView boardView) {
        super(boardView);
    }

    @Override
    protected void clickObjectLeft(Interactable obj) {
        if (obj instanceof AirWire) {
            AirWire aw = (AirWire) obj;       // TODO: How to remove this cast and the instanceof
            aw.convertToStraightTrace();
        }
    }
}
