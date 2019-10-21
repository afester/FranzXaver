package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractNode;
import afester.javafx.examples.board.view.AbstractEdgeView;
import afester.javafx.examples.board.view.BoardView;
import afester.javafx.examples.board.view.Handle;
import afester.javafx.examples.board.view.TraceView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;

public abstract class AirWireHandle extends Handle implements ChangeListener<Point2D> {

    private AbstractEdgeView aw;

    @Override
    public void changed(ObservableValue<? extends Point2D> observable, Point2D oldValue, Point2D newValue) {
      System.err.println("CHANGED!" + this);
      this.setCenterX(newValue.getX());
      this.setCenterY(newValue.getY());
    }

    public AirWireHandle(AbstractEdgeView airWire, Point2D pos) {
        super(pos, 0.5);
        this.aw = airWire;

        // TODO: the Listener is never removed afterwards!
        getNode().positionProperty().addListener(this);
//        (obj, oldValue, newValue) -> {
//            System.err.println("CHANGED!" + this);
//            this.setCenterX(newValue.getX());
//            this.setCenterY(newValue.getY());
//        });
    }

    public void disconnectListener() {
        getNode().positionProperty().removeListener(this);
    }

    public abstract AbstractNode getNode();
    
    public AbstractEdgeView getAirWire() {
        return aw;
    }


    protected abstract void setPosition(Point2D clickPos);
}
