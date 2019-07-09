package afester.javafx.examples.board.model;

import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.geometry.Point2D;

public class Junction extends AbstractNode {


    public Junction(Net net, Point2D pos) {
        super(net, pos);
    }
    

//    @Override
    public org.w3c.dom.Node getXML(Document doc) {
        Element result = doc.createElement("junction");
        result.setAttribute("x", Double.toString(getPosition().getX()));
        result.setAttribute("y", Double.toString(getPosition().getY()));
        result.setAttribute("id", Integer.toString(id));

        return result;
    }

    @Override
    public String toString() {
        return String.format("Junction[pos=%s]", getPosition());  
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


    final private static double MIN_DIST = 0.1;

    /** 
     * @param other The other junction to validate
     * @return <code>true</code> if this junction is at the same position as 
     *         another one, <code>false</code> otherwise.
     */
    public boolean samePositionAs(Junction j2) {
        if (getPosition().distance(j2.getPosition()) < MIN_DIST) {
            return true;
        }
        return false;
    }

}
