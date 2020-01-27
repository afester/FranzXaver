package afester.javafx.examples.board.view;

import afester.javafx.examples.board.model.Pin;
import javafx.geometry.Point2D;

public class ToHandle extends AirWireHandle {

    public ToHandle(AbstractEdgeView airWireView) {
        super(airWireView, airWireView.getEnd());

        // TODO: Make this easier to bind
        setCenterX(airWireView.getEnd().getX());
        setCenterY(airWireView.getEnd().getY());
        airWireView.endProperty().addListener((obj, oldValue, newValue) -> {
            setCenterX(newValue.getX());
            setCenterY(newValue.getY());
        });
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
            getAirWire().edge.reconnectToClosestJunction(newPos);
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
