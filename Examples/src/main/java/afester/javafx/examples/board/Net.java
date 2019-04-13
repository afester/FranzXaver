package afester.javafx.examples.board;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.Group;

/**
 * A net is a collection of (Pads and) Junctions which are connected through Traces.
 * (The pads are currenty not required - they are implicitly defined through the traces!)   
 */
public class Net extends Group {

    private String netName;
    private List<Junction> junctionList = new ArrayList<>();    // A list of junctions - not associated to a part. Junctions can be added and removed.
    private List<Trace> traces = new ArrayList<>();

    private Group junctions = new Group();
    private Group nets= new Group();

    public Net(String netName) {
        this.netName = netName;
        getChildren().addAll(nets, junctions);
    }


    /**
     * @return The name of the net.
     */
    public String getName() {
        return netName;
    }

    // The Junctions need to be trackes separately because they are not associated to a part
    // (on the other hand they ARE accessible through the Traces ...)

    public void addJunction(Junction newJunction) {
        junctionList.add(newJunction);
        junctions.getChildren().add(newJunction);
    }

    public List<Junction> getJunctions() {
        return junctionList;
    }

    public Set<Junction> getAllJunctions() {
        Set<Junction> result = new HashSet<>();

        traces.forEach(t -> {
            result.add(t.getFrom());
            result.add(t.getTo());
        });

        return result;
    }

    public List<Trace> getTraces() {
        return traces;
    }

    public void addTrace(Trace trace) {
        traces.add(trace);
        nets.getChildren().add(trace);
    }


    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("Junctions[");
        getAllJunctions().forEach(e -> { buffer.append(e); buffer.append(", "); } );
        buffer.append("]");
        return String.format("Net[netName=%s, %s]", netName, buffer);
    }

}
