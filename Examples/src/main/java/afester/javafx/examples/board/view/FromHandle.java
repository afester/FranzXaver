package afester.javafx.examples.board.view;

import afester.javafx.examples.board.AirWireHandle;
import afester.javafx.examples.board.model.AbstractNode;
import javafx.geometry.Point2D;

public class FromHandle extends AirWireHandle {

    public FromHandle(AbstractEdgeView airWireView) {
        super(airWireView, airWireView.getStart());

    }

    @Override
    public AbstractNode getNode() {
        return getAirWire().edge.getFrom();
    }

    @Override
    protected void setPosition(Point2D clickPos) {
        getAirWire().edge.reconnectFromNearestJunction(clickPos);
    }


    @Override
    public void moveToGrid(BoardView bv, Point2D newPos) {
        System.err.println("MOVE " + this + " to " + newPos);
        newPos = bv.snapToGrid(newPos, false);
        this.getNode().setPosition(newPos);

        // setPosition(newPos);
    }


    @Override
    public String toString() {
        return String.format("FromHandle[pos=%s/%s]", getCenterX(), getCenterY());  
    }

}
