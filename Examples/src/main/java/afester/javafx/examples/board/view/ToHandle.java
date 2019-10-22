package afester.javafx.examples.board.view;

import afester.javafx.examples.board.AirWireHandle;
import afester.javafx.examples.board.model.AbstractNode;
import javafx.geometry.Point2D;

public class ToHandle extends AirWireHandle {

    public ToHandle(AbstractEdgeView airWire) {
        super(airWire, airWire.getEnd());
    } 

    @Override
    public AbstractNode getNode() {
        return getAirWire().edge.getTo();
    }

    @Override
    public void moveToGrid(BoardView bv, Point2D newPos) {
        if (bv.isReconnectMode()) {
            getAirWire().edge.reconnectToNearestJunction(newPos);
        } else {
            newPos = bv.snapToGrid(newPos, false);
            this.getNode().setPosition(newPos);
        }
    }


    @Override
    public String toString() {
        return String.format("ToHandle[pos=%s/%s]", getCenterX(), getCenterY());  
    }
}
