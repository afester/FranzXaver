package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractWire;

/**
 * A Trace is a part of a Net which has already been routed.
 * It can either be rendered as trace or as bridge. 
 */
public class TraceView extends AbstractWireView {

    private boolean isBridge = false;

    public TraceView(AbstractWire trace) {
        super(trace); // from, to);
    }


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
        return String.format("Trace[%s - %s]", this.getStart(), this.getEnd());
    }

//
//    @Override
//    public Node getXML(Document doc)  {
//        Element traceNode = doc.createElement("trace");
////        traceNode.setAttribute("from", Integer.toString(getFrom().id));
////        traceNode.setAttribute("to",   Integer.toString(getTo().id));
//        traceNode.setAttribute("isBridge",  Boolean.toString(isBridge));
//
//        return traceNode;
//    }
//
//
//    public void setAsBridge() {
//        isBridge = true;
//
//        setStroke(Color.GREEN);
//        setStrokeWidth(0.5);
//    }
}
