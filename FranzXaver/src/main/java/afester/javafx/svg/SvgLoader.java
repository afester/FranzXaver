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

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGOMCircleElement;
import org.apache.batik.anim.dom.SVGOMDefsElement;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.SVGOMEllipseElement;
import org.apache.batik.anim.dom.SVGOMGElement;
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
import org.apache.batik.anim.dom.SVGOMTSpanElement;
import org.apache.batik.anim.dom.SVGOMTextElement;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.NodeList;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class SvgLoader {
    private static final Logger logger = LogManager.getLogger();

    Group parentNode;

    // flag whether to add a rectangle in the size of the drawing
    boolean addRootRect = false;

    // flag whether to use the alternative SVG Path element handling which adds
    // separate nodes for the path elements instead of an SVGPath node
    private boolean useSeparatePathElements = false;

    private Map<String, Consumer<SVGOMElement>> elementMap = new HashMap<>();

    private SvgBasicElementHandler bh;

    /**
     * Creates a new SVGLoader.
     */
    public SvgLoader() {
        bh = new SvgBasicElementHandler(this);

        elementMap.put("svg", e -> bh.handleElement((SVGOMSVGElement) e));
        elementMap.put("defs", e -> bh.handleElement((SVGOMDefsElement) e));
        elementMap.put("metadata", e -> bh.handleElement((SVGOMMetadataElement) e));
        // elementMap.put("title", e -> {} );

        elementMap.put("g", e -> bh.handleElement((SVGOMGElement) e));
        elementMap.put("path", e -> bh.handleElement((SVGOMPathElement) e, 
                                                     useSeparatePathElements));
        elementMap.put("line", e -> bh.handleElement((SVGOMLineElement) e));
        elementMap.put("rect", e -> bh.handleElement((SVGOMRectElement) e));
        elementMap.put("polygon", e -> bh.handleElement((SVGOMPolygonElement) e));
        elementMap.put("polyline", e -> bh.handleElement((SVGOMPolylineElement) e));
        elementMap.put("circle", e -> bh.handleElement((SVGOMCircleElement) e));
        elementMap.put("ellipse", e -> bh.handleElement((SVGOMEllipseElement) e));
        elementMap.put("text", e -> bh.handleElement((SVGOMTextElement) e));
        
        elementMap.put("tspan", e -> bh.handleElement((SVGOMTSpanElement) e));
        elementMap.put("pattern", e -> bh.handleElement((SVGOMPatternElement) e));

        elementMap.put("linearGradient", e -> bh.handleElement((SVGOMLinearGradientElement) e));
        elementMap.put("radialGradient", e -> bh.handleElement((SVGOMRadialGradientElement) e));
        elementMap.put("stop", e -> { } );

        /*
         * <title>
         * 
         * <a> <altGlyph> <altGlyphDef> <altGlyphItem> <animate> <animateColor>
         * <animateMotion> <animateTransform> <clipPath> <color-profile>
         * <cursor> <desc> <filter> <feGaussianBlur> <feOffset>
         * <feSpecularLighting> <fePointLight> <feComposite> <feMerge>
         * <feMergeNode> <feBlend> <feColorMatrix> <feComponentTransfer>
         * <feConvolveMatrix> <feDiffuseLighting> <feDisplacementMap>
         * <feDistantLight> <feFlood> <feFuncA> <feFuncB> <feFuncG> <feFuncR>
         * <feImage> <feMorphology> <feSpotLight> <feTile> <feTurbulence> <font>
         * <font-face> <font-face-format> <font-face-name> <font-face-src>
         * <font-face-uri> <foreignObject> <glyph> <glyphRef> <hkern> <image>
         * <marker> <mask> <missing-glyph> <mpath> <script>
         * <set> <style> <switch> <symbol> <textPath> <tref> <use> <view>
         * <vkern>
         */
    }

    
    /**
     * Defines whether the returned JavaFX group shall contain a rectangle
     * in the size of the svg document's viewbox.
     * 
     * @param flag Flag to determine whether or not to add a rectangle in the
     *             size of the drawing's viewbox.
     */
    public void setAddViewboxRect(boolean flag) {
        this.addRootRect = flag;
    }

    
    /**
     * Defines the gradient transformation policy to use when an SVG
     * gradient element contains a gradientTransform attribute.
     *
     * @param policy The {@link GradientPolicy} to use.
     */
    public void setGradientTransformPolicy(GradientPolicy policy) {
        bh.gradientFactory.setTransformationPolicy(policy);
    }

    
    private void handle(org.w3c.dom.Node node) {
        Group par = parentNode; // save current parent

        // Dispatch handling of the current node to its handler
        String localName = node.getLocalName();
        if (localName != null) {
            Consumer<SVGOMElement> consumer = elementMap.get(node.getLocalName());
            if (consumer != null) {
                consumer.accept((SVGOMElement) node);
            } else {
                logger.warn("Unknown element {} ({}):", node.getLocalName(), node);
            }
        }

        // Recursively handle child nodes
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            org.w3c.dom.Node element = children.item(i);
            handle(element);
        }

        parentNode = par; // restore current parent
    }

    
    /**
     * @param fileName The name of the SVG file to load.
     * @return An XML document with the loaded SVG file.
     */
    public SVGOMDocument loadSvgDocument(String fileName) {
        try {
            InputStream svgFile = new FileInputStream(fileName);
            return loadSvgDocument(svgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Loads an SVG document from an input stream.
     *
     * @param svgFile The input stream which provides the SVG document. 
     * @return A DOM document which represents the SVG file.
     */
    public SVGOMDocument loadSvgDocument(InputStream svgFile) {
        try {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            SVGOMDocument document = (SVGOMDocument) factory.createDocument("", svgFile);

            UserAgent userAgent = new UserAgentAdapter();
            DocumentLoader loader = new DocumentLoader(userAgent);
            BridgeContext bridgeContext = new BridgeContext(userAgent, loader);
            bridgeContext.setDynamicState(BridgeContext.DYNAMIC);

            // Enable CSS- and SVG-specific enhancements.
            (new GVTBuilder()).build(bridgeContext, document);

            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    
    /**
     * Loads an SVG file from a file with a specified name and returns a
     * corresponding JavaFX Group node.
     *
     * @param fileName The name of the SVG file to load.
     *
     * @return A JavaFX node representing the SVG file.
     */
    public Group loadSvg(String fileName) {
        // note: uses the DOM approach.
        // probably a SAX based approach would be better from a performance
        // perspective.
        SVGOMDocument doc = loadSvgDocument(fileName);

        parentNode = new Group();
        handle(doc);
        return parentNode;
    }


    /**
     * Loads an SVG file from an InputStream and returns a corresponding JavaFX
     * Group node.
     *
     * @param svgFile A stream which provides the SVG document.
     *
     * @return A JavaFX node representing the SVG file.
     */
    public Group loadSvg(InputStream svgFile) {
        // note: uses the DOM approach.
        // probably a SAX based approach would be better from a performance
        // perspective.
        SVGOMDocument doc = loadSvgDocument(svgFile);

        parentNode = new Group();
        handle(doc);

        return parentNode;
    }

}
