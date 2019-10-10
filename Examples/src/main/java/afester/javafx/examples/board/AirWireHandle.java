package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractNode;
import afester.javafx.examples.board.view.TraceView;
import javafx.geometry.Point2D;

public abstract class AirWireHandle extends Handle {

    private TraceView aw;

    public AirWireHandle(TraceView airWire, Point2D pos) {
        super(pos, 0.5);
        this.aw = airWire;
    }
    

    public abstract AbstractNode getNode();
    
    public TraceView getAirWire() {
        return aw;
    }


    protected abstract void setPosition(Point2D clickPos);
}
