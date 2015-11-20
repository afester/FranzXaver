package afester.javafx.svg.test;

import static org.junit.Assert.*;

import java.io.InputStream;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
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
    public void testCircle() {
        InputStream svgFile = getClass().getResourceAsStream("circle.svg");
        SVGLoader loader = new SVGLoader();

        Group svgImage = loader.loadSvg(svgFile);
        assertEquals(1, svgImage.getChildrenUnmodifiable().size());
        
        Node n = svgImage.getChildrenUnmodifiable().get(0);
        assertTrue(n instanceof Group);

        Group g = (Group) n;
        Node n2 = g.getChildrenUnmodifiable().get(0);
        assertTrue(n2 instanceof Circle);

        Circle c = (Circle) n2;
        assertEquals(86.42, c.getCenterX(), 0.01);
        assertEquals(84.5, c.getCenterY(), 0.01);
        assertEquals(70.71, c.getRadius(), 0.01);
        assertEquals("0x37ff00ff", c.getStroke().toString());
        assertEquals(StrokeType.CENTERED, c.getStrokeType());
        assertEquals(3.0, c.getStrokeWidth(), 0.1);
        assertEquals(2, c.getStrokeDashArray().size());
    }

    
    @Test
    public void testEllipse() {
        InputStream svgFile = getClass().getResourceAsStream("ellipse.svg");
        SVGLoader loader = new SVGLoader();

        Group svgImage = loader.loadSvg(svgFile);
        assertEquals(1, svgImage.getChildrenUnmodifiable().size());
        
        Node n = svgImage.getChildrenUnmodifiable().get(0);
        assertTrue(n instanceof Group);

        Group g = (Group) n;
        Node n2 = g.getChildrenUnmodifiable().get(0);
        assertTrue(n2 instanceof Ellipse);

        Ellipse e = (Ellipse) n2;
        assertEquals(164.21, e.getCenterX(), 0.01);
        assertEquals(81.71, e.getCenterY(), 0.01);
        assertEquals(147.79, e.getRadiusX(), 0.01);
        assertEquals(72.79, e.getRadiusY(), 0.01);
        assertEquals("0x37ff00ff", e.getStroke().toString());
        assertEquals(StrokeType.CENTERED, e.getStrokeType());
        assertEquals(4.4, e.getStrokeWidth(), 0.1);
        assertEquals(2, e.getStrokeDashArray().size());
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
        assertEquals(11.07, t.getX(), 0.01);
        assertEquals(51.79, t.getY(), 0.01);
        assertEquals("0x0000ffff", t.getStroke().toString());
        assertEquals("0xffff00ff", t.getFill().toString());
        assertEquals(40.0, t.getFont().getSize(), 0.1);
        assertEquals("SansSerif Regular", t.getFont().getName());
        assertEquals("SansSerif", t.getFont().getFamily());
        assertEquals("Regular", t.getFont().getStyle());
    }
}
