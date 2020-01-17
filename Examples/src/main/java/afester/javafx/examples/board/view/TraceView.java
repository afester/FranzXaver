package afester.javafx.examples.board.view;

import afester.javafx.examples.board.ApplicationProperties;
import afester.javafx.examples.board.StyleSelector;
import afester.javafx.examples.board.model.AbstractEdge;
import afester.javafx.examples.board.model.AbstractEdge.AbstractWireState;
import afester.javafx.examples.board.model.TraceType;
import afester.javafx.examples.board.tools.StraightLine;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;


/**
 * A Trace can either be rendered as an Airwire, Trace or Bridge. 
 */
public class TraceView extends AbstractEdgeView implements Interactable {

    private Point2D originalPos = new Point2D(0, 0);
    private boolean isBottom = false;
    
    // The select line is used to determine the selection area.
    // It is NOT part of the scene graph!
    private final Line selectLine = new Line(); 

    public Line gradientLine;
    private final ApplicationProperties props;
    

    public TraceView(AbstractEdge trace, boolean isBottom, 
                     ApplicationProperties props) {
        super(trace);
        this.isBottom = isBottom;
        this.props = props;

        // POC: Calculate the gradient line
        StraightLine l = new StraightLine(getStart(), getEnd());
        var mp = l.getMidpoint();
        var norm = l.getNormalizedNormVector().multiply(0.4); // .multiply(3); // 3mm
        var p1 = mp.add(norm);
        var p2 = mp.subtract(norm);
        gradientLine = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        gradientLine.setStroke(Color.BLUE);
        gradientLine.setStrokeWidth(0.3);

        // fixed properties of the lines
        setStrokeLineCap(StrokeLineCap.ROUND);
        selectLine.setStrokeWidth(1.5);
        selectLine.setStroke(Color.CYAN);

        updateStart(startProperty().getValue());
        startProperty().addListener((obj, oldValue, newValue) -> {
            updateStart(newValue);
        });

        updateEnd(endProperty().getValue());
        endProperty().addListener((obj, oldValue, newValue) -> {
            updateEnd(newValue);
        });

        update(AbstractWireState.NORMAL);
        trace.stateProperty().addListener((obj, oldState, newState) -> {
            update(newState);
        });

    }
    
    private void updateStart(Point2D pos) {
        setStartX(pos.getX());
        setStartY(pos.getY());
        selectLine.setStartX(pos.getX());
        selectLine.setStartY(pos.getY());
    }

    private void updateEnd(Point2D pos) {
        setEndX(pos.getX());
        setEndY(pos.getY());
        selectLine.setEndX(pos.getX());
        selectLine.setEndY(pos.getY());
    }



    public void setShapeStroke(Color newValue) {
        var lightColor = newValue.deriveColor(0.0, 1.0, 1.2, 1.0);
        
        var stops = new Stop[] { new Stop(0, newValue), 
                                new Stop(0.5, lightColor),
                                new Stop(1, newValue)};
        var lg1 = new LinearGradient(gradientLine.getStartX(), gradientLine.getStartY(),
                            gradientLine.getEndX(), gradientLine.getEndY(), 
        false, CycleMethod.NO_CYCLE, stops);
        setStroke(lg1);
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

    
    private void updateStyle(ShapeStyle style) {
        setShapeStroke(style.getColor());
        setStrokeWidth(style.getWidth());
        setOpacity(style.getOpacity());
    }

    private void setTraceVisual(AbstractWireState newState) {
        switch(newState) {
            case NORMAL:
                if (isBottom) {
                    // updateStyle(props.getTraceNormalStyle());
                } else {
                    updateStyle(props.getStyle(StyleSelector.TOPTRACE_NORMAL));
                }
                break;

            case HIGHLIGHTED:
                if (isBottom) {
                    
                } else {
                    updateStyle(props.getStyle(StyleSelector.TOPTRACE_HIGHLIGHTED));

                }
                break;

            case SELECTED:
                if (isBottom) {
                    
                } else {
                    updateStyle(props.getStyle(StyleSelector.TOPTRACE_SELECTED));
                }
                break;
    
            default:
                break;
        }
    }

    private void setBridgeVisual(AbstractWireState newState) {
        switch(newState) {
            case NORMAL:
                if (isBottom) {
//1                    getStyleClass().add("BridgeNormalBottom");
                } else {
//1                    getStyleClass().add("BridgeNormal");
                }
                break;
    
            case HIGHLIGHTED:
//1                getStyleClass().add("BridgeHighlight");
                break;
    
            case SELECTED:
//1                getStyleClass().add("BridgeSelect");
                break;
    
            default:
                break;
        }
    }

    private void setAirwireVisual(AbstractWireState newState) {
        switch(newState) {
            case NORMAL:
//                setStroke(Color.ORANGE);
//                setStrokeWidth(0.3);
//1                getStyleClass().add("AirwireNormal");
                break;

            case HIGHLIGHTED:
//1                getStyleClass().add("AirwireHighlight");
                break;

            case SELECTED:
//1                getStyleClass().add("AirwireSelect");
                break;

            default:
                break;
        }
    }

    public AbstractEdge getTrace() {
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
    public void startDrag() {
        originalPos = getPos();
    }

    @Override
    public void drag(BoardView boardView, Point2D delta) {
        Point2D newPos = boardView.snapToGrid(originalPos.add(delta));
        edge.move(newPos);
    }

    @Override
    public String toString() {
        return String.format("TraceView[p1=(%s %s), p2=(%s %s), type=%s]", 
                             this.getStart().getX(), this.getStart().getY(), 
                             this.getEnd().getX(), this.getEnd().getY(),
                             edge.getType());
    }

    @Override
    public boolean contains(Point2D pos) {
        return selectLine.contains(pos);
    }
}
