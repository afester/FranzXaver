package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractWire;
import afester.javafx.examples.board.model.AbstractWire.AbstractWireState;
import afester.javafx.examples.board.model.TraceType;
import javafx.scene.paint.Color;


/**
 * A Trace can either be rendered as an Airwire, Trace or Bridge. 
 */
public class TraceView extends AbstractEdgeView {

    public TraceView(AbstractWire trace) {
        super(trace);

        update(AbstractWireState.NORMAL);
        trace.stateProperty().addListener((obj, oldState, newState) -> {
            update(newState);
        });

//        trace.typeProperty().addListener((obj, oldColor, newColor) -> {
//
//        });
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
                getStyleClass().add("BridgeNormal");
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

    @Override
    public String toString() {
        return String.format("TraceView[p1=(%s %s), p2=(%s %s), type=%s]", 
                             this.getStart().getX(), this.getStart().getY(), 
                             this.getEnd().getX(), this.getEnd().getY(),
                             edge.getType());
    }

    public TraceType getType() {
        return edge.getType();
    }
}
