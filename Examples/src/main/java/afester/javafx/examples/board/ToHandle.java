package afester.javafx.examples.board;

public class ToHandle extends AirWireHandle {

    public ToHandle(AirWire airWire) {
        super(airWire, airWire.to.getPos());
    }

    public AbstractNode getNode() {
        return getAirWire().getTo();
    }
}
