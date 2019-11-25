package afester.javafx.examples.board.view;

import afester.javafx.examples.board.AirWireHandle;
import afester.javafx.examples.board.model.Pin;
import javafx.geometry.Point2D;

public class FromHandle extends AirWireHandle {

    public FromHandle(AbstractEdgeView airWireView) {
        super(airWireView, airWireView.getStart());

        centerXProperty().bind(airWireView.startXProperty());
        centerYProperty().bind(airWireView.startYProperty());
    }

    @Override
    public void drag(BoardView bv, Point2D newPos) {
        final var node = getAirWire().edge.getFrom();

        if (bv.isReconnectMode()) {
            // TODO: Fix API; this should be 
            // node.reconnectNearest(newPos);
            getAirWire().edge.reconnectFromNearestJunction(newPos);
        } else {
            if (!(node instanceof Pin)) {
                var snappedPos = bv.snapToGrid(newPos, false);
                node.setPosition(snappedPos);
            }
        }    
    }


    @Override
    public String toString() {
        return String.format("FromHandle[pos=%s/%s]", getCenterX(), getCenterY());  
    }

    @Override
    public void startDrag() {
        // TODO Auto-generated method stub
        
    }
}
