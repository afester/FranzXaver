package afester.javafx.svg.test;

import static org.junit.Assert.*;

import java.io.InputStream;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import org.junit.Test;

import afester.javafx.svg.SVGLoader;

public class BasicTests {

    @Test
    public void testRedLine() {
        // Note: Inkscape cannot create svg:line objects, a line is created as path in any case.
        // The redline.svg file has been created manually.
        InputStream svgFile = getClass().getResourceAsStream("redline.svg");
        SVGLoader loader = new SVGLoader();

        Group svgImage = loader.loadSvg(svgFile);
        assertEquals(1, svgImage.getChildrenUnmodifiable().size());

        Node n = svgImage.getChildrenUnmodifiable().get(0);
        assertTrue(n instanceof Line);
        
        Line l = (Line) n;
        assertEquals(10.0, l.getStartX(), 0.01);
        assertEquals(10.0, l.getStartY(), 0.01);
        assertEquals(110.0, l.getEndX(), 0.01);
        assertEquals(110.0, l.getEndY(), 0.01);
        Color c = (Color) l.getStroke();
        assertEquals("0xff0000ff", c.toString());
    }


    @Test
    public void testBlueRectangle() {
        InputStream svgFile = getClass().getResourceAsStream("bluerect.svg");
        SVGLoader loader = new SVGLoader();

        Group svgImage = loader.loadSvg(svgFile);
        assertEquals(1, svgImage.getChildrenUnmodifiable().size());

        Node n = svgImage.getChildrenUnmodifiable().get(0);
        assertTrue(n instanceof Group);

        Group g = (Group) n;
        Node n2 = g.getChildrenUnmodifiable().get(0);
        assertTrue(n2 instanceof Rectangle);

        Rectangle r = (Rectangle) n2;
        assertEquals(10.0, r.getX(), 0.01);
        assertEquals(12.36, r.getY(), 0.01);
        assertEquals(200.0, r.getWidth(), 0.01);
        assertEquals(100.0, r.getHeight(), 0.01);
        
        Color stroke = (Color) r.getStroke();
        assertNull(stroke);

        Color fill = (Color) r.getFill();
        assertEquals("0x0000ffff", fill.toString());
    }

    @Test
    public void testText() {
        InputStream svgFile = getClass().getResourceAsStream("simpletext.svg");
        SVGLoader loader = new SVGLoader();

        Group svgImage = loader.loadSvg(svgFile);
        assertEquals(1, svgImage.getChildrenUnmodifiable().size());
        
        Node n = svgImage.getChildrenUnmodifiable().get(0);
        assertTrue(n instanceof Group);

        Group g = (Group) n;
        Node n2 = g.getChildrenUnmodifiable().get(0);
        assertTrue(n2 instanceof Text);

        Text t = (Text) n2;
        assertEquals("Hello World", t.getText());
        assertEquals(10.0, t.getX(), 0.01);
        assertEquals(12.36, t.getY(), 0.01);
    }
}
