package afester.javafx.examples.board;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.Group;

/**
 * A net is a collection of (Pads and) Junctions which are connected through Traces.
 * (The pads are currenty not required - they are implicitly defined through the traces!)   
 */
public class Net extends Group {

    // Model
    private String netName;
    private List<Junction> junctionList = new ArrayList<>();    // A list of junctions - not associated to a part. Junctions can be added and removed.
    private List<AbstractWire> traces = new ArrayList<>();

    // View (TODO: separate in own class)
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

    public Set<AbstractNode> getAllJunctions() {
        Set<AbstractNode> result = new HashSet<>();

        traces.forEach(t -> {
            result.add(t.getFrom());
            result.add(t.getTo());
        });

        return result;
    }

    public List<AbstractWire> getTraces() {
        return traces;
    }

    public void addTrace(AbstractWire trace) {
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


    public Set<Pad> getPads() {
        final Set<Pad> result = new HashSet<>();
        for (AbstractWire t : traces) {
            AbstractNode j1 = t.getFrom();
            if (j1 instanceof Pad) {
                result.add((Pad) j1);
            }

            AbstractNode j2 = t.getTo();
            if (j2 instanceof Pad) {
                result.add((Pad) j2);
            }
        }
        return result;
    }
    
    public boolean sameAs(Net n2) {
        Set<String> thisPads = getPads().stream()
                                        .map(e -> e.getPadId())
                                        .collect(Collectors.toSet());

        Set<String> otherPads = n2.getPads().stream()
                                            .map(e -> e.getPadId())
                                            .collect(Collectors.toSet());

        return thisPads.equals(otherPads);
    }


    public void clear() {
        junctionList.clear();
        traces.clear();

        // View (TODO: separate into own class)
        junctions.getChildren().clear();
        nets.getChildren().clear();
    }

    
    /**
     * Calculate the shortest path using the nearest point heuristic. 
     *
     * @param pads The input list of Pads.
     * @return The list of Pads in the order of their nearest point.
     */
    private List<Pad> calculateNearestPath(List<Pad> pads) {
        List<Pad> result = new ArrayList<>(pads);

        for (int idx2 = 0;  idx2 < pads.size() - 1;  idx2++) {
            Pad P0 = result.get(idx2);

            double minDist = Double.MAX_VALUE;
            int nearestIdx = -1;
            Pad nearest = null;
            for (int idx = idx2+1;  idx < result.size();  idx++) {
                Pad p = result.get(idx);
                double dist = P0.getPos().distance(p.getPos());
                if (dist < minDist) {
                    nearestIdx = idx;
                    minDist = dist;
                    nearest = p;
                }
            }

            // swap the next point with the nearest one.
            Pad tmp = result.get(idx2+1);
            result.set(idx2+1, nearest);
            result.set(nearestIdx, tmp);
        }

        return result;
    }


    /**
     * Calculates the shortest path of this net by
     *
     * <ul>
     *   <li>clearing the net</li>
     *   <li>taking the first pad in the net</li>
     *   <li>calculating the next pad by its minimum distance</li>
     * </ul>
     */
    public void calculateShortestPath() {
        if (getJunctions().size() != 0) {
            System.err.printf("Can not calculate shortest paths - RESET the net first\n");
            return;
        }
    
        List<Pad> pads = getPads().stream().collect(Collectors.toList());
        List<Pad> sortedPads = calculateNearestPath(pads);
        clear();
    
        // Connect all pads through an AirWire (TODO: duplicate code in EagleNetImport)
        // TODO: Use a lineIterator
        Pad p1 = null;
        for (Pad p2 : sortedPads) {
            if (p1 != null) {
                addTrace(new AirWire(p1, p2));
            }
            p1 = p2;
        }
    }
}
