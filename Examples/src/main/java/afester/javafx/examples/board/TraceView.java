package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractWire;
import afester.javafx.examples.board.model.TraceType;
import javafx.scene.paint.Color;


/**
 * A Trace is a part of a Net which has already been routed.
 * It can either be rendered as trace or as bridge. 
 */
public class TraceView extends AbstractEdgeView {


    public TraceView(AbstractWire trace) {
        super(trace);

        showUnselected();

        trace.colorProperty().addListener((obj, oldColor, newColor) -> {
            setStroke(newColor);
        });
    }

//    public AbstractWire getTrace() {
//        return wire;
//    }


//    public void setSelected(boolean isSelected) {
//        trace.setSelected(isSelected);
////        if (isSelected) {
////            showSelected();
////        } else {
////            showUnselected();
////        }
//    }


    private void showUnselected() {
        switch(getType()) {
        case AIRWIRE:
            // TODO: We need a thicker selectionShape (a thicker transparent line) with the
            // same coordinates
            // so that selecting the line is easier
            setStrokeWidth(0.3); // 0.2);
            setStroke(Color.ORANGE);
            break;

        case BRIDGE:
            setStrokeWidth(0.5);
            setStroke(Color.GREEN);
            break;

        case TRACE:
            setStrokeWidth(1.0); // 0.2);
            setStroke(Color.SILVER);
            break;

        default:
            break;
        }
    }


//    private void showSelected() {
//        switch(getType()) {
//        case AIRWIRE:
//            // TODO: We need a thicker selectionShape (a thicker transparent line) with the
//            // same coordinates
//            // so that selecting the line is easier
//            setStrokeWidth(0.3); // 0.2);
//            setStroke(Color.RED);
//            break;
//
//        case BRIDGE:
//            setStrokeWidth(0.5);
//            setStroke(Color.RED);
//            break;
//
//        case TRACE:
//            setStrokeWidth(1.0); // 0.2);
//            setStroke(Color.RED);
////            from.setSelected(true);
////            to.setSelected(true);
//            break;
//
//        default:
//            break;
//        }
//    }



    @Override
    protected void setSegmentSelected(boolean isSelected) {
//      if (isSelected) {
//        from.setSelected(true);
//        to.setSelected(true);
//
//        setStroke(Color.RED);
//      } else {
//        from.setSelected(false);
//        to.setSelected(false);
//
//        if (isBridge) {
//            setStroke(Color.GREEN);
//        } else {
//            setStroke(Color.SILVER);
//        }
//      }
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
