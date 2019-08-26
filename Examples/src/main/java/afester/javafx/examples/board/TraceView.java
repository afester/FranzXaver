package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractWire;
import afester.javafx.examples.board.model.AbstractWire.AbstractWireState;
import afester.javafx.examples.board.model.TraceType;
import javafx.scene.paint.Color;


/**
 * A Trace is a part of a Net which has already been routed.
 * It can either be rendered as trace or as bridge. 
 */
public class TraceView extends AbstractEdgeView {

    private static Color AIRWIRE_NORMAL_COLOR = Color.ORANGE;
    private static Color AIRWIRE_HIGHLIGHT_COLOR = new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), 0.5);
    private static Color AIRWIRE_SELECTED_COLOR = Color.RED;

    private static Color TRACE_NORMAL_COLOR = Color.SILVER;
    private static Color TRACE_HIGHLIGHT_COLOR = new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), 0.5);
    private static Color TRACE_SELECTED_COLOR = Color.RED;

    private static Color BRIDGE_NORMAL_COLOR = Color.GREEN;
    private static Color BRIDGE_HIGHLIGHT_COLOR = new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), 0.5);
    private static Color BRIDGE_SELECTED_COLOR = Color.RED;


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
        setStrokeWidth(1.0);

        switch(newState) {
            case NORMAL:
                setStroke(TRACE_NORMAL_COLOR);
                break;
    
            case HIGHLIGHTED:
                setStroke(TRACE_HIGHLIGHT_COLOR);
                break;
    
            case SELECTED:
                setStroke(TRACE_SELECTED_COLOR);
                break;
    
            default:
                break;
        }
    }

    private void setBridgeVisual(AbstractWireState newState) {
        setStrokeWidth(0.5);

        switch(newState) {
            case NORMAL:
                setStroke(BRIDGE_NORMAL_COLOR);
                break;
    
            case HIGHLIGHTED:
                setStroke(BRIDGE_HIGHLIGHT_COLOR);
                break;
    
            case SELECTED:
                setStroke(BRIDGE_SELECTED_COLOR);
                break;
    
            default:
                break;
        }
    }

    private void setAirwireVisual(AbstractWireState newState) {
        setStrokeWidth(0.3);

        switch(newState) {
            case NORMAL:
                setStroke(AIRWIRE_NORMAL_COLOR);
                break;

            case HIGHLIGHTED:
                setStroke(AIRWIRE_HIGHLIGHT_COLOR);
                break;

            case SELECTED:
                setStroke(AIRWIRE_SELECTED_COLOR);
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
