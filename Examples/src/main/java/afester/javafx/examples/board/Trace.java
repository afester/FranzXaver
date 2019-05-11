package afester.javafx.examples.board;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.scene.paint.Color;

/**
 * A Trace is a part of a Net which has already been routed.
 * It can either be rendered as trace or as bridge. 
 */
public class Trace extends AbstractWire {

    private boolean isBridge = false;

    public Trace(AbstractNode from, AbstractNode to) {
        super(from, to);
    }

    
    @Override
    protected void setSegmentSelected(boolean isSelected) {
      if (isSelected) {
        from.setSelected(true);
        to.setSelected(true);

        setStroke(Color.RED);
      } else {
        from.setSelected(false);
        to.setSelected(false);

        if (isBridge) {
            setStroke(Color.GREEN);
        } else {
            setStroke(Color.SILVER);
        }
      }
    }


    @Override
    public String toString() {
        return String.format("Trace[%s - %s]", this.getStart(), this.getEnd());
    }


    @Override
    public Node getXML(Document doc)  {
        Element traceNode = doc.createElement("trace");
        traceNode.setAttribute("from", Integer.toString(getFrom().id));
        traceNode.setAttribute("to",   Integer.toString(getTo().id));
        traceNode.setAttribute("isBridge",  Boolean.toString(isBridge));

        return traceNode;
    }


    public void setAsBridge() {
        isBridge = true;

        setStroke(Color.GREEN);
        setStrokeWidth(0.5);
    }
}
