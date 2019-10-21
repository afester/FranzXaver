package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractWire;
import afester.javafx.examples.board.view.BoardView;
import afester.javafx.examples.board.view.TraceView;

public class TraceInteractor extends MouseInteractor {

    public TraceInteractor(BoardView boardView) {
        super(boardView);
    }

//
//    @Override
//    protected void selectObject(Interactable obj) {
//        System.err.println("CLICKED LEFT!");
//        if (obj instanceof TraceView) {
//            TraceView tw = (TraceView) obj;       // TODO: How to remove this cast and the instanceof
//            final AbstractWire wire = tw.getTrace();
//            wire.convertToStraightTrace();
//        }
//    }
//

    @Override
    public String toString() {
        return "TraceInteractor";
    }
}
