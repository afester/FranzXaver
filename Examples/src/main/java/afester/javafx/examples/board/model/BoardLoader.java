package afester.javafx.examples.board.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import afester.javafx.examples.board.eagle.SubContentHandler;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.text.FontWeight;


class BoardLoaderException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BoardLoaderException(String msg) {
        super(msg);
    }
}


class AttributeReader {

    final Attributes atts;

    public AttributeReader(Attributes attributes) {
        atts = attributes;
    }

    public String getString(String name) {
        final var value = atts.getValue(name);
        if (value == null) {
            throw new BoardLoaderException("Missing value for \"" + name + "\"");
        }
        return value;
    }

    public String getOptionalString(String name, String defaultValue) {
        final var value = atts.getValue(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public Double getDouble(String name) {
        final var value = atts.getValue(name);
        if (value == null) {
            throw new BoardLoaderException("Missing value for \"" + name + "\"");
        }
        return Double.parseDouble(value);
    }

    public Double getOptionalDouble(String name, Double defaultValue) {
        var value = atts.getValue(name);
        if (value == null) {
            return defaultValue;
        }
        return Double.parseDouble(value);
    }

    public Boolean getBoolean(String name) {
        final var value = atts.getValue(name);
        if (value == null) {
            throw new BoardLoaderException("Missing value for \"" + name + "\"");
        }
        return Boolean.parseBoolean(value);
    }

    public Boolean getOptionalBoolean(String name, Boolean defaultValue) {
        var value = atts.getValue(name);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
}


class BoardShapeHandler extends SubContentHandler {
    private final static Logger log = LogManager.getLogger();

    private BoardLoader loader;
    
    
    public BoardShapeHandler(BoardLoader boardLoader) {
        this.loader = boardLoader;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        final AttributeReader ar = new AttributeReader(attributes);

        if (localName.equals("point")) {
            final Point2D point = new Point2D(ar.getDouble("x"), ar.getDouble("y"));
            log.debug(point);
            loader.boardShape.add(point);
        } else {
            log.warn("Unexpected Element:" + localName);
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        return localName.equals("boardShape");
    }
}

class PackageHandler extends SubContentHandler {
    private final static Logger log = LogManager.getLogger();

    private ShapeText currentText = null;
    private Package thePackage = null;

    public PackageHandler(Package pkg) {
        thePackage = pkg;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        final AttributeReader ar = new AttributeReader(attributes);

        if (localName.equals("line")) {
            final var p1 = new Point2D(ar.getDouble("x1"), ar.getDouble("y1"));
            final var p2 = new Point2D(ar.getDouble("x2"), ar.getDouble("y2"));
            final var width = ar.getDouble("width");

            ShapeModel shape = new ShapeLine(p1, p2, width);
            log.debug(shape);
            thePackage.addShape(shape);
        } else if (localName.equals("rectangle")) {
            final var p1 = new Point2D(ar.getDouble("x1"), ar.getDouble("y1"));
            final var p2 = new Point2D(ar.getDouble("x2"), ar.getDouble("y2"));

            ShapeModel shape = new ShapeRectangle(p1, p2);
            log.debug(shape);
            thePackage.addShape(shape);
        } else if (localName.equals("circle")) {
            final var center = new Point2D(ar.getDouble("x"), ar.getDouble("y"));
            final var radius = ar.getDouble("radius");
            final var width = ar.getDouble("width");

            ShapeModel shape = new ShapeCircle(center, radius, width);
            log.debug(shape);
            thePackage.addShape(shape);
        } else if (localName.equals("arc")) {
            final var center = new Point2D(ar.getDouble("cx"), ar.getDouble("cy"));
            final var radius = ar.getDouble("radius");
            final var start = ar.getDouble("start");
            final var angle = ar.getDouble("angle");
            final var width = ar.getDouble("width");

            ShapeModel shape = new ShapeArc(center, radius, start, angle, width);
            log.debug(shape);
            thePackage.addShape(shape);
        } else if (localName.equals("text")) {
            final var pos = new Point2D(ar.getDouble("x"), ar.getDouble("y"));
            final var size = ar.getDouble("size");
            final var weight = FontWeight.valueOf(ar.getString("weight"));
            // String layer = textNode.getAttribute("layer");

            currentText = new ShapeText(pos, size, weight);
        } else if (localName.equals("pad")) {
            final var padNumber = ar.getString("padName");
            final var padPos = new Point2D(ar.getDouble("x"), ar.getDouble("y"));

            ShapePad pad = new ShapePad(padNumber, padPos);
            log.debug(pad);
            thePackage.addPad(pad);
        } else {
            log.warn("Unexpected element: " + localName);
        }

    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("text")) {
            log.debug(currentText);
            thePackage.addShape(currentText);
            currentText = null;
            return false;
        }

        return localName.equals("package");
    }
    

    @Override
    public void characters(char[] ch, int start, int length) {
        if (currentText != null) {
            currentText.append(new String(ch, start, length));
        }
    }

}

class PartHandler extends SubContentHandler {
    private final static Logger log = LogManager.getLogger();

    private Part part;
    private BoardLoader bl;

    public PartHandler(BoardLoader loader, Part part) {
        this.part = part;
        this.bl = loader;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        final AttributeReader ar = new AttributeReader(attributes);

        if (localName.equals("pad")) {
            final var padName = ar.getString("padName");
            final var padId = ar.getString("id");

            final Pin pin = part.getPin(padName);
            bl.nodes.put(padId, pin);
        } else {
            log.debug("   " + localName);
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        return localName.equals("part");
    }
}


class NetHandler extends SubContentHandler {
    private final static Logger log = LogManager.getLogger();

    private Net net;
    private BoardLoader bl;

    public NetHandler(BoardLoader bl, Net net) {
        this.bl = bl;
        this.net = net;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        final AttributeReader ar = new AttributeReader(attributes);

        if (localName.equals("junction")) {
            final var junctionId = ar.getString("id");
            final var jPos = new Point2D(ar.getDouble("x"),
                                         ar.getDouble("y"));

            Junction junction = new Junction(jPos);
            log.debug(junction);

            bl.nodes.put(junctionId, junction);
            net.addJunction(junction);
        } else if (localName.equals("airwire")) {
            final var fromId = ar.getString("from");    // unique id
            final var toId = ar.getString("to");        // unique id

            AbstractNode from = bl.nodes.get(fromId);
            AbstractNode to = bl.nodes.get(toId);

            Trace aw = new Trace(from, to, net, TraceType.AIRWIRE);
            log.debug(aw);

            // TODO: Hack to take care of airwires connected to hidden Parts
            if (from instanceof Pin) {
                final Pin p1 = (Pin) from;
                if (p1.getPart().isHidden()) {
                    aw.setHidden(true);
                }
            }
            if (to instanceof Pin) {
                final Pin p1 = (Pin) to;
                if (p1.getPart().isHidden()) {
                    aw.setHidden(true);
                }
            }
            
            
            net.addTrace(aw);
        } else if (localName.equals("trace")) {
            final var fromId = ar.getString("from");
            final var toId = ar.getString("to");
            final var isBridge = ar.getBoolean("isBridge");

            AbstractNode from = bl.nodes.get(fromId);
            AbstractNode to = bl.nodes.get(toId);

            Trace t = new Trace(from, to, net, TraceType.TRACE);
            if (isBridge) {
                t.setAsBridge();
            }
            log.debug(t);

            net.addTrace(t);
        } else {
            log.debug("   " + localName);
        }
      
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        return localName.equals("net");
    }
}


public class BoardLoader extends DefaultHandler {
    private final static Logger log = LogManager.getLogger();

    private File sourceFile;
    private SubContentHandler currentHandler;
    private String schematicFile;

    Package currentPackage = null;
    final Map<String, Package> packages = new HashMap<>();
    final List<Point2D> boardShape = new ArrayList<>();
    final List<Part> parts = new ArrayList<>();
    final List<Net> nets = new ArrayList<>();
    final Map<String, AbstractNode> nodes = new HashMap<>();

    public BoardLoader(File file) {
        sourceFile = file;
    }


    @Override
    public void startDocument() throws SAXException {
        log.info("Loading " + sourceFile.getAbsolutePath());
    }


    @Override
    public void endDocument() throws SAXException {
        log.info("Loaded {} board shape points", boardShape.size());
        log.info("Loaded {} Packages", packages.size());
        log.info("Loaded {} Parts", parts.size());
        log.info("Loaded {} Nets\n", nets.size());
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (currentHandler != null) {
            currentHandler.startElement(uri, localName, qName, attributes);
        } else {
            final AttributeReader ar = new AttributeReader(attributes);

            if (localName.equals("breadboard")) {
                log.debug("BreadBoard");
                schematicFile = ar.getString("schematic");
            } else if (localName.equals("boardShape")) {
                log.debug("Board shape");

                currentHandler = new BoardShapeHandler(this);
            } else if (localName.equals("package")) {
                final var id = ar.getString("id");
                final var name = ar.getString("name");

                Package thePackage = new Package(id, name);
                currentHandler = new PackageHandler(thePackage);

                // TODO: This prints the "empty" package! 
                // probably this should really be done in the endElement() method?
                log.debug(thePackage); 
                packages.put(thePackage.getId(), thePackage);
            } else if (localName.equals("part")) {
                final var name = ar.getString("name");
                final var value = ar.getString("value");
                final var packageRef = ar.getString("packageRef"); 
                final var partPosition = new Point2D(ar.getDouble("x"),
                                                     ar.getDouble("y"));
                final var rotation = ar.getOptionalDouble("rotation", 0.0);
                final var isHidden = ar.getOptionalBoolean("hidden", false);

                final Package thePackage = packages.get(packageRef);

                Part part = new Part(name, value, thePackage);
                part.setPosition(partPosition);
                part.setRotation(rotation);
                part.setHidden(isHidden);

                log.debug(part);
                currentHandler = new PartHandler(this, part);
                parts.add(part);
            } else if (localName.equals("net")) {

                final var name = ar.getString("name");
                final var net = new Net(name);

                currentHandler = new NetHandler(this, net);

                // TODO: This prints the "empty" net! 
                // probably this should really be done in the endElement() method?
                log.debug(net);
                nets.add(net);
            }
        }
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (currentHandler != null) {
            if (currentHandler.endElement(uri, localName, qName)) {
                currentHandler = null;
            }
        }
    }


    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentHandler != null) {
            currentHandler.characters(ch, start, length);
        }
    }


    public Board load() {

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(false);
        spf.setNamespaceAware(true);

        try {
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(this);
            InputStream is = new FileInputStream(sourceFile);
            xmlReader.parse(new InputSource(is));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Board result = new Board(sourceFile);
        result.setSchematicFile(schematicFile);
        nets.forEach(n -> result.addNet(n));
        parts.forEach(p -> result.addPart(p));
        
        ObservableList<Point2D> boardCorners = result.getBoardCorners();
        boardCorners.clear();
        boardShape.forEach(p -> boardCorners.add(p));

        return result;
    }
}

