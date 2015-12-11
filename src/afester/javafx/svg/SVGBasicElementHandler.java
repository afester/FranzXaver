package afester.javafx.svg;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;

import org.apache.batik.anim.dom.SVGOMAnimatedPathData;
import org.apache.batik.anim.dom.SVGOMAnimatedPathData.BaseSVGPathSegList;
import org.apache.batik.anim.dom.SVGOMCircleElement;
import org.apache.batik.anim.dom.SVGOMDefsElement;
import org.apache.batik.anim.dom.SVGOMEllipseElement;
import org.apache.batik.anim.dom.SVGOMGElement;
import org.apache.batik.anim.dom.SVGOMGradientElement;
import org.apache.batik.anim.dom.SVGOMLineElement;
import org.apache.batik.anim.dom.SVGOMLinearGradientElement;
import org.apache.batik.anim.dom.SVGOMMetadataElement;
import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.anim.dom.SVGOMPatternElement;
import org.apache.batik.anim.dom.SVGOMRadialGradientElement;
import org.apache.batik.anim.dom.SVGOMRectElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.anim.dom.SVGOMStopElement;
import org.apache.batik.anim.dom.SVGOMTSpanElement;
import org.apache.batik.anim.dom.SVGOMTextElement;
import org.apache.batik.css.dom.CSSOMSVGColor;
import org.apache.batik.css.dom.CSSOMValue;
import org.apache.batik.dom.svg.SVGPathSegItem;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;

public class SVGBasicElementHandler {

    private SVGStyleTools styleTools = null;
    private SVGLoader loader = null;

    public SVGBasicElementHandler(SVGLoader svgLoader) {
        this.loader = svgLoader;
    }


    // <svg>
    void handleElement(SVGOMSVGElement element) {
        styleTools = new SVGStyleTools(element);

        // optionally add a rectangle using the size of the whole drawing
        if (loader.addRootRect) {
            float height = element.getViewBox().getBaseVal().getHeight();
            float width = element.getViewBox().getBaseVal().getWidth();
            Rectangle result = new Rectangle(width,  height, null);
            result.setId(element.getId());
            result.setStroke(Color.BLACK);
            result.getStrokeDashArray().addAll(3.0,7.0,3.0,7.0);

            loader.parentNode.getChildren().add(result);    
        }
    }


    // <defs>
    void handleElement(SVGOMDefsElement element) {
        System.err.println("Handling <defs>: " + element);
    }


    // <g>
    void handleElement(SVGOMGElement element) {
        Group result = new Group();
        result.setId(element.getId());

        Affine transformation = styleTools.getTransform(element);
        if (transformation != null) {
            result.getTransforms().add(transformation);
        }


        loader.parentNode.getChildren().add(result);
        loader.parentNode = result;
    }


    void handleElement(SVGOMMetadataElement element) {
        System.err.println("Handling <metadata>: " + element);
    }

    
    void handleElement(SVGOMPathElement element) {
        // Get attributes from SVG node
        String path = element.getAttribute("d");

        // Create JavaFX SVGPath object
        SVGPath result = new SVGPath();
        result.setId(element.getId());
        result.setContent(path);

        Affine transformation = styleTools.getTransform(element);
        if (transformation != null) {
            result.getTransforms().add(transformation);
        }

        styleTools.applyStyle(result, element);

        //fxObj.setStroke(Color.VIOLET);
        loader.parentNode.getChildren().add(result);
    }


    /**
     * Alternative handling of an SVG path.
     * Creates distinct JavaFX Shapes for each path segment.
     * 
     * @param obj
     */
    Node handlePath2(SVGOMPathElement element) {
        
        Group g = new Group();
        g.setId(element.getId());

        double xpos = 0.0;
        double ypos = 0.0;

        SVGOMAnimatedPathData.BaseSVGPathSegList list = (BaseSVGPathSegList) element.getPathSegList();
        //System.err.printf("%s%s\n", indent(level), "PATH: " + list.getNumberOfItems());
        for (int i = 0;  i < list.getNumberOfItems();  i++) {
            SVGPathSegItem item = (SVGPathSegItem) list.getItem(i);
            Shape fxObj = null;

            switch(item.getPathSegType()) { 
                case SVGPathSegItem.PATHSEG_UNKNOWN : 
                    //System.err.printf("%s PATHSEG_UNKNOWN\n", indent(level+2));
                    break;
                
                case SVGPathSegItem.PATHSEG_CLOSEPATH : 
                    //System.err.printf("%s PATHSEG_CLOSEPATH\n", indent(level+2));
                    break;

                case SVGPathSegItem.PATHSEG_MOVETO_ABS : 
                    xpos = item.getX();
                    ypos = item.getY();
                    //System.err.printf("%s moveto(%f/%f)\n", indent(level+2), xpos, ypos);
                    break;
                
                case SVGPathSegItem.PATHSEG_MOVETO_REL :
                    xpos += item.getX();
                    ypos += item.getY();
                    //System.err.printf("%s moveto(%f/%f)\n", indent(level+2), xpos, ypos);
                    break;

                case SVGPathSegItem.PATHSEG_LINETO_ABS  : { 
                        double x2 = item.getX();
                        double y2 = item.getY();
                        
                        //System.err.printf("%s lineto(%f/%f)\n", indent(level+2), x2, y2);
                        fxObj = new Line(xpos, ypos, x2, y2);
                        
                        xpos = x2;
                        ypos = y2;
                    }
                    break;

                case SVGPathSegItem.PATHSEG_LINETO_REL  : {
                        double x2 = xpos + item.getX();
                        double y2 = ypos + item.getY();
                        
                        //System.err.printf("%s lineto(%f/%f)\n", indent(level+2), x2, y2);
                        fxObj = new Line(xpos, ypos, x2, y2);
                        
                        xpos = x2;
                        ypos = y2;
                    }
                    break;

                case SVGPathSegItem.PATHSEG_CURVETO_CUBIC_ABS  : {
                        double endX = xpos + item.getX();
                        double endY = ypos + item.getY();
                        
                        //System.err.printf("%s cubicCurve(%f/%f, %f/%f)\n", indent(level+2), xpos, ypos, endX, endY);
                        fxObj = new CubicCurve(xpos, ypos, item.getX1(), item.getY1(), item.getX2(), item.getY2(), endX, endY);

                        xpos = endX;
                        ypos = endY;
                    }
                    break;

                case SVGPathSegItem.PATHSEG_CURVETO_CUBIC_REL : { 
                        double endX = xpos + item.getX();
                        double endY = ypos + item.getY();
                        double x1 = xpos + item.getX1();
                        double y1 = ypos + item.getY1();
                        double x2 = xpos + item.getX2();
                        double y2 = ypos + item.getY2();
                        
                        //System.err.printf("%s cubicCurve(%f/%f, %f/%f)\n", indent(level+2), xpos, ypos, endX, endY);
                        fxObj = new CubicCurve(xpos, ypos, x1, y1, x2, y2, endX, endY);

                        xpos = endX;
                        ypos = endY;
                    }
                    break;

                default:
                    //System.err.printf("%s UNKNOWN\n", indent(level+2));
                    break;
            }

            if (fxObj != null) {
                
                System.err.printf("%s\n", element);
                styleTools.applyStyle(fxObj, element);

                g.getChildren().add(fxObj);
            }
        }

        return g;
    }


    
    void handleElement(SVGOMRectElement element) {
        // Get attributes from SVG node
        float xpos = element.getX().getBaseVal().getValue();
        float ypos = element.getY().getBaseVal().getValue();
        float width = element.getWidth().getBaseVal().getValue();
        float height = element.getHeight().getBaseVal().getValue();
        float rX = element.getRx().getBaseVal().getValue();
        float rY = element.getRy().getBaseVal().getValue();

        // Create JavaFX Rectangle object
        Rectangle result = new Rectangle(xpos, ypos, width, height);
        result.setId(element.getId());
        result.setArcWidth(2*rX);
        result.setArcHeight(2*rY);

        Affine transformation = styleTools.getTransform(element);
        if (transformation != null) {
            result.getTransforms().add(transformation);
        }

        styleTools.applyStyle(result, element);

        loader.parentNode.getChildren().add(result);
    }

    void handleElement(SVGOMTextElement element) {
        // Get attributes from SVG node
        String text = element.getTextContent();
        float xpos = element.getX().getBaseVal().getItem(0).getValue();
        float ypos = element.getY().getBaseVal().getItem(0).getValue();

        // Create JavaFX text object
        Text result = new Text(xpos, ypos, text);
        result.setId(element.getId());

        Affine transformation = styleTools.getTransform(element);
        if (transformation != null) {
            result.getTransforms().add(transformation);
        }

        styleTools.applyTextStyle(result, element);

        loader.parentNode.getChildren().add(result);
    }

    void handleElement(SVGOMTSpanElement element) {
        System.err.println("Handling <tspan>: " + element);
    }

    void handleElement(SVGOMPatternElement element) {
        System.err.println("Handling <pattern>: " + element);
    }


    void handleElement(SVGOMLineElement element) {
        // Get attributes from SVG node
        float x1 = element.getX1().getBaseVal().getValue();
        float y1 = element.getY1().getBaseVal().getValue();
        float x2 = element.getX2().getBaseVal().getValue();
        float y2 = element.getY2().getBaseVal().getValue();

        // Create JavaFX Line object
        Line result = new Line(x1, y1, x2, y2);
        result.setId(element.getId());

        Affine transformation = styleTools.getTransform(element);
        if (transformation != null) {
            result.getTransforms().add(transformation);
        }

        styleTools.applyStyle(result, element);

        loader.parentNode.getChildren().add(result);
    }


    public void handleElement(SVGOMCircleElement element) {
        // Get attributes from SVG node
        float x = element.getCx().getBaseVal().getValue();
        float y = element.getCy().getBaseVal().getValue();
        float r = element.getR().getBaseVal().getValue();

        // Create JavaFX Circle object
        Circle result = new Circle(x, y, r);
        result.setId(element.getId());

        Affine transformation = styleTools.getTransform(element);
        if (transformation != null) {
            result.getTransforms().add(transformation);
        }

        styleTools.applyStyle(result, element);

        loader.parentNode.getChildren().add(result);
    }


    public void handleElement(SVGOMEllipseElement element) {
        // Get attributes from SVG node
        float x = element.getCx().getBaseVal().getValue();
        float y = element.getCy().getBaseVal().getValue();
        float rx = element.getRx().getBaseVal().getValue();
        float ry = element.getRy().getBaseVal().getValue();

        // Create JavaFX Ellipse object
        Ellipse result = new Ellipse(x, y, rx, ry);
        result.setId(element.getId());

        Affine transformation = styleTools.getTransform(element);
        if (transformation != null) {
            result.getTransforms().add(transformation);
        }

        styleTools.applyStyle(result, element);

        loader.parentNode.getChildren().add(result);
    }


    public void handleElement(SVGOMRadialGradientElement e) {
        // Get attributes from SVG node
        String id = e.getId();
        float focusAngle = e.getFx().getBaseVal().getValue();   // TODO
        float focusDistance = e.getFy().getBaseVal().getValue();    // TODO
        float centerX = e.getCx().getBaseVal().getValue();
        float centerY = e.getCy().getBaseVal().getValue();
        float radius = e.getR().getBaseVal().getValue();
        List<Stop> stops = getStops(e);

        RadialGradient gradientObject = 
                new RadialGradient(focusAngle, focusDistance, centerX, centerY, radius, 
                                   true, CycleMethod.NO_CYCLE, 
                                   stops);
        styleTools.addPaint(id, gradientObject);
    }


    public void handleElement(SVGOMLinearGradientElement e) {
        // Get attributes from SVG node
        String id = e.getId();
        float startX = e.getX1().getBaseVal().getValue();
        float startY = e.getY1().getBaseVal().getValue();
        float endX = e.getX2().getBaseVal().getValue();
        float endY = e.getY2().getBaseVal().getValue();
        List<Stop> stops = getStops(e);

        LinearGradient gradientObject = 
                new LinearGradient(startX, startY, endX, endY, 
                                   true, CycleMethod.NO_CYCLE,
                                   stops);
        styleTools.addPaint(id, gradientObject);
    }


    private List<Stop> getStops(SVGOMGradientElement e) {
        List<Stop> result = new ArrayList<>();

        // traverse child nodes to add any stops
        for (int idx = 0;  idx < e.getChildNodes().getLength();  idx++) {
            Object stop = e.getChildNodes().item(idx);
            if (stop instanceof SVGOMStopElement) {
                Stop stopObject = createStopElement((SVGOMStopElement) stop);
                result.add(stopObject);
            }
        }

        // check xlink:href to get all stops from a possibly linked gradient
        String hRef = e.getHref().getBaseVal();
        if (hRef.startsWith("#")) {
            hRef = hRef.substring(1);
            Paint linked = styleTools.getPaint(hRef);
            if (linked != null) {
                if (linked instanceof LinearGradient) {
                    LinearGradient target = (LinearGradient) linked;
                    result.addAll(target.getStops());
                } else if (linked instanceof RadialGradient) {
                    RadialGradient target = (RadialGradient ) linked;
                    result.addAll(target.getStops());
                }
            }
        }

        return result;
    }
    
    
    private Stop createStopElement(SVGOMStopElement e) {
        float offset = e.getOffset().getBaseVal();

        CSSStyleDeclaration style = styleTools.svgElement.getComputedStyle(e, null);
        CSSOMSVGColor stopColorValue = (CSSOMSVGColor) style.getPropertyCSSValue("stop-color");

        float red = stopColorValue.getRed().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
        float green = stopColorValue.getGreen().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
        float blue = stopColorValue.getBlue().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;

        CSSOMValue stopOpacityValue = (CSSOMValue) style.getPropertyCSSValue("stop-opacity");
        float stopOpacity = stopOpacityValue.getFloatValue (CSSPrimitiveValue.CSS_NUMBER);

        Color stopColor = new Color(red, green, blue, stopOpacity);

        return new Stop(offset,  stopColor);
    }
}
