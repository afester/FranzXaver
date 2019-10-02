package afester.javafx.examples.board.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import afester.javafx.examples.board.BoardView;
import afester.javafx.examples.board.PartView;
import afester.javafx.examples.board.model.Board;
import afester.javafx.examples.board.model.Part;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.transform.Rotate;

public class BoardEditTest {

    private Board board;
    private BoardView boardView;

    @Before
    public void loadData() {
        board = new Board();
        board.load(new File("small.brd"));
        boardView = new BoardView(board);
    }

    @Test
    public void testMovePart() {
        // get a reference to a part view and its part model
        final Group partsGroup = (Group) boardView.lookup("#partsGroup");
        PartView r2PartView = (PartView) partsGroup.getChildrenUnmodifiable().stream().filter(e -> ((PartView) e).getName().equals("R2")).findFirst().get();
        Part r2part = board.getPart("R2");
        assertSame(r2part, r2PartView.getPart());

        // move the part to a new position
        final Point2D newLocation = new Point2D(142, 66);
        r2PartView.getPart().setPosition(newLocation);

        // check that model and view are in sync
        final Point2D viewPos = new Point2D(r2PartView.getLayoutX(), r2PartView.getLayoutY());
        assertEquals(newLocation, r2part.getPosition());
        assertEquals(newLocation, viewPos);
    }


    @Test
    public void testRotatePart() {
        // get a reference to a part view and its part model
        final Group partsGroup = (Group) boardView.lookup("#partsGroup");
        PartView r2PartView = (PartView) partsGroup.getChildrenUnmodifiable()
                .stream()
                .filter(e -> ((PartView) e).getName().equals("R2"))
                .findFirst()
                .get();
        Part r2part = board.getPart("R2");
        assertSame(r2part, r2PartView.getPart());

        // rotate the part
        final double oldRot = ((Rotate) r2PartView.getTransforms().get(0)).getAngle();  // current rotation in view
        final double rot = r2part.getRotation();                                        // current rotation in model
        r2part.rotateClockwise();

        // check that rotation has been updated in model
        assertEquals(rot + 90, r2part.getRotation(), 1.0);

        // check that model and view are in sync
        final double newRot = ((Rotate) r2PartView.getTransforms().get(0)).getAngle();
        assertEquals(oldRot + 90, newRot, 1.0);
    }
}
