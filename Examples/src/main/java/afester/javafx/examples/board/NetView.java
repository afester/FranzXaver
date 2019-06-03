package afester.javafx.examples.board;

import java.util.ArrayList;
import java.util.List;

import afester.javafx.examples.board.model.Net;

/**
 * A logical grouping of traces which belong together.
 * The individual traces must be added to a group directly below the board,
 * to keep the Z order proper.
 */
public class NetView {

    private List<TraceView> traces = new ArrayList<>();
    private List<JunctionView> junctions = new ArrayList<>();
    
    public NetView(Net net) {
        
        net.getTraces().forEach(trace -> traces.add(new TraceView(trace)));
        net.getJunctions().forEach(junction -> junctions.add(new JunctionView(junction)));
    }
    
    public List<TraceView> getTraceViews() {
        return traces;
    }

    public  List<JunctionView> getJunctionViews() {
        return junctions;
    }
}
