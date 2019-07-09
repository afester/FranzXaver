package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractNode;

public class ToHandle extends AirWireHandle {

    public ToHandle(TraceView airWire) {
        super(airWire, airWire.getEnd());
    }

    @Override
    public AbstractNode getNode() {
        return getAirWire().edge.getTo();
    }
    
    @Override
    public String toString() {
        return String.format("ToHandle[pos=%s/%s]", getCenterX(), getCenterY());  
    }
}
