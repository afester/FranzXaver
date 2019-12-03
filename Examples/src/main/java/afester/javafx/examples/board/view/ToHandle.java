package afester.javafx.examples.board.view;

import afester.javafx.examples.board.model.Pin;
import javafx.geometry.Point2D;

public class ToHandle extends AirWireHandle {

    public ToHandle(AbstractEdgeView airWireView) {
        super(airWireView, airWireView.getEnd());

        centerXProperty().bind(airWireView.endXProperty());
        centerYProperty().bind(airWireView.endYProperty());
    }

    @Override
    public void startDrag() {
    }

    @Override
    public void drag(BoardView bv, Point2D newPos) {
        final var node = getAirWire().edge.getTo();

        if (bv.isReconnectMode()) {
            // TODO: Fix API; this should be 
            // node.reconnectNearest(newPos);
            getAirWire().edge.reconnectToNearestJunction(newPos);
        } else {
            if (!(node instanceof Pin)) {
                var snappedPos = bv.snapToGrid(newPos);
                node.setPosition(snappedPos);
            }
        }
    }


    @Override
    public String toString() {
        return String.format("ToHandle[pos=%s/%s]", getCenterX(), getCenterY());  
    }
}
