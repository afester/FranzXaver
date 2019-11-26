package afester.javafx.examples.board.view;

import afester.javafx.examples.board.model.AbstractNode;
import afester.javafx.examples.board.model.AbstractWire;
import afester.javafx.examples.board.model.AbstractWire.AbstractWireState;
import afester.javafx.examples.board.model.TraceType;
import javafx.geometry.Point2D;


/**
 * A Trace can either be rendered as an Airwire, Trace or Bridge. 
 */
public class TraceView extends AbstractEdgeView {

    private Point2D originalPos = new Point2D(0, 0);
    private boolean isBottom = false;

    public TraceView(AbstractWire trace, boolean isBottom) {
        super(trace);
        this.isBottom = isBottom;

        update(AbstractWireState.NORMAL);
        trace.stateProperty().addListener((obj, oldState, newState) -> {
            update(newState);
        });
    }

    private void update(AbstractWireState newState) {
        getStyleClass().clear();
        switch(getTrace().getType()) {
            case AIRWIRE: setAirwireVisual(newState);
                          break;
    
            case BRIDGE: setBridgeVisual(newState);
                         break;
    
            case TRACE: setTraceVisual(newState);
                        break;
    
            default:
                break;
        }
    }

    private void setTraceVisual(AbstractWireState newState) {
        switch(newState) {
            case NORMAL:
                getStyleClass().add("TraceNormal");
                break;
    
            case HIGHLIGHTED:
                getStyleClass().add("TraceHighlight");
                break;
    
            case SELECTED:
                getStyleClass().add("TraceSelect");
                break;
    
            default:
                break;
        }
    }

    private void setBridgeVisual(AbstractWireState newState) {
        switch(newState) {
            case NORMAL:
                if (isBottom) {
                    getStyleClass().add("BridgeNormalBottom");
                } else {
                    getStyleClass().add("BridgeNormal");
                }
                break;
    
            case HIGHLIGHTED:
                getStyleClass().add("BridgeHighlight");
                break;
    
            case SELECTED:
                getStyleClass().add("BridgeSelect");
                break;
    
            default:
                break;
        }
    }

    private void setAirwireVisual(AbstractWireState newState) {
        switch(newState) {
            case NORMAL:
                getStyleClass().add("AirwireNormal");
                break;

            case HIGHLIGHTED:
                getStyleClass().add("AirwireHighlight");
                break;

            case SELECTED:
                getStyleClass().add("AirwireSelect");
                break;

            default:
                break;
        }
    }

    public AbstractWire getTrace() {
        return edge;
    }

    public TraceType getType() {
        return edge.getType();
    }

    @Override
    public Point2D getPos() {
        // The start position is the reference point!
        return getStart();
    }

    @Override
    public void drag(BoardView boardView, Point2D delta1) {
        Point2D newPos = originalPos.add(delta1);

        AbstractNode fromNode = edge.getFrom();
        AbstractNode toNode = edge.getTo();

        final var delta = toNode.getPosition().subtract(fromNode.getPosition());

        // The start node is the reference point and can directly be set to the new position.
        final var snappedPos = boardView.snapToGrid(newPos, false);
        fromNode.setPosition(snappedPos);

        // update dependent positions
        final var newToPos = fromNode.getPosition().add(delta);
        toNode.setPosition(newToPos);
    }
    
    @Override
    public String toString() {
        return String.format("TraceView[p1=(%s %s), p2=(%s %s), type=%s]", 
                             this.getStart().getX(), this.getStart().getY(), 
                             this.getEnd().getX(), this.getEnd().getY(),
                             edge.getType());
    }

    @Override
    public void startDrag() {
        originalPos = getPos();
    }

}
