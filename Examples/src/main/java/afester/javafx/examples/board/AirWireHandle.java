package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractNode;
import afester.javafx.examples.board.model.AirWire;
import afester.javafx.examples.board.model.Net;
import javafx.geometry.Point2D;

public abstract class AirWireHandle extends Handle {

    private AirWire aw;

    public AirWireHandle(Net net, AirWire airWire, Point2D pos) {
        super(net, pos, 0.5);
        this.aw = airWire;
    }
    

    public abstract AbstractNode getNode();
    
    public AirWire getAirWire() {
        return aw;
    }
}
