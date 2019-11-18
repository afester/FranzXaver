package afester.javafx.examples.board.view;

import afester.javafx.examples.board.AirWireHandle;
import afester.javafx.examples.board.model.AbstractNode;
import afester.javafx.examples.board.model.Pin;
import javafx.geometry.Point2D;

public class FromHandle extends AirWireHandle {

    public FromHandle(AbstractEdgeView airWireView) {
        super(airWireView, airWireView.getStart());

        centerXProperty().bind(airWireView.startXProperty());
        centerYProperty().bind(airWireView.startYProperty());
    }

    @Override
    public void moveToGrid(BoardView bv, Point2D newPos) {
        AbstractNode node = getAirWire().edge.getFrom();

        if (bv.isReconnectMode()) {
            // TODO: Fix API; this should be 
            // node.reconnectNearest(newPos);
            getAirWire().edge.reconnectFromNearestJunction(newPos);
        } else {
            if (!(node instanceof Pin)) {
                newPos = bv.snapToGrid(newPos, false);
                node.setPosition(newPos);
            }
        }    
    }


    @Override
    public String toString() {
        return String.format("FromHandle[pos=%s/%s]", getCenterX(), getCenterY());  
    }
}
