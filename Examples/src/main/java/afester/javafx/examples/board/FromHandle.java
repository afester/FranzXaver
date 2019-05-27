package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractNode;
import afester.javafx.examples.board.model.AirWire;
import afester.javafx.examples.board.model.Net;

public class FromHandle extends AirWireHandle {

    public FromHandle(Net net, AirWire airWire) {
        super(net, airWire, airWire.getFrom().getPos());
    }
    
    public AbstractNode getNode() {
        return getAirWire().getFrom();
    }
}
