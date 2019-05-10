package afester.javafx.examples.board;

import javafx.geometry.Point2D;

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
            
        } else if (junction != null) {  // clicked a junction of the currently selected trace
            System.err.println("JUNCTION:" + junction);
            junctionToMove = junction;
        }

    }

    @Override
    protected void dragObject(Interactable obj) {
        // System.err.println("MOVE: " + junctionToMove);
        if (junctionToMove != null) {
            Point2D snapPos = snapToGrid(getPos(), getBoardView(), getOffset());
            junctionToMove.setPos(snapPos);
        }
    }
}
