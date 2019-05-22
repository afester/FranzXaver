package afester.javafx.examples.board;

import javafx.geometry.Point2D;

public abstract class AirWireHandle extends Handle {

    private AirWire aw;

    public AirWireHandle(AirWire airWire, Point2D pos) {
        super(pos, 0.5);
        this.aw = airWire;
    }
    

    public abstract AbstractNode getNode();
    
    public AirWire getAirWire() {
        return aw;
    }
}
