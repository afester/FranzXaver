package afester.javafx.examples.board;

import java.util.List;

import afester.javafx.examples.board.model.Junction;
import afester.javafx.examples.board.model.Part;
import afester.javafx.examples.board.model.TraceType;
import afester.javafx.examples.board.view.BoardView;
import afester.javafx.examples.board.view.JunctionView;
import afester.javafx.examples.board.view.PartView;
import afester.javafx.examples.board.view.TraceView;
import javafx.geometry.Point2D;


public class EditInteractor  extends MouseInteractor {

    private Junction junctionToMove;

    private Part partToMove;

    private AirWireHandle handleToMove;

    public EditInteractor(BoardView boardView) {
        super(boardView);
    }

    @Override
    protected List<Interactable> pickObjects(Point2D mpos) {
        return getBoardView().getPartsAndNets(mpos);
    }

    @Override
    protected void selectObject(Interactable obj) {
        if (obj instanceof PartView) {
           // clickedPartView(obj);
        } else if (obj instanceof TraceView) {
            clickedTraceView((TraceView) obj);
        } else if (obj instanceof AirWireHandle) {
            clickedHandle((AirWireHandle) obj);
        } else if (obj instanceof JunctionView) {
            clickedJunction(obj);
        }
    }


    private void clickedJunction(Interactable obj) {
        System.err.println("Clicked junction view: " + obj);
        junctionToMove = ((JunctionView) obj).getJunction();
        System.err.println("Clicked junction: " + junctionToMove);

        partToMove = null;
        handleToMove = null;
    }


    private void clickedHandle(AirWireHandle obj) {
        System.err.println("Clicked Handle: " + obj);

        junctionToMove = null;
        partToMove = null;
        handleToMove = obj;
    }


    private void clickedTraceView(TraceView obj) {
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

        partToMove = null;
        handleToMove = null;
        junctionToMove = null;

        if (obj.getType() == TraceType.AIRWIRE) {
            System.err.println("AIRWIRE!!");
            FromHandle h1 = new FromHandle(obj);
            ToHandle h2 = new ToHandle(obj);
            bv.getHandleGroup().getChildren().add(h1);
            bv.getHandleGroup().getChildren().add(h2);
        }

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

            // TODO: Should all be accessed through the Interactable interface!
            partToMove = ((PartView) obj).getPart();
            handleToMove = null;
            junctionToMove = null;
        }
    }

    @Override
    protected void moveObject(Interactable obj, Point2D newPos) {
        obj.moveToGrid(getBoardView(), newPos); 

//        if (partToMove != null) {
//            Point2D snapPos = snapToGrid(getClickPos(), getBoardView(), getOffset());
//            partToMove.setPosition(snapPos);
//        } else if (junctionToMove != null) {
//            // move the junction to the new position
//            Point2D snapPos = snapToGrid(getClickPos(), getBoardView(), getOffset());
//            junctionToMove.setPosition(snapPos);
//        } else if (handleToMove != null) {
//            handleToMove.setPosition(getClickPos());
//        }
    }

    @Override
    protected void rightClickObject(Interactable obj) {
        if (obj instanceof PartView) {
            PartView partView = (PartView) obj;
            Part part = partView.getPart();
            part.rotateClockwise();
        }
    }
    
    
    @Override
    public String toString() {
        return "EditInteractor";
    }
}
