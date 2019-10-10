package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractNode;
import afester.javafx.examples.board.view.TraceView;
import javafx.geometry.Point2D;

public class ToHandle extends AirWireHandle {

    public ToHandle(TraceView airWire) {
        super(airWire, airWire.getEnd());
    } 

    @Override
    public AbstractNode getNode() {
        return getAirWire().edge.getTo();
    }

    @Override
    protected void setPosition(Point2D clickPos) {
        getAirWire().edge.reconnectToNearestJunction(clickPos);
    }

    @Override
    public String toString() {
        return String.format("ToHandle[pos=%s/%s]", getCenterX(), getCenterY());  
    }
}
