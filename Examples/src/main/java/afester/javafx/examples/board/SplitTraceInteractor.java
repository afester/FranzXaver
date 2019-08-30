package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractNode;
import afester.javafx.examples.board.model.Junction;
import afester.javafx.examples.board.model.Net;
import afester.javafx.examples.board.model.Trace;
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

            Junction newJunction = new Junction(net, snapPos);
            net.addJunction(newJunction);
//            trace.reconnect(trace.getTo(), newJunction);    
            
            Trace newTrace = new Trace(newJunction, oldDest, net);
            net.addTrace(newTrace);
        }
    }
}
