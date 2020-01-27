package afester.javafx.examples.board.view;

import afester.javafx.examples.board.model.Pin;
import javafx.geometry.Point2D;

public class FromHandle extends AirWireHandle {

    public FromHandle(AbstractEdgeView airWireView) {
        super(airWireView, airWireView.getStart());

        // TODO: Make this easier to wire!
        setCenterX(airWireView.getStart().getX());
        setCenterY(airWireView.getStart().getY());
        airWireView.startProperty().addListener((obj, oldValue, newValue) -> {
            setCenterX(newValue.getX());
            setCenterY(newValue.getY());
        });
    }


    @Override
    public void startDrag() {
    }

    @Override
    public void drag(BoardView bv, Point2D newPos) {
        final var node = getAirWire().edge.getFrom();

        if (bv.isReconnectMode()) {
            // TODO: Fix API; this should be 
            // node.reconnectNearest(newPos);
            getAirWire().edge.reconnectFromClosestJunction(newPos);
        } else {
            if (!(node instanceof Pin)) {
                var snappedPos = bv.snapToGrid(newPos);
                node.setPosition(snappedPos);
            }
        }    
    }


    @Override
    public String toString() {
        return String.format("FromHandle[pos=%s/%s]", getCenterX(), getCenterY());  
    }
}
