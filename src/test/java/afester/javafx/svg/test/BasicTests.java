package afester.javafx.svg.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import afester.javafx.svg.SvgLoader;

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



public class BasicTests {

    @Test
    public void testRedLine() {
        // Note: Inkscape cannot create svg:line objects, a line is created as path in any case.
        // The redline.svg file has been created manually.
        SvgLoader loader = new SvgLoader();
        Group svgImage = loader.loadSvg("data/redline.svg");
        assertEquals(1, svgImage.getChildrenUnmodifiable().size());

        Node node = svgImage.getChildrenUnmodifiable().get(0);
        assertTrue(node instanceof Line);
        
        Line line = (Line) node;
        assertEquals(10.0, line.getStartX(), 0.01);
        assertEquals(10.0, line.getStartY(), 0.01);
        assertEquals(110.0, line.getEndX(), 0.01);
        assertEquals(110.0, line.getEndY(), 0.01);
        Color color = (Color) line.getStroke();
        assertEquals("0xff0000ff", color.toString());
    }


    @Test
    public void testBlueRectangle() {
        SvgLoader loader = new SvgLoader();
        Group svgImage = loader.loadSvg("data/bluerect.svg");
        assertEquals(1, svgImage.getChildrenUnmodifiable().size());

        Node node = svgImage.getChildrenUnmodifiable().get(0);
        assertTrue(node instanceof Group);

        Group group = (Group) node;
        Node n2 = group.getChildrenUnmodifiable().get(0);
        assertTrue(n2 instanceof Rectangle);

        Rectangle rectangle = (Rectangle) n2;
        assertEquals(10.0, rectangle.getX(), 0.01);
        assertEquals(12.36, rectangle.getY(), 0.01);
        assertEquals(200.0, rectangle.getWidth(), 0.01);
        assertEquals(100.0, rectangle.getHeight(), 0.01);
        
        Color stroke = (Color) rectangle.getStroke();
        assertNull(stroke);

        Color fill = (Color) rectangle.getFill();
        assertEquals("0x0000ffff", fill.toString());
    }

    
    @Test
    public void testCircle() {
        SvgLoader loader = new SvgLoader();
        Group svgImage = loader.loadSvg("data/circle.svg");
        assertEquals(1, svgImage.getChildrenUnmodifiable().size());
        
        Node node = svgImage.getChildrenUnmodifiable().get(0);
        assertTrue(node instanceof Group);

        Group group = (Group) node;
        Node n2 = group.getChildrenUnmodifiable().get(0);
        assertTrue(n2 instanceof Circle);

        Circle circle = (Circle) n2;
        assertEquals(86.42, circle.getCenterX(), 0.01);
        assertEquals(84.5, circle.getCenterY(), 0.01);
        assertEquals(70.71, circle.getRadius(), 0.01);
        assertEquals("0x37ff00ff", circle.getStroke().toString());
        assertEquals(StrokeType.CENTERED, circle.getStrokeType());
        assertEquals(3.0, circle.getStrokeWidth(), 0.1);
        assertEquals(2, circle.getStrokeDashArray().size());
    }

    
    @Test
    public void testEllipse() {
        SvgLoader loader = new SvgLoader();
        Group svgImage = loader.loadSvg("data/ellipse.svg");
        assertEquals(1, svgImage.getChildrenUnmodifiable().size());
        
        Node node = svgImage.getChildrenUnmodifiable().get(0);
        assertTrue(node instanceof Group);

        Group group = (Group) node;
        Node n2 = group.getChildrenUnmodifiable().get(0);
        assertTrue(n2 instanceof Ellipse);

        Ellipse ellipse = (Ellipse) n2;
        assertEquals(164.21, ellipse.getCenterX(), 0.01);
        assertEquals(81.71, ellipse.getCenterY(), 0.01);
        assertEquals(147.79, ellipse.getRadiusX(), 0.01);
        assertEquals(72.79, ellipse.getRadiusY(), 0.01);
        assertEquals("0x37ff00ff", ellipse.getStroke().toString());
        assertEquals(StrokeType.CENTERED, ellipse.getStrokeType());
        assertEquals(4.4, ellipse.getStrokeWidth(), 0.1);
        assertEquals(2, ellipse.getStrokeDashArray().size());
    }


    @Test
    public void testText() {
        SvgLoader loader = new SvgLoader();
        Group svgImage = loader.loadSvg("data/simpletext.svg");
        assertEquals(1, svgImage.getChildrenUnmodifiable().size());
        
        Node node = svgImage.getChildrenUnmodifiable().get(0);
        assertTrue(node instanceof Group);

        Group group = (Group) node;
        Node n2 = group.getChildrenUnmodifiable().get(0);
        assertTrue(n2 instanceof Text);

        Text text = (Text) n2;
        assertEquals("Hello World", text.getText());
        assertEquals(11.07, text.getX(), 0.01);
        assertEquals(51.79, text.getY(), 0.01);
        assertEquals("0x0000ffff", text.getStroke().toString());
        assertEquals("0xffff00ff", text.getFill().toString());
        assertEquals(40.0, text.getFont().getSize(), 0.1);
        assertEquals("SansSerif Regular", text.getFont().getName());
        assertEquals("SansSerif", text.getFont().getFamily());
        assertEquals("Regular", text.getFont().getStyle());
    }
}
