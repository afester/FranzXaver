package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractNode;
import afester.javafx.examples.board.model.AirWire;
import afester.javafx.examples.board.model.Net;

public class ToHandle extends AirWireHandle {

    public ToHandle(Net net, AirWire airWire) {
        super(net, airWire, airWire.getTo().getPos());
    }

    public AbstractNode getNode() {
        return getAirWire().getTo();
    }
}
