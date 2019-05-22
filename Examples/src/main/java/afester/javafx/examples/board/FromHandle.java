package afester.javafx.examples.board;

public class FromHandle extends AirWireHandle {

    public FromHandle(AirWire airWire) {
        super(airWire, airWire.from.getPos());
    }
    
    public AbstractNode getNode() {
        return getAirWire().getFrom();
    }
}
