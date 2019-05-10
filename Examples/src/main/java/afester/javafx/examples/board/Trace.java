package afester.javafx.examples.board;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.scene.paint.Color;

/**
 * A Trace is a part of a Net which has already been routed. 
 */
public class Trace extends AbstractWire {

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
        setStroke(Color.SILVER);
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

        return traceNode;
    }
}
