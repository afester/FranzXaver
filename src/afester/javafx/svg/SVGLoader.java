package afester.javafx.svg;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javafx.scene.Group;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGOMDefsElement;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.SVGOMGElement;
import org.apache.batik.anim.dom.SVGOMMetadataElement;
import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.anim.dom.SVGOMPatternElement;
import org.apache.batik.anim.dom.SVGOMRectElement;
import org.apache.batik.anim.dom.SVGOMLineElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.anim.dom.SVGOMTSpanElement;
import org.apache.batik.anim.dom.SVGOMTextElement;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


public class SVGLoader {

    Group parentNode;

	// flag whether to add a rectangle in the size of the drawing
    boolean addRootRect = false;

    // flag whether to use the alternative SVG Path element handling which adds
    // separate nodes for the path elements instead of an SVGPath node
    private boolean useSeparatePathElements = false;

    private Map<String, Consumer<SVGOMElement>> elementMap = new HashMap<>();

    
    public SVGLoader() {
        SVGBasicElementHandler bh = new SVGBasicElementHandler(this);
        
        elementMap.put("svg",      e -> bh.handleElement((SVGOMSVGElement) e));
        elementMap.put("defs",     e -> bh.handleElement((SVGOMDefsElement) e));
        elementMap.put("metadata", e -> bh.handleElement((SVGOMMetadataElement) e));
        elementMap.put("g",        e -> bh.handleElement((SVGOMGElement) e));

        if (useSeparatePathElements) {
        	elementMap.put("path", e -> bh.handlePath2((SVGOMPathElement) e));
        } else {
        	elementMap.put("path", e -> bh.handleElement((SVGOMPathElement) e));
        }
        elementMap.put("line",     e -> bh.handleElement((SVGOMLineElement) e));
        elementMap.put("rect",     e -> bh.handleElement((SVGOMRectElement) e));
        elementMap.put("text",     e -> bh.handleElement((SVGOMTextElement) e));

        elementMap.put("tspan",    e -> bh.handleElement((SVGOMTSpanElement) e));
        elementMap.put("pattern",  e -> bh.handleElement((SVGOMPatternElement) e));
    }



    /**
     * @param flag Flag to determine whether or not to add a rectangle in the 
     *             size of the drawing's viewbox.
     */
    public void setAddViewboxRect(boolean flag) {
        this.addRootRect = flag;
    }


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
        NodeList children = node.getChildNodes();
        for (int i = 0;  i < children.getLength();  i++) {
            org.w3c.dom.Node element = children.item(i);
            handle(element);
        }

        parentNode = par;       // restore current parent
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


    public Document loadSvgDocument(InputStream svgFile) {
        try {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory( parser );
            SVGOMDocument document = (SVGOMDocument) factory.createDocument("", svgFile);

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

    
    /**
     * Loads an SVG file from a file with a specified name and returns a corresponding 
     * JavaFX scene graph.
     *
     * @param fileName The name of the SVG file to load.
     *
     * @return A JavaFX node representing the SVG file.
     */
    public Group loadSvg(String fileName) {
        // note: uses the DOM approach.
        // probably a SAX based approach would be better from a performance perspective.
        SVGOMDocument doc = (SVGOMDocument) loadSvgDocument(fileName);

        parentNode = new Group();
        handle(doc);
        return parentNode;
    }


    /**
     * Loads an SVG file from an InputStream and returns a corresponding 
     * JavaFX scene graph.
     *
     * @param svgFile A stream which provides the SVG document.
     *
     * @return A JavaFX node representing the SVG file.
     */
    public Group loadSvg(InputStream svgFile) {
        // note: uses the DOM approach.
        // probably a SAX based approach would be better from a performance perspective.
        SVGOMDocument doc = (SVGOMDocument) loadSvgDocument(svgFile);

        parentNode = new Group();
        handle(doc);
        return parentNode;
    }
}
