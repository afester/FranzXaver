package afester.javafx.examples.board.view;

import afester.javafx.examples.board.AirWireHandle;
import afester.javafx.examples.board.model.AbstractNode;
import javafx.geometry.Point2D;

public class FromHandle extends AirWireHandle {

    public FromHandle(AbstractEdgeView airWireView) {
        super(airWireView, airWireView.getStart());

    }

    @Override
    public AbstractNode getNode() {
        return getAirWire().edge.getFrom();
    }


    @Override
    public void moveToGrid(BoardView bv, Point2D newPos) {
        if (bv.isReconnectMode()) {
            getAirWire().edge.reconnectFromNearestJunction(newPos);
        } else {
            newPos = bv.snapToGrid(newPos, false);
            this.getNode().setPosition(newPos);
        }    
    }


    @Override
    public String toString() {
        return String.format("FromHandle[pos=%s/%s]", getCenterX(), getCenterY());  
    }

}
