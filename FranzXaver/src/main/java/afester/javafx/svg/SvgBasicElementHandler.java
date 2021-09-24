/*
 * Copyright 2016 Andreas Fester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package afester.javafx.svg;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
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
import org.apache.batik.anim.dom.SVGOMPolygonElement;
import org.apache.batik.anim.dom.SVGOMPolylineElement;
import org.apache.batik.anim.dom.SVGOMRadialGradientElement;
import org.apache.batik.anim.dom.SVGOMRectElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.anim.dom.SVGOMStopElement;
import org.apache.batik.anim.dom.SVGOMTSpanElement;
import org.apache.batik.anim.dom.SVGOMTextElement;
import org.apache.batik.css.dom.CSSOMSVGColor;
import org.apache.batik.css.dom.CSSOMValue;
import org.apache.batik.dom.svg.SVGPathSegItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;

import java.util.ArrayList;
import java.util.List;


public class SvgBasicElementHandler {
    private static final Logger logger = LogManager.getLogger();

    public SvgStyleTools styleTools = null;
    private SvgLoader loader = null;
    GradientFactory gradientFactory = new GradientFactory();

    SvgBasicElementHandler(SvgLoader svgLoader) {
        this.loader = svgLoader;
    }


    // <svg>
    void handleElement(SVGOMSVGElement element) {
        styleTools = new SvgStyleTools(element);

        // optionally add a rectangle using the size of the whole drawing
        if (loader.addRootRect) {
            SVGRect viewPort = element.getViewBox().getBaseVal();
            float height = viewPort.getHeight();
            float width = viewPort.getWidth();
            Rectangle result = new Rectangle(width,  height, null);
            result.setId(element.getId());
            result.setStroke(Color.BLACK);
            result.getStrokeDashArray().addAll(3.0,7.0,3.0,7.0);

            loader.parentNode.getChildren().add(result);    
        }
    }


    // <defs>
    void handleElement(SVGOMDefsElement element) {
        logger.debug("Handling <defs>: {}", element);
    }


    // <svg:g>
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
        logger.debug("Handling <metadata>: {}", element);
    }


    void handleElement(SVGOMPathElement element, boolean useSeparateShapes) {
        if (useSeparateShapes) {
            handlePathElementsSeparately(element);
        } else {
            handlePathElement(element);
        }
    }


    void handleElement(SVGOMRectElement element) {
        // Get attributes from SVG node
        float xpos = element.getX().getBaseVal().getValue();
        float ypos = element.getY().getBaseVal().getValue();
        float width = element.getWidth().getBaseVal().getValue();
        float height = element.getHeight().getBaseVal().getValue();
        float cornerWidth = element.getRx().getBaseVal().getValue();
        float cornerHeight = element.getRy().getBaseVal().getValue();

        // Create JavaFX Rectangle object
        Rectangle result = new Rectangle(xpos, ypos, width, height);
        result.setId(element.getId());
        result.setArcWidth(2 * cornerWidth);
        result.setArcHeight(2 * cornerHeight);

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
        logger.debug("Handling <tspan>: {}", element);
    }


    void handleElement(SVGOMPatternElement element) {
        logger.debug("Handling <pattern>: {}", element);
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


    void handleElement(SVGOMCircleElement element) {
        // Get attributes from SVG node
        float centerX = element.getCx().getBaseVal().getValue();
        float centerY = element.getCy().getBaseVal().getValue();
        float radius = element.getR().getBaseVal().getValue();

        // Create JavaFX Circle object
        Circle result = new Circle(centerX, centerY, radius);
        result.setId(element.getId());

        Affine transformation = styleTools.getTransform(element);
        if (transformation != null) {
            result.getTransforms().add(transformation);
        }

        styleTools.applyStyle(result, element);

        loader.parentNode.getChildren().add(result);
    }


    void handleElement(SVGOMEllipseElement element) {
        // Get attributes from SVG node
        float centerX = element.getCx().getBaseVal().getValue();
        float centerY = element.getCy().getBaseVal().getValue();
        float rx = element.getRx().getBaseVal().getValue();
        float ry = element.getRy().getBaseVal().getValue();

        // Create JavaFX Ellipse object
        Ellipse result = new Ellipse(centerX, centerY, rx, ry);
        result.setId(element.getId());

        Affine transformation = styleTools.getTransform(element);
        if (transformation != null) {
            result.getTransforms().add(transformation);
        }

        styleTools.applyStyle(result, element);

        loader.parentNode.getChildren().add(result);
    }


    void handleElement(SVGOMRadialGradientElement element) {
        // Get attributes from SVG node
        String id = element.getId();
        List<Stop> stops = getStops(element);

        RadialGradient gradientObject = gradientFactory.createRadialGradient(element, stops);
        styleTools.addPaint(id, gradientObject);

    }


    void handleElement(SVGOMLinearGradientElement element) {
        // Get attributes from SVG node
        String id = element.getId();
        List<Stop> stops = getStops(element);
        
        LinearGradient gradientObject = gradientFactory.createLinearGradient(element, stops);
        styleTools.addPaint(id, gradientObject);
    }

    
    /**
     * Converts an SVGOMPolygonElement svg node to a JavaFX Polygon object.
     *
     * @param element The svg Polygon node to convert. 
     */
    void handleElement(SVGOMPolygonElement element) {
        // Get attributes from SVG node
        double[] coordinates = getCoordinates(element.getPoints());

        // Create JavaFX Polygon object
        Polygon result = new Polygon(coordinates);
        result.setId(element.getId());

        Affine transformation = styleTools.getTransform(element);
        if (transformation != null) {
            result.getTransforms().add(transformation);
        }

        styleTools.applyStyle(result, element);

        loader.parentNode.getChildren().add(result);
    }

    
    /**
     * Converts an SVGOMPolylineElement svg node to a JavaFX Polyline object.
     *
     * @param element The svg PolyLine node to convert. 
     */
    void handleElement(SVGOMPolylineElement element) {
        // Get attributes from SVG node
        double[] coordinates = getCoordinates(element.getPoints());

        // Create JavaFX Polygon object
        Polyline result = new Polyline(coordinates);
        result.setId(element.getId());

        Affine transformation = styleTools.getTransform(element);
        if (transformation != null) {
            result.getTransforms().add(transformation);
        }

        styleTools.applyStyle(result, element);

        loader.parentNode.getChildren().add(result);
    }

    
    /**
     * Returns an array with coordinate pairs from an SVGPointList.
     *
     * @param points The SVGPointList which contains the svg points.
     * @return An array of X/Y coordinate pairs which correspond to the 
     *         given svg point list.
     */
    private static double[] getCoordinates(SVGPointList points) {
        int numberOfPoints = points.getNumberOfItems();
        double[] coordinates = new double[2 * numberOfPoints];

        for (int i = 0; i < numberOfPoints; i++) {
            SVGPoint point = points.getItem(i);
            coordinates[2 * i] = point.getX();
            coordinates[2 * i + 1] = point.getY();
        }
        return coordinates;
    }


    private void handlePathElement(SVGOMPathElement element) {
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
     * @param element The SVG path element to handle.
     */
    private void handlePathElementsSeparately(SVGOMPathElement element) {

        Group result = new Group();
        result.setId(element.getId());

        double xpos = 0.0;
        double ypos = 0.0;

        SVGOMAnimatedPathData.BaseSVGPathSegList list = 
                                    (BaseSVGPathSegList) element.getPathSegList();
        for (int i = 0;  i < list.getNumberOfItems();  i++) {
            SVGPathSegItem item = (SVGPathSegItem) list.getItem(i);
            Shape fxObj = null;

            switch (item.getPathSegType()) { 
              case SVGPathSegItem.PATHSEG_UNKNOWN : 
                  break;

              case SVGPathSegItem.PATHSEG_CLOSEPATH : 
                  break;

              case SVGPathSegItem.PATHSEG_MOVETO_ABS : 
                  xpos = item.getX();
                  ypos = item.getY();
                  break;
                
              case SVGPathSegItem.PATHSEG_MOVETO_REL :
                  xpos += item.getX();
                  ypos += item.getY();
                  break;

              case SVGPathSegItem.PATHSEG_LINETO_ABS  : { 
                  double x2 = item.getX();
                  double y2 = item.getY();
                        
                  fxObj = new Line(xpos, ypos, x2, y2);
                        
                  xpos = x2;
                  ypos = y2;
              }
              break;

              case SVGPathSegItem.PATHSEG_LINETO_REL  : {
                  double x2 = xpos + item.getX();
                  double y2 = ypos + item.getY();
                        
                  fxObj = new Line(xpos, ypos, x2, y2);
                        
                  xpos = x2;
                  ypos = y2;
              }
              break;

              case SVGPathSegItem.PATHSEG_CURVETO_CUBIC_ABS  : {
                  double endX = xpos + item.getX();
                  double endY = ypos + item.getY();
                        
                  fxObj = new CubicCurve(xpos, ypos, item.getX1(), item.getY1(), 
                                         item.getX2(), item.getY2(), endX, endY);

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
                        
                  fxObj = new CubicCurve(xpos, ypos, x1, y1, x2, y2, endX, endY);

                  xpos = endX;
                  ypos = endY;
              }
              break;

              default:
                  break;
            }

            if (fxObj != null) {
                logger.debug(element);
                styleTools.applyStyle(fxObj, element);

                result.getChildren().add(fxObj);
            }
        }

        loader.parentNode.getChildren().add(result);
    }

    private List<Stop> getStops(SVGOMGradientElement element) {
        List<Stop> result = new ArrayList<>();

        // traverse child nodes to add any stops
        for (int idx = 0;  idx < element.getChildNodes().getLength();  idx++) {
            Object stop = element.getChildNodes().item(idx);
            if (stop instanceof SVGOMStopElement) {
                Stop stopObject = createStopElement((SVGOMStopElement) stop);
                result.add(stopObject);
            }
        }

        // check xlink:href to get all stops from a possibly linked gradient
        String href = element.getHref().getBaseVal();
        if (href.startsWith("#")) {
            href = href.substring(1);
            Paint linked = styleTools.getPaint(href);
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


    private Stop createStopElement(SVGOMStopElement element) {
        float offset = element.getOffset().getBaseVal();

        CSSStyleDeclaration style = styleTools.svgElement.getComputedStyle(element, null);
        CSSOMSVGColor stopColorValue = (CSSOMSVGColor) style.getPropertyCSSValue("stop-color");

        float red = stopColorValue.getRed().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
        float green = stopColorValue.getGreen().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
        float blue = stopColorValue.getBlue().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;

        CSSOMValue stopOpacityValue = (CSSOMValue) style.getPropertyCSSValue("stop-opacity");
        float stopOpacity = stopOpacityValue.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);

        Color stopColor = new Color(red, green, blue, stopOpacity);
        logger.debug("stopColor={}", stopColor);

        return new Stop(offset, stopColor);
    }
}
