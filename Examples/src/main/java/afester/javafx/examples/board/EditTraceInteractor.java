package afester.javafx.examples.board;

import java.util.List;
import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class EditTraceInteractor  extends MouseInteractor {

    private Junction junctionToMove;

    public EditTraceInteractor(BoardView boardView) {
        super(boardView);
    }
    

    @Override
    protected void clickObjectLeft(Interactable obj) {
        final BoardView bv = getBoardView();

        System.err.println("CLICK: " + obj);
        Interactable currentSelection = bv.getSelectedObject();

        // obj can be Junction, Trace or something else
        //            Junction can be part of the currently selected Trace or something else

        // if something else: deselect current selection if any
        // if Trace: select Trace
        // if junction: do nothing - will be processed in dragObject
        Trace trace = null;
        Junction junction = null;
        if (obj instanceof Trace) {
            trace = (Trace) obj;
        } else if (obj instanceof Junction) {
            junction = (Junction) obj;
            if (currentSelection instanceof Trace) {
                Trace selectedTrace = (Trace) currentSelection;
                if (selectedTrace.getFrom() != junction && selectedTrace.getTo() != junction) {
                    junction = null;
                }
            }
        }

        // clicked no trace, no junction of selected trace, or another trace
        if ((trace == null && junction == null) || (junction == null && trace != currentSelection)) {
            if (currentSelection != null) {
                currentSelection.setSelected(false);
                bv.setSelectedObject(null);
            }
        }

        if (trace != null) {            // clicked a trace (probably the same one, but this should not matter)
            trace.setSegmentSelected(true);
            bv.setSelectedObject(trace);
            junctionToMove = null;
        } else if (junction != null) {  // clicked a junction of the currently selected trace
            System.err.println("JUNCTION:" + junction);
            junctionToMove = junction;
        }

    }

    
    private Random r = new Random(System.currentTimeMillis());

    @Override
    protected void dragObject(Interactable obj) {

        final double red = r.nextDouble();
        final double green = r.nextDouble();
        final double blue = r.nextDouble();
        final Color c = new Color(red, green, blue, 1.0);

        // System.err.println("MOVE: " + junctionToMove);
        if (junctionToMove != null) {
            // move the junction to the new position
            Point2D snapPos = snapToGrid(getClickPos(), getBoardView(), getOffset());
            junctionToMove.setPos(snapPos);

            List<AirWire> airWires = junctionToMove.getAirwires();
            airWires.forEach(aw -> {
                AbstractNode otherNode = aw.getOtherNode(junctionToMove);
                if (otherNode.getEdges().size() > 1) {
                    Net net = aw.getNet();
    
                    net.dumpNet();

                    // get all nodes which are reachable in the net from otherNode IF the given airwire would not exist
                    List<AbstractNode> possibleNodes = net.getNodesWithout(otherNode, aw);
                    Trace t = (Trace) obj;                  // TODO .... hack ...
                    possibleNodes.remove(t.getFrom());
                    possibleNodes.remove(t.getTo());
    
    //                System.err.println("Possible nodes: " + possibleNodes);
                    possibleNodes.forEach(node -> node.setSelected(true,  c));
    
                    // These are the nodes where we could POTENTIALLY move the airwire without breaking the net
                    // from the candidate nodes, get the nearest one
                    AbstractNode nearestNode = junctionToMove.getNearestNode(possibleNodes);
    
    //              System.err.println("This node            : " + junctionToMove);
    //              System.err.println("Other node on AirWire: " + otherNode);
    //              System.err.println("Nearest node         : " + nearestNode);
    //                nearestNode.setSelected(true, c);
    
                    // Reconnect the edge from the old node to the new nearest node
                    aw.reconnect(otherNode, nearestNode);
                }
            });
        }
    }
}
