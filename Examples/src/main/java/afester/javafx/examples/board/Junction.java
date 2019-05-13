package afester.javafx.examples.board;

import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.geometry.Point2D;

public class Junction extends AbstractNode {


    public Junction(Point2D pos) {
        super(pos);
        setFill(null);
    }
    


    @Override
    public org.w3c.dom.Node getXML(Document doc) {
        Element result = doc.createElement("junction");
        result.setAttribute("x", Double.toString(getCenterX()));
        result.setAttribute("y", Double.toString(getCenterY()));
        result.setAttribute("id", Integer.toString(id));

        return result;
    }

    

    @Override
    public String getRepr() {
        return "Junction";
    }

    @Override
    public String toString() {
        return String.format("Junction[pos=%s/%s]", getCenterX(), getCenterY());  
    }



    public List<AirWire> getAirwires() {
        List<AirWire> result = traceStarts.stream()
                                          .filter(trace -> trace instanceof AirWire)
                                          .map(trace -> (AirWire) trace)
                                          .collect(Collectors.toList());
        result.addAll(traceEnds.stream()
                               .filter(trace -> trace instanceof AirWire)
                               .map(trace -> (AirWire) trace)
                               .collect(Collectors.toList()));
        return result;
    }

}
