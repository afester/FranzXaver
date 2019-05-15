package afester.javafx.examples.board;

import javafx.geometry.Point2D;

public class SplitTraceInteractor extends MouseInteractor {

    public SplitTraceInteractor(BoardView boardView) {
        super(boardView);
    }

    @Override
    protected void clickObjectLeft(Interactable obj) {
        if (obj instanceof Trace) {
            Trace trace = (Trace) obj;
            Net net = trace.getNet();
            AbstractNode oldDest = trace.getTo();
            Point2D snapPos = snapToGrid(getClickPos(), false);

            Junction newJunction = new Junction(snapPos);
            net.addJunction(newJunction);
            trace.reconnect(trace.getTo(), newJunction);    
            
            Trace newTrace = new Trace(newJunction, oldDest);
            net.addTrace(newTrace);
        }
    }
}
