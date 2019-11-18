package afester.javafx.examples.board;

import afester.javafx.examples.board.view.AbstractEdgeView;
import afester.javafx.examples.board.view.Handle;
import javafx.geometry.Point2D;

public abstract class AirWireHandle extends Handle {

    private AbstractEdgeView aw;

    public AirWireHandle(AbstractEdgeView airWire, Point2D pos) {
        super(pos, 0.5);
        this.aw = airWire;
    }

    public void disconnectListener() {
        centerXProperty().unbind();
        centerYProperty().unbind();
    }

    public AbstractEdgeView getAirWire() {
        return aw;
    }
}
