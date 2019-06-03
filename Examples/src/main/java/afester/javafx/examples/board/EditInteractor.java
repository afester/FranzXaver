package afester.javafx.examples.board;

import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class EditInteractor  extends MouseInteractor {

//    private JunctionView junctionToMove;
    private AirWireHandle handleToMove;
    private PartView partToMove;

    public EditInteractor(BoardView boardView) {
        super(boardView);
    }


    @Override
    protected void clickObjectLeft(Interactable obj) {
        System.err.println("CLICK ON: " + obj);
        if (obj instanceof PartView) {
            clickedPartView(obj);
        } else if (obj instanceof TraceView) {
            clickedTraceView(obj);
        } else if (obj instanceof Handle) {
            clickedHandle(obj);
        } else if (obj instanceof JunctionView) {
            clickedJunction(obj);
        }
    }


    private void clickedJunction(Interactable obj) {
        System.err.println("Clicked junction: " + obj);
    }


    private void clickedHandle(Interactable obj) {
        // TODO Auto-generated method stub
    }


    private void clickedTraceView(Interactable obj) {
        final BoardView bv = getBoardView();
        Interactable currentSelection = bv.getSelectedObject();
    
        System.err.println("CLICK  : " + obj);
        System.err.println("CURRENT: " + currentSelection);

        if (obj != currentSelection) {
            if (currentSelection != null) {
                System.err.println("DESELECTING ...");
                currentSelection.setSelected(false);
                bv.setSelectedObject(null);
            }
        }

        bv.setSelectedObject(obj);
        obj.setSelected(true);

//        // obj can be Junction, Wire (Trace or AirWire) or something else
//        //            Junction can be part of the currently selected Trace or something else
//
//        // if something else: deselect current selection if any
//        // if Trace: select Trace
//        // if junction: do nothing - will be processed in dragObject
//        AbstractWire wire = null;
//        Junction junction = null;
//        AirWireHandle handle = null;
//        if (obj instanceof AirWireHandle) {
//            handle = (AirWireHandle) obj;
//        } else if (obj instanceof AbstractWire) {
//            wire = (AbstractWire) obj;
//        } else if (obj instanceof Junction) {
//            junction = (Junction) obj;
//            if (currentSelection instanceof Trace) {
//                Trace selectedTrace = (Trace) currentSelection;
//                if (selectedTrace.getFrom() != junction && selectedTrace.getTo() != junction) {
//                    junction = null;
//                }
//            }
//        }
//
//        // clicked no trace, no junction of selected trace, or another trace
//        if ((wire == null && junction == null && handle == null) || ( (junction == null && handle == null) && wire != currentSelection)) {
//            if (currentSelection != null) {
//                System.err.println("DESELECTING ...");
//                currentSelection.setSelected(false);
//                bv.setSelectedObject(null);
//            }
//        }
//
////        junctionToMove = null;
//        handleToMove = null;
////        if (wire != null) {            // clicked a trace (probably the same one, but this should not matter)
////            wire.setSegmentSelected(true);
////            bv.setSelectedObject(wire);
////        } else if (junction != null) {  // clicked a junction of the currently selected trace
////            System.err.println("JUNCTION:" + junction);
////            junctionToMove = junction;
////        } else if (handle != null) {
////            System.err.println("HANDLE:" + handle);
////            handleToMove = handle;
////        }
    }

    
    private void clickedPartView(Interactable obj) {
        final BoardView bv = getBoardView();
        Interactable currentSelection = bv.getSelectedObject();
        if (currentSelection != obj) {
            if (currentSelection != null) {
                currentSelection.setSelected(false);
            }

            System.err.println("Selecting " + obj);
            obj.setSelected(true);
            bv.setSelectedObject(obj);

            // TODO: Should all be accessedthrough the Interactable interface!
            partToMove = (PartView) obj;
            //junctionToMove = null;
            handleToMove = null;
        }
    }

    private Random r = new Random(System.currentTimeMillis());
    private final Color getRandomColor() {
        final double red = r.nextDouble();
        final double green = r.nextDouble();
        final double blue = r.nextDouble();
        return new Color(red, green, blue, 1.0);
    }

    @Override
    protected void dragObject(Interactable obj) {

        if (partToMove != null) {
            movePart();
        }


//        // System.err.println("MOVE: " + junctionToMove);
//        if (junctionToMove != null) {
//            // move the junction to the new position
//            Point2D snapPos = snapToGrid(getClickPos(), getBoardView(), getOffset());
////            junctionToMove.setPos(snapPos);
//
//            List<AirWire> airWires = junctionToMove.getAirwires();
//            airWires.forEach(aw -> {
//                AbstractNode otherNode = null; // aw.getOtherNode(junctionToMove);
//                if (otherNode.getEdges().size() > 1) {
//                    Net net = aw.getNet();
//    
//                    // net.dumpNet();
//
//                    // get all nodes which are reachable in the net from otherNode IF the given airwire would not exist
//                    List<AbstractNode> possibleNodes = net.getNodesWithout(otherNode, aw);
//
//                 // Filter out any nodes which are not allowed
//                    //AbstractWire t = (AbstractWire) obj;                  // TODO .... hack ...
//                    //possibleNodes.remove(t.getFrom());
//                    //possibleNodes.remove(t.getTo());
//                    possibleNodes.remove(junctionToMove);
//
//                    System.err.println("Possible nodes: " + possibleNodes);
////                    possibleNodes.forEach(node -> node.setSelected(true,  c));
//    
//                    // These are the nodes where we could POTENTIALLY move the airwire without breaking the net
//                    // from the candidate nodes, get the nearest one
//                    AbstractNode nearestNode = junctionToMove.getNearestNode(possibleNodes);
//    
//    //              System.err.println("This node            : " + junctionToMove);
//    //              System.err.println("Other node on AirWire: " + otherNode);
//    //              System.err.println("Nearest node         : " + nearestNode);
//    //                nearestNode.setSelected(true, c);
//    
//                    // Reconnect the edge from the old node to the new nearest node
////                    aw.reconnect(otherNode, nearestNode);
//                }
//            });
//        } else if (handleToMove != null) {
//            System.err.println("MOVING HANDLE: " + handleToMove);
//
//            // move the handle to the new position
//            Point2D snapPos = snapToGrid(getClickPos(), getBoardView(), getOffset());
////            handleToMove.setPos(snapPos);
//
//            AirWire aw = handleToMove.getAirWire();
//            Net net = aw.getNet();
//            AbstractNode node = handleToMove.getNode();
//
//            // get all nodes which are reachable in the net from the otherNode IF the given airwire would not exist
//            List<AbstractNode> possibleNodes = net.getNodesWithout(node, aw);
//
//            System.err.println("Possible nodes: " + possibleNodes);
////            possibleNodes.forEach(n -> n.setSelected(true,  c));
////            AbstractNode nearestNode = handleToMove.getNearestNode(possibleNodes);
//
//            System.err.println("Current node: " + node);
////            System.err.println("Nearest node: " + nearestNode);
//
//            // Reconnect the edge from the old node to the new nearest node
////            aw.reconnect(node, nearestNode);
//        }
    }

    @Override
    protected void clickObjectRight(Interactable obj) {
        if (obj instanceof PartView) {
            PartView part = (PartView) obj;
            part.rotatePart();
        }
    }


    private void movePart() {
      // PartView partView = (PartView) obj;
      // System.err.println("MOVE: " + obj);
    
      // Snap to center of part
      // (this is also what the Eagle board editor does)
      Point2D snapPos = snapToGrid(getClickPos(), getBoardView(), getOffset());
      partToMove.move(snapPos);
    }
}
