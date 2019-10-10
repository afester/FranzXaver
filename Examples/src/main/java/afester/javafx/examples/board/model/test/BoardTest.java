
package afester.javafx.examples.board.model.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

import afester.javafx.examples.board.eagle.EagleImport;
import afester.javafx.examples.board.model.Board;
import afester.javafx.examples.board.model.BoardLoader;
import afester.javafx.examples.board.model.NetImport;
import afester.javafx.examples.board.model.Part;
import afester.javafx.examples.board.model.PartShape;

public class BoardTest {

    @Test
    public void test() {
        BoardLoader bl = new BoardLoader(new File("large.brd"));
        Board board = bl.load();

        assertEquals(69, board.getParts().size());
        assertEquals(67, board.getNets().size());
    }
    
    
    @Test
    public void importTest() {
        File schematicFile = new File("small.sch");
        NetImport ni = new EagleImport(schematicFile);
        Board board = new Board();
        ni.importFile(board);

        assertEquals(7, board.getParts().size());
        assertEquals(4, board.getNets().size());

        Part r2 = board.getPart("R2");
        assertEquals("R2", r2.getName());
        assertEquals("10k", r2.getValue());

        assertEquals(93.98, r2.getPosition().getX(), 0.5);
        assertEquals(35.56,  r2.getPosition().getY(), 0.5);
    }


    @Test
    public void saveTest() {
        File schematicFile = new File("small.sch");
        NetImport ni = new EagleImport(schematicFile);
        Board board = new Board();
        ni.importFile(board);

        assertEquals(7, board.getParts().size());
        assertEquals(4, board.getNets().size());

        File boardFile = new File("smallTest.brd");
        board.saveAs(boardFile);

        BoardLoader bl = new BoardLoader(boardFile);
        Board loadedBoard = bl.load();
        assertEquals(7, loadedBoard.getParts().size());
        assertEquals(4, loadedBoard.getNets().size());
    }

    
    @Test
    public void loadTest() {
        BoardLoader bl = new BoardLoader(new File("loadTest.brd"));
        Board loadedBoard = bl.load();
        assertEquals(7, loadedBoard.getParts().size());
        assertEquals(4, loadedBoard.getNets().size());

        Part part = loadedBoard.getPart("D1");
        assertEquals("D1", part.getName());
        assertEquals("1N4148", part.getValue());

        List<PartShape> shapes = part.getPackage().getShapes();
        assertEquals(22,  shapes.size());
    }

}
