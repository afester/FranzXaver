package afester.javafx.examples.board;

import java.util.List;

import afester.javafx.examples.board.model.AbstractWire;
import afester.javafx.examples.board.view.BoardView;
import afester.javafx.examples.board.view.TraceView;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

public class SplitTraceInteractor implements Interactor {
    private BoardView bv;       // The BoardView to which this interactor is attached

    public SplitTraceInteractor(BoardView boardView) {
        bv = boardView;
    }

    
    @Override
    public void mousePressed(MouseEvent e) {
        final Point2D mpos = new Point2D(e.getSceneX(), e.getSceneY());
        List<Interactable> edges = bv.getNetsGroup().pickAll(mpos);
        if (!edges.isEmpty()) {
            Interactable edge = edges.get(0);
            if (edge instanceof TraceView) {
                TraceView trace = (TraceView) edge;

                Point2D newPos = bv.sceneToLocal(e.getSceneX(), e.getSceneY());
                newPos = bv.snapToGrid(newPos, false);

                AbstractWire aw = trace.getTrace();
                aw.splitTrace(newPos);
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public String toString() {
        return "SplitTraceInteractor";
    }
}
