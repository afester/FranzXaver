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

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import afester.javafx.examples.board.eagle.SubContentHandler;
import javafx.geometry.Point2D;
import javafx.scene.text.FontWeight;

class BoardShapeHandler extends SubContentHandler {

    private BoardLoader loader;
    
    
    public BoardShapeHandler(BoardLoader boardLoader) {
        this.loader = boardLoader;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("point")) {
            final Point2D point = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                              Double.parseDouble(attributes.getValue("y")));
            System.err.println("  " + point);
            loader.boardShape.add(point);
        } else {
            // unexpected element
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        return localName.equals("boardShape");
    }
}

class PackageHandler extends SubContentHandler {

    private PartText currentText = null;
    private Package thePackage = null;

    public PackageHandler(Package pkg) {
        thePackage = pkg;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("line")) {
            Point2D p1 = new Point2D(Double.parseDouble(attributes.getValue("x1")),
                                     Double.parseDouble(attributes.getValue("y1")));
            Point2D p2 = new Point2D(Double.parseDouble(attributes.getValue("x2")),
                                     Double.parseDouble(attributes.getValue("y2")));
            Double width = Double.parseDouble(attributes.getValue("width"));

            PartShape shape = new PartLine(p1, p2, width);
            System.err.println("  " + shape);
            thePackage.addShape(shape);
        } else if (localName.equals("rectangle")) {
            Point2D p1 = new Point2D(Double.parseDouble(attributes.getValue("x1")),
                                     Double.parseDouble(attributes.getValue("y1")));
            Point2D p2 = new Point2D(Double.parseDouble(attributes.getValue("x2")),
                                     Double.parseDouble(attributes.getValue("y2")));

            PartShape shape = new PartRectangle(p1, p2);
            System.err.println("  " + shape);
            thePackage.addShape(shape);
        } else if (localName.equals("circle")) {
            Point2D center = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                         Double.parseDouble(attributes.getValue("y")));
            double radius = Double.parseDouble(attributes.getValue("radius"));
            double width = Double.parseDouble(attributes.getValue("width"));

            PartShape shape = new PartCircle(center, radius, width);
            System.err.println("  " + shape);
            thePackage.addShape(shape);
        } else if (localName.equals("arc")) {

            Point2D cx = new Point2D(Double.parseDouble(attributes.getValue("cx")),
                                     Double.parseDouble(attributes.getValue("cy")));
            Double radius = Double.parseDouble(attributes.getValue("radius"));
            Double start = Double.parseDouble(attributes.getValue("start"));
            Double angle = Double.parseDouble(attributes.getValue("angle"));
            Double width = Double.parseDouble(attributes.getValue("width"));

            PartShape shape = new PartArc(cx, radius, start, angle, width);
            System.err.println("  " + shape);
            thePackage.addShape(shape);
        } else if (localName.equals("text")) {

            Point2D pos = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                      Double.parseDouble(attributes.getValue("y")));
            Double size = Double.parseDouble(attributes.getValue("size"));
            FontWeight weight = FontWeight.findByName(attributes.getValue("weight"));
            // String layer = textNode.getAttribute("layer");

            currentText = new PartText(pos, size, weight);
        } else if (localName.equals("pad")) {
            final String padNumber = attributes.getValue("padName");
            final Point2D padPos = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                               Double.parseDouble(attributes.getValue("y")));

            PartPad pad = new PartPad(padNumber, padPos);
            System.err.println("  " + pad);
            thePackage.addPad(pad);
        } else {
            // unexpected element
            System.err.println("   " + localName);
        }

    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("text")) {
            System.err.println("  " + currentText);
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

    private Part part;
    private BoardLoader bl;

    public PartHandler(BoardLoader loader, Part part) {
        this.part = part;
        this.bl = loader;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("pad")) {
            final String padName = attributes.getValue("padName");
            final String padId = attributes.getValue("id");

            final Pin pin = part.getPad(padName);
            bl.nodes.put(padId, pin);
        } else {
            System.err.println("   " + localName);
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        return localName.equals("part");
    }
}


class NetHandler extends SubContentHandler {
    private Net net;
    private BoardLoader bl;

    public NetHandler(BoardLoader bl, Net net) {
        this.bl = bl;
        this.net = net;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        
        if (localName.equals("junction")) {
            String junctionId = attributes.getValue("id");
            Point2D jPos = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                       Double.parseDouble(attributes.getValue("y")));

            Junction junction = new Junction(net, jPos);
            System.err.printf("  %s\n", junction);

            bl.nodes.put(junctionId, junction);
            // net.addJunction(junction);
        } else if (localName.equals("airwire")) {
            String fromId = attributes.getValue("from");    // unique id
            String toId = attributes.getValue("to");        // unique id

//            AbstractNode from = junctions.get(fromId);
//            if (from == null) from = pads.get(fromId);  // TODO: This is a bad hack!!!!
//            AbstractNode to = junctions.get(toId);
//            if (to == null) to = pads.get(toId);        // TODO: This is a bad hack!!!!

            AbstractNode from = bl.nodes.get(fromId);
            AbstractNode to = bl.nodes.get(toId);

            AirWire aw = new AirWire(from, to, net);
            System.err.printf("  %s\n", aw);

            net.addTrace(aw);
        } else if (localName.equals("trace")) {
            String fromId = attributes.getValue("from");
            String toId = attributes.getValue("to");
            boolean isBridge = Boolean.parseBoolean(attributes.getValue("isBridge"));

//            AbstractNode from = junctions.get(fromId);
//            if (from == null) from = pads.get(fromId);      // TODO: This is a bad hack!!!!
//            AbstractNode to = junctions.get(toId);
//            if (to == null) to = pads.get(toId);          // TODO: This is a bad hack!!!!

            AbstractNode from = bl.nodes.get(fromId);
            AbstractNode to = bl.nodes.get(toId);

//            System.err.printf("  T : %s -> %s\n", from, to);

            Trace t = new Trace(from, to, net);
            if (isBridge) {
                t.setAsBridge();
            }
            System.err.printf("  %s\n", t);

            net.addTrace(t);
        } else {
            System.err.println("   " + localName);
        }
      
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        return localName.equals("net");
    }
}


public class BoardLoader extends DefaultHandler {

    private File sourceFile;
    private SubContentHandler currentHandler;

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
        System.err.println("Loading " + sourceFile.getAbsolutePath());
    }


    @Override
    public void endDocument() throws SAXException {
        System.err.printf("Loaded %s board shape points\n", boardShape.size());
        System.err.printf("Loaded %s Packages\n", packages.size());
        System.err.printf("Loaded %s Parts\n", parts.size());
        System.err.printf("Loaded %s Nets\n", nets.size());
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (currentHandler != null) {
            currentHandler.startElement(uri, localName, qName, attributes);
        } else {
            if (localName.equals("boardShape")) {
                System.err.println("boardshape");
                currentHandler = new BoardShapeHandler(this);
            } else if (localName.equals("package")) {
                final String id = attributes.getValue("id");
                final String name = attributes.getValue("name");

                Package thePackage = new Package(id, name);
                currentHandler = new PackageHandler(thePackage);
                packages.put(thePackage.getId(), thePackage);
            } else if (localName.equals("part")) {
                final String name = attributes.getValue("name");
                final String packageRef = attributes.getValue("packageRef");
                Double rotation = Double.parseDouble(attributes.getValue("rotation"));
                final String value = attributes.getValue("value");
                Point2D partPosition = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                                   Double.parseDouble(attributes.getValue("y")));
                final Package thePackage = packages.get(packageRef);

                Part part = new Part(name, value, thePackage);
                part.setPosition(partPosition);
                part.setRotation(rotation);

                System.err.println(part);
                currentHandler = new PartHandler(this, part);
                parts.add(part);
            } else if (localName.equals("net")) {
                final String name = attributes.getValue("name");
                final Net net = new Net(name);

                currentHandler = new NetHandler(this, net);
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
            InputStream is = new FileInputStream(this.sourceFile);
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

        Board result = new Board();
        nets.forEach(n -> result.addNet(n));
        parts.forEach(p -> result.addPart(p));
        result.boardShapePoints.clear();
        boardShape.forEach(p -> result.boardShapePoints.add(p));
        return result;
    }
}

