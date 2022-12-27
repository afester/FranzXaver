package afester.javafx.examples.board.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import afester.javafx.examples.board.ApplicationProperties;
import afester.javafx.examples.board.model.Board;
import afester.javafx.examples.board.model.Junction;
import afester.javafx.examples.board.model.Net;
import afester.javafx.examples.board.model.Part;
import afester.javafx.examples.board.model.ShapePad;
import afester.javafx.examples.board.model.Trace;
import afester.javafx.examples.board.model.TraceType;
import afester.javafx.examples.board.model.Package;

import afester.javafx.examples.board.view.BoardView;
import afester.javafx.examples.board.view.PartView;
import afester.javafx.examples.board.view.TopBoardView;
import javafx.geometry.Point2D;
import javafx.scene.Group;

public class BoardViewTest {
    @Rule
    public JavaFXThreadingRule jfxRule = new JavaFXThreadingRule();

    private Board board;
    private BoardView boardView;

    @Before
    public void loadData() {
        board = new Board();
        boardView = new TopBoardView(board, ApplicationProperties.load());
    }


    private Part createPart(String name, String value) {
        final Package pkg = new Package("pkgDil8", "DIP-8");
        pkg.addPad(new ShapePad("1", new Point2D(0, 0)));
        pkg.addPad(new ShapePad("2", new Point2D(2.54, 0)));
        pkg.addPad(new ShapePad("3", new Point2D(5.08, 0)));
        final Part part = new Part(name, value, pkg);
        return part;
    }

    
    private Part createPart(String name, String value, double x, double y) {
        final var part = createPart(name, value);
        part.setPosition(new Point2D(x, y));
        return part;
    }

    @Test
    public void testBoard() {
        final Group partsGroup = (Group) boardView.lookup("#partsGroup");
        assertEquals(0, partsGroup.getChildren().size());
    }


    @Test
    public void testAddPart() {
        final var part = createPart("P1", "100k");
        assertEquals(3, part.getPins().size());

        board.addPart(part);

        final Group partsGroup = (Group) boardView.lookup("#partsGroup");
        assertEquals(1, partsGroup.getChildren().size());
    }


    @Test
    public void testAddTrace() {
        final var part1 = createPart("P1", "100k", 10, 10);
        final var part2 = createPart("P2", "10k", 20, 20);

        board.addPart(part1);
        board.addPart(part2);

        final Group partsGroup = (Group) boardView.lookup("#partsGroup");
        assertEquals(2, partsGroup.getChildren().size());

        final var net = new Net("N$1");
        board.addNet(net);

        final var from = part1.getPin("1");
        final var to = part2.getPin("2");
        final var trace = new Trace(TraceType.TRACE);
        net.addTrace(trace, from, to);

        final Group traceGroup = (Group) boardView.lookup("#traceGroup");
        assertEquals(1, traceGroup.getChildren().size());
    }

    
    @Test
    public void testAddJunction() {
        final var part1 = createPart("P1", "100k", 10, 10);
        final var part2 = createPart("P2", "10k", 20, 20);

        board.addPart(part1);
        board.addPart(part2);

        final var net = new Net("N$1");
        board.addNet(net);

        final var junction = new Junction(new Point2D(15, 15));
        net.addJunction(junction);

        final var from = part1.getPin("1");
        final var to = part2.getPin("2");

        final var trace1 = new Trace(TraceType.TRACE);
        final var trace2 = new Trace(TraceType.TRACE);
        net.addTrace(trace1, from, junction);
        net.addTrace(trace2, junction, to);

        final Group traceGroup = (Group) boardView.lookup("#traceGroup");
        assertEquals(2, traceGroup.getChildren().size());
    }


    @Test
    public void testRemoveTraceAndFrom() {
        final var part1 = createPart("P1", "100k", 10, 10);
        final var part2 = createPart("P2", "10k", 20, 20);

        board.addPart(part1);
        board.addPart(part2);

        final var net = new Net("N$1");
        board.addNet(net);

        final var junction = new Junction(new Point2D(15, 15));
        net.addJunction(junction);

        final var from = part1.getPin("1");
        final var to = part2.getPin("2");

        final var trace1 = new Trace(TraceType.TRACE);
        final var trace2 = new Trace(TraceType.TRACE);
        net.addTrace(trace1, from, junction);
        net.addTrace(trace2, junction, to);

        final Group traceGroup = (Group) boardView.lookup("#traceGroup");

        assertEquals(2, traceGroup.getChildren().size());
        assertEquals(1, net.getJunctions().size());

        net.removeTraceAndFrom(trace2);

        assertEquals(1, traceGroup.getChildren().size());
        assertEquals(0, net.getJunctions().size());

        assertSame(from, trace1.getFrom());
        assertSame(to,   trace1.getTo());
    }

    @Test
    public void testRemoveTraceAndTo() {
        final var part1 = createPart("P1", "100k", 10, 10);
        final var part2 = createPart("P2", "10k", 20, 20);

        board.addPart(part1);
        board.addPart(part2);

        final var net = new Net("N$1");
        board.addNet(net);

        final var junction = new Junction(new Point2D(15, 15));
        net.addJunction(junction);

        final var from = part1.getPin("1");
        final var to = part2.getPin("2");

        final var trace1 = new Trace(TraceType.TRACE);
        final var trace2 = new Trace(TraceType.TRACE);
        net.addTrace(trace1, from, junction);
        net.addTrace(trace2, junction, to);

        final Group traceGroup = (Group) boardView.lookup("#traceGroup");

        assertEquals(2, traceGroup.getChildren().size());
        assertEquals(1, net.getJunctions().size());

        net.removeTraceAndTo(trace1);

        assertEquals(1, traceGroup.getChildren().size());
        assertEquals(0, net.getJunctions().size());

        assertSame(from, trace2.getFrom());
        assertSame(to,   trace2.getTo());
    }
    

    @Test
    public void testChangeTraceType() {
        final var part1 = createPart("P1", "100k", 10, 10);
        final var part2 = createPart("P2", "10k", 20, 20);

        board.addPart(part1);
        board.addPart(part2);

        final var net = new Net("N$1");
        board.addNet(net);

        final var junction = new Junction(new Point2D(15, 15));
        net.addJunction(junction);

        final var from = part1.getPin("1");
        final var to = part2.getPin("2");

        final var trace1 = new Trace(TraceType.TRACE);
        final var trace2 = new Trace(TraceType.TRACE);
        net.addTrace(trace1, from, junction);
        net.addTrace(trace2, junction, to);

        final Group traceGroup = (Group) boardView.lookup("#traceGroup");
        final Group bridgeGroup = (Group) boardView.lookup("#bridgeGroup");
        final Group airwireGroup = (Group) boardView.lookup("#airWireGroup");
        assertEquals(2, traceGroup.getChildren().size());
        assertEquals(0, bridgeGroup.getChildren().size());
        assertEquals(0, airwireGroup.getChildren().size());

        trace1.setTraceType(TraceType.AIRWIRE);
        trace2.setTraceType(TraceType.BRIDGE);

        assertEquals(0, traceGroup.getChildren().size());
        assertEquals(1, bridgeGroup.getChildren().size());
        assertEquals(1, airwireGroup.getChildren().size());
    }
    

    @Test
    public void testSplitTrace() {
        final var part1 = createPart("P1", "100k", 10, 10);
        final var part2 = createPart("P2", "10k", 20, 20);

        board.addPart(part1);
        board.addPart(part2);

        final var net = new Net("N$1");
        board.addNet(net);

        final var from = part1.getPin("1");
        final var to = part2.getPin("2");

        final var trace = new Trace(TraceType.TRACE);
        net.addTrace(trace, from, to);

        assertEquals(0, net.getJunctions().size());

        final Group traceGroup = (Group) boardView.lookup("#traceGroup");
        assertEquals(1, traceGroup.getChildren().size());

        trace.splitTrace(new Point2D(15, 15));
        assertEquals(2, traceGroup.getChildren().size());
        assertEquals(1, net.getJunctions().size());
    }


    @Test
    public void testReplacePart() {
        final var part1 = createPart("P1", "100k", 10, 10);
        final var part2 = createPart("P2", "10k", 20, 20);

        board.addPart(part1);
        board.addPart(part2);

        final var net = new Net("N$1");
        board.addNet(net);

        final var from = part1.getPin("1");
        final var to = part2.getPin("2");

        final var trace = new Trace(TraceType.TRACE);
        net.addTrace(trace, from, to);

        // Check view
        final Group traceGroup = (Group) boardView.lookup("#traceGroup");
        final Group partGroup = (Group) boardView.lookup("#partsGroup");
        
        assertEquals(0, net.getJunctions().size());
        assertEquals(1, traceGroup.getChildren().size());
        assertEquals(2, partGroup.getChildren().size());
        
        final var partNew = createPart("P2", "20k", 20, 20);

        board.replacePart(s -> System.err.println(s), part2, partNew);
        
        assertEquals(0, net.getJunctions().size());
        assertEquals(1, traceGroup.getChildren().size());
        assertEquals(2, partGroup.getChildren().size());
        
        final var p1 = ((PartView) partGroup.getChildren().get(0)).getPart();
        final var p2 = ((PartView) partGroup.getChildren().get(1)).getPart();

        assertEquals("100k", p1.getValue());
        assertEquals("20k", p2.getValue());
    }
}