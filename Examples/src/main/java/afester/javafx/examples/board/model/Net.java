package afester.javafx.examples.board.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import afester.javafx.examples.board.model.AbstractWire.AbstractWireState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

/**
 * A net is a collection of (Pads and) Junctions which are connected through Traces.
 * (The pads are currently not required - they are implicitly defined through the traces!)   
 */
public class Net {

    private String netName;
    private ObservableList<Junction> junctionList = FXCollections.observableArrayList();    // A list of junctions - not associated to a part. Junctions can be added and removed.
    private ObservableList<AbstractWire> traceList = FXCollections.observableArrayList();

    public Net(String netName) {
        this.netName = netName;
    }

    /**
     * @return The name of the net.
     */
    public String getName() {
        return netName;
    }

    // The Junctions need to be tracked separately because they are not associated to a part
    // (on the other hand they ARE accessible through the Traces ...)

    public void addJunction(Junction newJunction) {
        junctionList.add(newJunction);
    }

    public void removeJunction(AbstractNode junction) {
        junctionList.remove(junction);
    }

    public ObservableList<Junction> getJunctions() {
        return junctionList;
    }

    public Set<AbstractNode> getAllJunctions() {
        Set<AbstractNode> result = new HashSet<>();

        traceList.forEach(t -> {
            result.add(t.getFrom());
            result.add(t.getTo());
        });

        return result;
    }

    public ObservableList<AbstractWire> getTraces() {
        return traceList;
    }

    public void addTrace(AbstractWire trace) {
        traceList.add(trace);
    }

    /**
     * Removes a trace from this net. Also, the "from" junction is removed
     * and all traces which ended in the from junction will now end at the "to" junction.
     *
     * @param trace The trace to remove.
     */
    public void removeTraceAndFrom(AbstractWire trace) {
        AbstractNode from = trace.getFrom();
        AbstractNode to = trace.getTo();

        from.traceStarts.remove(trace);
        to.traceEnds.remove(trace);

        to.traceEnds.addAll(from.traceEnds);
        from.traceEnds.forEach(xtrace -> {
//            xtrace.setEndX(to.getCenterX());
//            xtrace.setEndY(to.getCenterY());
            xtrace.to = to;
        });
        removeJunction(from);

        traceList.remove(trace);

        // update view
//        traces.getChildren().remove(trace);
    }


    public Set<Pad> getPads() {
        final Set<Pad> result = new HashSet<>();
        for (AbstractWire t : traceList) {
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


    /**
     * Removes all junctions and all traces from this net.
     * Afterwards, the net is effectively empty.
     */
    public void clear() {
        getPads().forEach(pad -> {
            pad.traceEnds.clear();
            pad.traceStarts.clear();
        });
        junctionList.clear();
        traceList.clear();
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
    
        resetNet();
    }
    
    /**
     * Removes all traces and re-applies the "shortest path" algorithm to this net.
     * 
     */
    public void resetNet() {
        List<Pad> pads = getPads().stream().collect(Collectors.toList());
        List<Pad> sortedPads = calculateNearestPath(pads);

        clear();

        // Connect all pads through an AirWire (TODO: duplicate code in EagleNetImport)
        sortedPads.stream()
                  .reduce((p1, p2) -> {
            System.err.println("ADDING:" + p1 + "=>" + p2);
            addTrace(new AirWire(p1, p2, this));
            return p2;
        });

        // dumpNet();
    }
    


    /**
     * @param startWith The start node.
     * @param aw The edge to disregard.
     * @return A list of nodes which are reachable from a given node, without traversing the given edge.
     */
    public List<AbstractNode> getNodesWithout(final AbstractNode startWith, final AirWire ignore) {
        Set<AbstractNode> result = new HashSet<>();
        Stack<AbstractNode> nodeStack = new Stack<>();

        // dumpNet();

        // start with the given node and add all destination nodes to the result set
        AbstractNode currentNode = startWith;
        while(currentNode != null) {
            List<AbstractWire> edges = currentNode.getEdges();
            edges.remove(ignore);

            for(AbstractWire edge : edges) {
                AbstractNode dest = edge.getOtherNode(currentNode);
                if (!result.contains(dest) ) {
                    result.add(dest);
                    nodeStack.push(dest);
                }
            }
            if (!nodeStack.empty()) {
                currentNode = nodeStack.pop();
            } else {
                currentNode = null;
            }
        }

        System.err.println("RESULT: " + result);
        return result.stream().collect(Collectors.toList());
    }


    private class DuplicateJunctions {
        public DuplicateJunctions(Junction j1, Junction j2) {
            this.j1 = j1;
            this.j2 = j2;
        }
        public Junction j1;
        public Junction j2;
        
        
        @Override
        public String toString() {
            return j1 + "== " + j2;
        }
    }

    public void cleanup() {
        junctionList.forEach(junction -> System.err.println("   " + junction));
        traceList.forEach(trace -> System.err.println("   " + trace));

        // get all redundant junctions
        final List<DuplicateJunctions> duplicates = new ArrayList<>();
        for (int outer = 0;  outer < junctionList.size();  outer++) {
            for (int inner = outer + 1;  inner < junctionList.size();  inner++) {
                Junction j1 = junctionList.get(outer);

                Junction j2 = junctionList.get(inner);
                if (j1.samePositionAs(j2)) {
                    duplicates.add(new DuplicateJunctions(j1, j2));
                }
            }
        }

        // remove all redundant junctions
        duplicates.forEach(d -> {
            Junction keep = d.j1;
            Junction remove = d.j2;

            keep.traceStarts.addAll(remove.traceStarts);
//            remove.traceStarts.forEach(trace -> trace.setFrom(keep));
            remove.traceStarts.clear();

            keep.traceEnds.addAll(remove.traceEnds);
//            remove.traceEnds.forEach(trace -> trace.setTo(keep));
            remove.traceEnds.clear();

            removeJunction(remove);
        });

        // get all traces which connect to the same junction on both ends
        List<AbstractWire> selfTraces = new ArrayList<>();
        traceList.forEach(wire -> {
            if (wire.getFrom() == wire.getTo()) {
                selfTraces.add(wire);
            }
        });

        selfTraces.forEach(wire -> { 
            wire.from.traceStarts.remove(wire);
            wire.from = null;
            wire.to.traceEnds.remove(wire);
            wire.to = null;
    
            traceList.remove(wire);
    
            // update view
//            traces.getChildren().remove(wire);
        });

//        junctionList.forEach(j1 -> {
//            junctionList.forEach(j2 -> {
//               if (j1 != j2 && j1.samePositionAs(j2)) {
//                   System.err.println("   " + j1 + "<=>" + j2);
//               }
//            });
//        });
    }

    
    public void dumpNet() {
        List<AbstractNode> allNodes = getPads().stream().collect(Collectors.toList());
        allNodes.addAll(junctionList);

        allNodes.forEach(node -> {
            System.err.println("   " + node);
            node.traceStarts.forEach(trace -> System.err.println("     >> " + trace));
            node.traceEnds.forEach(trace -> System.err.println("     << " + trace));
        });
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("Junctions[");
        getAllJunctions().forEach(e -> { buffer.append(e); buffer.append(", "); } );
        buffer.append("]");
        return String.format("Net[netName=%s, %s]", netName, buffer);
    }

    
    

    /**
     * Selects the whole net, and marks the given trace as the interactable object to work on
     *
     * @param trace
     */
    public void setSelected(boolean isSelected, AbstractWire trace) {
        if (isSelected) {
            getTraces().forEach(segment -> segment.setState(AbstractWireState.HIGHLIGHTED));
            trace.setState(AbstractWireState.SELECTED);

            trace.getFrom().setColor(Color.DARKVIOLET);
            trace.getTo().setColor(Color.DARKVIOLET);
        } else {
            getTraces().forEach(segment -> segment.setState(AbstractWireState.NORMAL) );

            trace.getFrom().setColor(null);
            trace.getTo().setColor(null);
        }
    }
}
