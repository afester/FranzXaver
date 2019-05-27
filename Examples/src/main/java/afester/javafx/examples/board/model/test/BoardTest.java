
package afester.javafx.examples.board.model.test;

import static junit.framework.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import afester.javafx.examples.board.model.Board;

public class BoardTest {

    @Test
    public void test() {
        Board board = new Board();
        board.load(new File("large.brd"));
        assertEquals(69, board.getParts().size());
        assertEquals(67, board.getNets().size());
    }
}
