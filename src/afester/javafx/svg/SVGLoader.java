package afester.javafx.svg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGOMAnimatedPathData;
import org.apache.batik.anim.dom.SVGOMAnimatedPathData.BaseSVGPathSegList;
import org.apache.batik.anim.dom.SVGOMDefsElement;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.SVGOMGElement;
import org.apache.batik.anim.dom.SVGOMMetadataElement;
import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.anim.dom.SVGOMPatternElement;
import org.apache.batik.anim.dom.SVGOMRectElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.anim.dom.SVGOMTSpanElement;
import org.apache.batik.anim.dom.SVGOMTextElement;
import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.css.dom.CSSOMComputedStyle;
import org.apache.batik.css.dom.CSSOMComputedStyle.ComputedCSSValue;
import org.apache.batik.css.dom.CSSOMSVGComputedStyle;
import org.apache.batik.css.dom.CSSOMSVGComputedStyle.ComputedCSSPaintValue;
import org.apache.batik.dom.svg.SVGPathSegItem;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPaint;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGTransformList;
import org.w3c.dom.svg.SVGTransformable;


public class SVGLoader {

    /** The root &lt;svg&gt; element */
    private SVGOMSVGElement svgElement = null;

    private Group parentNode;
    
    private Group result;

	// flag whether to add a rectangle in the size of the drawing
    private boolean addRootRect = false;

    // flag whether to use the alternative SVG Path element handling which adds
    // separate nodes for the path elements instead of an SVGPath node
    private boolean useSeparatePathElements = false;

    private Map<String, Consumer<SVGOMElement>> elementMap = new HashMap<>();

    public SVGLoader() {
        elementMap.put("svg",      e -> handleElement((SVGOMSVGElement) e));
        elementMap.put("defs",     e -> handleElement((SVGOMDefsElement) e));
        elementMap.put("metadata", e -> handleElement((SVGOMMetadataElement) e));
        elementMap.put("g",        e -> handleElement((SVGOMGElement) e));
        if (useSeparatePathElements) {
        	elementMap.put("path", e -> handlePath2((SVGOMPathElement) e));
        } else {
        	elementMap.put("path", e -> handleElement((SVGOMPathElement) e));
        }
        elementMap.put("rect",     e -> handleElement((SVGOMRectElement) e));
        elementMap.put("text",     e -> handleElement((SVGOMTextElement) e));
        elementMap.put("tspan",    e -> handleElement((SVGOMTSpanElement) e));
        elementMap.put("pattern",  e -> handleElement((SVGOMPatternElement) e));
    }


    // <svg>
    void handleElement(SVGOMSVGElement element) {
        svgElement = element;

        // optionally add a rectangle using the size of the whole drawing
        if (addRootRect) {
            float height = element.getViewBox().getBaseVal().getHeight();
            float width = element.getViewBox().getBaseVal().getWidth();
            Rectangle result = new Rectangle(width,  height, null);
            result.setId(element.getId());
            result.setStroke(Color.BLACK);
            result.getStrokeDashArray().addAll(3.0,7.0,3.0,7.0);

            parentNode.getChildren().add(result);    
        }
    }


    // <defs>
    void handleElement(SVGOMDefsElement element) {
        System.err.println("Handling <defs>: " + element);
    }


    public Image snapshotImage = null;

    // <g>
    void handleElement(SVGOMGElement element) {
        Group result = new Group();
        result.setId(element.getId());

        Affine transformation = getTransform(element);
        if (transformation != null) {
            result.getTransforms().add(transformation);
        }


        parentNode.getChildren().add(result);
        parentNode = result;
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

        Affine transformation = getTransform(element);
        if (transformation != null) {
            result.getTransforms().add(transformation);
        }

        applyStyle(result, element);

        //fxObj.setStroke(Color.VIOLET);
        parentNode.getChildren().add(result);
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

        Affine transformation = getTransform(element);
        if (transformation != null) {
            result.getTransforms().add(transformation);
        }

        applyStyle(result, element);

        System.err.println("*** FILL:" + result.getFill());
//        result.setFill(new Color(0.455, 0.455, 0.455, 0.09));
        System.err.println("*** FILL2:" + result.getFill());
        parentNode.getChildren().add(result);
    }

    void handleElement(SVGOMTextElement element) {
        // Get attributes from SVG node
        String text = element.getTextContent();
        float xpos = element.getX().getBaseVal().getItem(0).getValue();
        float ypos = element.getY().getBaseVal().getItem(0).getValue();

        // Create JavaFX text object
        Text result = new Text(xpos, ypos, text);
        result.setId(element.getId());

        Affine transformation = getTransform(element);
        if (transformation != null) {
            result.getTransforms().add(transformation);
        }

        applyTextStyle(result, element);

        parentNode.getChildren().add(result);
    }

    void handleElement(SVGOMTSpanElement element) {
        System.err.println("Handling <tspan>: " + element);
    }

    void handleElement(SVGOMPatternElement element) {
        System.err.println("Handling <pattern>: " + element);
    }

    /**
     * @param fileName The name of the SVG file to load.
     *
     * @return A JavaFX node representing the SVG file.
     */
    public Group loadSvg(String fileName) {
        // note: uses the DOM approach.
        // probably a SAX based approach would be better from a performance perspective.
        SVGOMDocument doc = (SVGOMDocument) loadSvgDocument(fileName);

        result = new Group();
        parentNode = result;
        handle(doc);
        return result;
    }


    /**
     * @param flag Flag to determine whether or not to add a rectangle in the 
     *             size of the drawing's viewbox.
     */
    public void setAddViewboxRect(boolean flag) {
        this.addRootRect = flag;
    }

    
    private String indent(int level) {
        return "                                    ".substring(0, level*2);
    }

    
    private int level = 0;
    
    private void handle(org.w3c.dom.Node node) {
        Group par = parentNode;  // save current parent

        // Dispatch handling of the current node to its handler
        String localName = node.getLocalName();
        if (localName != null) {
            Consumer<SVGOMElement> c = elementMap.get(node.getLocalName());
            // System.err.printf("Handling %s through %s\n", localName, c);
            if (c != null) {
                c.accept((SVGOMElement) node);
            } else {
                System.err.println("UNKNOWN:" + node.getLocalName() + "/" + node);
            }
        }

        // Recursively handle child nodes
        level++;
        NodeList children = node.getChildNodes();
        for (int i = 0;  i < children.getLength();  i++) {
            org.w3c.dom.Node element = children.item(i);
            handle(element);
        }
        level--;


        // NOTE: Filter needs to be handled after the children have been added!
        if (localName != null && localName.equals("g")) {
            String filter = ((SVGOMElement) node).getAttribute("filter");
            if (filter != null && !filter.isEmpty()) {
                System.err.println(">> FILTER:" + filter);

                
                // NOTE: In the example, the <g> node consists of two 
                // stretched circles and the text. For these nodes,
                // the alpha channel is implicitly 1.0 - However, there is no
                // background color!!!! Hence, extracting the alpha channel
                // from those nodes would result in an exact mask of these 
                // three shapes.
                SnapshotParameters params = new SnapshotParameters();
                params.setFill(new Color(1.0, 1.0, 1.0, 0.0));
                snapshotImage = parentNode.snapshot(params, null);
            }
        }

        parentNode = par;       // restore current parent
    }


    private Affine getTransform(SVGTransformable element) {
        Affine fxTrans = null;

        SVGTransformList svgTransformations = element.getTransform().getBaseVal();
        if (svgTransformations.getNumberOfItems() > 1) {
            throw new RuntimeException("More than one transformation matrix not yet supported");
        }
        if (svgTransformations.getNumberOfItems() == 1) {
            SVGTransform svgTrans = svgTransformations.getItem(0);
            SVGMatrix m = svgTrans.getMatrix();

            // SVG: matrix(0.67018323,-0.74219568,0.74219568,0.67018323,0,0)
            //         [   a    c    e  ]
            //         [   b    d    f  ]
            //         [   0    0    1  ]

            // JavaFX: [  mxx  mxy  mxz  tx  ]
            //         [  myx  myy  myz  ty  ]
            //         [  mzx  mzy  mzz  tz  ]

            fxTrans = new Affine(m.getA(), m.getC(), m.getE(), m.getB(), m.getD(), m.getF());
        }

        return fxTrans;
    }

    private Color getFillColor(SVGStylableElement obj) {
        Color result = null;
        
        // svgElement.getComputedStyle() takes care of all styling aspects,
        // like inheritance of style attributes or presentation versus CSS styles
        CSSStyleDeclaration style = svgElement.getComputedStyle(obj, null);
        CSSOMSVGComputedStyle.ComputedCSSPaintValue val = (ComputedCSSPaintValue) style.getPropertyCSSValue("fill");

        if (val.getPaintType() == SVGPaint.SVG_PAINTTYPE_NONE) {    // fill=none
            return null;
        }

        float red = val.getRed().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
        float green = val.getGreen().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
        float blue = val.getBlue().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;

        CSSOMComputedStyle.ComputedCSSValue  opacity = (ComputedCSSValue) style.getPropertyCSSValue("fill-opacity");
        float alpha = opacity.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
        result = new Color(red, green, blue, alpha);

        return result;
    }

    
    private Color getStrokeColor(SVGStylableElement obj) {

        Color result = null;

        CSSStyleDeclaration style = svgElement.getComputedStyle(obj, null);
        CSSOMSVGComputedStyle.ComputedCSSPaintValue val = (ComputedCSSPaintValue) style.getPropertyCSSValue("stroke");

        if (val.getPaintType() == SVGPaint.SVG_PAINTTYPE_NONE) {
            return null;    // stroke=none
        }

        float red = val.getRed().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
        float green = val.getGreen().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
        float blue = val.getBlue().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;

        CSSOMComputedStyle.ComputedCSSValue opacity = (ComputedCSSValue) style.getPropertyCSSValue("stroke-opacity");
        float alpha = opacity.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);

        result = new Color(red, green, blue, alpha);

        return result;
    }


    private void applyStyle(Shape fxObj, SVGStylableElement obj) {

        Color fillColor = getFillColor(obj);
        fxObj.setFill(fillColor);

        Color strokeColor = getStrokeColor(obj);
        fxObj.setStroke(strokeColor);

        CSSStyleDeclaration style = svgElement.getComputedStyle(obj, null);
        CSSOMSVGComputedStyle.ComputedCSSValue swidth = (ComputedCSSValue) style.getPropertyCSSValue("stroke-width");
        if (swidth != null) {
            float strokeWidth = swidth.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
            fxObj.setStrokeWidth(strokeWidth);
        }
    }

    
    private void applyTextStyle(Text fxObj, SVGStylableElement obj) {
        CSSStyleDeclaration style = svgElement.getComputedStyle(obj, null); // obj.getStyle();

        String fontFamily = null;
        CSSOMComputedStyle.ComputedCSSValue val = (ComputedCSSValue) style.getPropertyCSSValue("font-family");
        if (val != null) {
            fontFamily = val.getCssText();
        }

        float fontSize = 0;
        CSSOMComputedStyle.ComputedCSSValue val2 = (ComputedCSSValue) style.getPropertyCSSValue("font-size");
        if (val2 != null) {
            if (val2.getPrimitiveType() == CSSPrimitiveValue.CSS_NUMBER) {
                fontSize = val2.getFloatValue (CSSPrimitiveValue.CSS_NUMBER);   // https://bugs.launchpad.net/inkscape/+bug/168164
            } else {
                fontSize = val2.getFloatValue (CSSPrimitiveValue.CSS_PX);       // https://bugs.launchpad.net/inkscape/+bug/168164
            }
        }

        System.err.println("FONT: " + fontFamily + "/" + fontSize);
        Font font = Font.font(fontFamily, fontSize);
        fxObj.setFont(font);
        
        applyStyle(fxObj, obj);
        
/*        font-style:normal;
        font-variant:normal;
        font-weight:normal;
        font-stretch:normal;
*/
    }


    /**
     * Alternative handling of an SVG path.
     * Creates distinct JavaFX Shapes for each path segment.
     * 
     * @param obj
     */
    private Node handlePath2(SVGOMPathElement element) {
        
        Group g = new Group();
        g.setId(element.getId());

        double xpos = 0.0;
        double ypos = 0.0;

        //CSSStyleDeclaration style = obj.getStyle();
        SVGOMAnimatedPathData.BaseSVGPathSegList list = (BaseSVGPathSegList) element.getPathSegList();
        System.err.printf("%s%s\n", indent(level), "PATH: " + list.getNumberOfItems());
        for (int i = 0;  i < list.getNumberOfItems();  i++) {
            SVGPathSegItem item = (SVGPathSegItem) list.getItem(i);
            Shape fxObj = null;

            switch(item.getPathSegType()) { 
                case SVGPathSegItem.PATHSEG_UNKNOWN : 
                    System.err.printf("%s PATHSEG_UNKNOWN\n", indent(level+2));
                    break;
                
                case SVGPathSegItem.PATHSEG_CLOSEPATH : 
                    System.err.printf("%s PATHSEG_CLOSEPATH\n", indent(level+2));
                    break;

                case SVGPathSegItem.PATHSEG_MOVETO_ABS : 
                    xpos = item.getX();
                    ypos = item.getY();
                    System.err.printf("%s moveto(%f/%f)\n", indent(level+2), xpos, ypos);
                    break;
                
                case SVGPathSegItem.PATHSEG_MOVETO_REL :
                    xpos += item.getX();
                    ypos += item.getY();
                    System.err.printf("%s moveto(%f/%f)\n", indent(level+2), xpos, ypos);
                    break;

                case SVGPathSegItem.PATHSEG_LINETO_ABS  : { 
                        double x2 = item.getX();
                        double y2 = item.getY();
                        
                        System.err.printf("%s lineto(%f/%f)\n", indent(level+2), x2, y2);
                        fxObj = new Line(xpos, ypos, x2, y2);
                        
                        xpos = x2;
                        ypos = y2;
                    }
                    break;

                case SVGPathSegItem.PATHSEG_LINETO_REL  : {
                        double x2 = xpos + item.getX();
                        double y2 = ypos + item.getY();
                        
                        System.err.printf("%s lineto(%f/%f)\n", indent(level+2), x2, y2);
                        fxObj = new Line(xpos, ypos, x2, y2);
                        
                        xpos = x2;
                        ypos = y2;
                    }
                    break;

                case SVGPathSegItem.PATHSEG_CURVETO_CUBIC_ABS  : {
                        double endX = xpos + item.getX();
                        double endY = ypos + item.getY();
                        
                        System.err.printf("%s cubicCurve(%f/%f, %f/%f)\n", indent(level+2), xpos, ypos, endX, endY);
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
                        
                        System.err.printf("%s cubicCurve(%f/%f, %f/%f)\n", indent(level+2), xpos, ypos, endX, endY);
                        fxObj = new CubicCurve(xpos, ypos, x1, y1, x2, y2, endX, endY);

                        xpos = endX;
                        ypos = endY;
                    }
                    break;

                default:
                    System.err.printf("%s UNKNOWN\n", indent(level+2));
                    break;
            }

            if (fxObj != null) {
                
                System.err.printf("%s\n", element);
                applyStyle(fxObj, element);

                g.getChildren().add(fxObj);
            }
        }

        return g;
    }


    /**
     * @param The name of the SVG file to load.
     * @return An XML document with the loaded SVG file.
     */
    public Document loadSvgDocument(String fileName) {
        try {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory( parser );
            SVGOMDocument document = (SVGOMDocument) factory.createDocument( fileName );
    
            UserAgent userAgent = new UserAgentAdapter();
            DocumentLoader loader = new DocumentLoader( userAgent );
            BridgeContext bridgeContext = new BridgeContext( userAgent, loader );
            bridgeContext.setDynamicState( BridgeContext.DYNAMIC );
    
            // Enable CSS- and SVG-specific enhancements.
            (new GVTBuilder()).build( bridgeContext, document );

            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
