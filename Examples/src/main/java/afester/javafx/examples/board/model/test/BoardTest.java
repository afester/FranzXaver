
package afester.javafx.examples.board.model.test;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import afester.javafx.examples.board.model.Board;
import afester.javafx.examples.board.model.EagleNetImport;
import afester.javafx.examples.board.model.EagleNetImportNew;
import afester.javafx.examples.board.model.NetImport;
import afester.javafx.examples.board.model.Part;

public class BoardTest {

    @Test
    public void test() {
        File boardFile = new File("large.brd");
        Board board = new Board();
        board.load(boardFile);

        assertEquals(69, board.getParts().size());
        assertEquals(67, board.getNets().size());
    }
    
    
    @Test
    public void importTest() {
        File schematicFile = new File("small.sch");
        NetImport ni = new EagleNetImportNew(schematicFile);
        Board board = new Board();
        ni.importFile(board);

        assertEquals(7, board.getParts().size());
        assertEquals(4, board.getNets().size());

        Part r2 = board.getPart("R2");
        assertEquals(r2.getName(), "R2");

        assertEquals(93.98, r2.getPosition().getX(), 0.5);
        assertEquals(35.56,  r2.getPosition().getY(), 0.5);
    }


    @Test
    public void saveTest() {
        File schematicFile = new File("small.sch");
        NetImport ni = new EagleNetImport(schematicFile);
        Board board = new Board();
        ni.importFile(board);

        assertEquals(7, board.getParts().size());
        assertEquals(4, board.getNets().size());

        File boardFile = new File("smallTest.brd");
        board.saveAs(boardFile);

        Board loadedBoard = new Board();
        loadedBoard.load(boardFile);
        assertEquals(7, loadedBoard.getParts().size());
        assertEquals(4, loadedBoard.getNets().size());
    }
}
