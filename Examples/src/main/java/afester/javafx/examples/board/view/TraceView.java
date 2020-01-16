package afester.javafx.examples.board.view;

import afester.javafx.examples.board.ApplicationProperties;
import afester.javafx.examples.board.model.AbstractEdge;
import afester.javafx.examples.board.model.AbstractEdge.AbstractWireState;
import afester.javafx.examples.board.model.TraceType;
import afester.javafx.examples.board.tools.StraightLine;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;


/**
 * A Trace can either be rendered as an Airwire, Trace or Bridge. 
 */
public class TraceView extends AbstractEdgeView implements Interactable {

    private Point2D originalPos = new Point2D(0, 0);
    private boolean isBottom = false;

    public final Line theLine = new Line();
    public final Line theLine2 = new Line();
    
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
        theLine.setStrokeLineCap(StrokeLineCap.ROUND);
        theLine2.setStrokeLineCap(StrokeLineCap.ROUND);
        selectLine.setStrokeWidth(1.5);
        selectLine.setStroke(Color.CYAN);
//
//        setStrokeWidth(1.0);
//        setStroke(Color.LIGHTGRAY);

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

        theLine.visibleProperty().bind(visibleProperty());
        theLine2.visibleProperty().bind(visibleProperty());
        
    }
    
    private void updateStart(Point2D pos) {
        theLine.setStartX(pos.getX());
        theLine.setStartY(pos.getY());
        theLine2.setStartX(pos.getX());
        theLine2.setStartY(pos.getY());
        selectLine.setStartX(pos.getX());
        selectLine.setStartY(pos.getY());
    }

    private void updateEnd(Point2D pos) {
        theLine.setEndX(pos.getX()); 
        theLine.setEndY(pos.getY());
        theLine2.setEndX(pos.getX()); 
        theLine2.setEndY(pos.getY());
        selectLine.setEndX(pos.getX());
        selectLine.setEndY(pos.getY());
    }


    public void setStroke(Color newValue) {
//        theLine.setStroke(newValue);
        
        var lightColor = newValue.deriveColor(0.0, 1.0, 1.2, 1.0);
        
        var stops = new Stop[] { new Stop(0, newValue), 
                                new Stop(0.5, lightColor),
                                new Stop(1, newValue)};
        var lg1 = new LinearGradient(gradientLine.getStartX(), gradientLine.getStartY(),
                            gradientLine.getEndX(), gradientLine.getEndY(), 
        false, CycleMethod.NO_CYCLE, stops);
        theLine.setStroke(lg1);

        theLine2.setStroke(lightColor);
    }


    public void setStrokeWidth(double newValue) {
        theLine.setStrokeWidth(newValue);
        theLine2.setStrokeWidth(newValue/10);
    }


    private void setOpacity(double newValue) {
        theLine.setOpacity(newValue);
        theLine2.setOpacity(newValue);
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
        setStroke(style.getColor());
        setStrokeWidth(style.getWidth());
        setOpacity(style.getOpacity());
    }

    private void setTraceVisual(AbstractWireState newState) {
        switch(newState) {
            case NORMAL:
                if (isBottom) {
                    // updateStyle(props.getTraceNormalStyle());
                } else {
                    updateStyle(props.getTopTraceNormalStyle());
                }
                break;

            case HIGHLIGHTED:
                if (isBottom) {
                    
                } else {
                    updateStyle(props.getTopTraceHighlightedStyle());
                }
                break;

            case SELECTED:
                if (isBottom) {
                    
                } else {
                    updateStyle(props.getTopTraceSelectedStyle());
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

    public Point2D sceneToLocal(Point2D mpos) {
        return theLine.sceneToLocal(mpos);
    }

    public boolean contains(Point2D pos) {
        return selectLine.contains(pos);
    }
}
