package afester.javafx.examples.board;

import java.util.List;

import afester.javafx.examples.board.view.BoardView;
import afester.javafx.examples.board.view.TraceView;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

public class TraceInteractor implements Interactor {
    private BoardView bv;       // The BoardView to which this interactor is attached

    public TraceInteractor(BoardView boardView) {
        bv = boardView;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        final Point2D mpos = new Point2D(e.getSceneX(), e.getSceneY());
        List<Interactable> edges = bv.getNetsGroup().pickAll(mpos);
        if (!edges.isEmpty()) {
            Interactable edge = edges.get(0);
            if (edge instanceof TraceView) {
                TraceView t = (TraceView) edge;
                t.edge.convertToStraightTrace();
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
        return "TraceInteractor";
    }
}
