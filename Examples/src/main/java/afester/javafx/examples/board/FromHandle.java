package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractNode;
import afester.javafx.examples.board.model.AirWire;
import javafx.geometry.Point2D;

public class FromHandle extends AirWireHandle {

    public FromHandle(TraceView airWireView) {
        super(airWireView, airWireView.getStart());
    }

    @Override
    public AbstractNode getNode() {
        return getAirWire().edge.getFrom();
    }

    @Override
    protected void setPosition(Point2D clickPos) {
        getAirWire().edge.reconnectFromNearestJunction(clickPos);
    }

    @Override
    public String toString() {
        return String.format("FromHandle[pos=%s/%s]", getCenterX(), getCenterY());  
    }

}
