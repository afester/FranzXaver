package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractNode;
import afester.javafx.examples.board.model.AirWire;

public class FromHandle extends AirWireHandle {

    public FromHandle(TraceView airWireView) {
        super(airWireView, airWireView.getStart());
    }

    @Override
    public AbstractNode getNode() {
        return getAirWire().edge.getFrom();
    }
    
    @Override
    public String toString() {
        return String.format("FromHandle[pos=%s/%s]", getCenterX(), getCenterY());  
    }
}
