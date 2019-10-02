package afester.javafx.examples.board.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javafx.geometry.Point2D;

interface SubContentHandler {

    void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException;

    boolean endElement(String uri, String localName, String qName) throws SAXException;
}


class IgnoreHandler implements SubContentHandler {

    private String myElement;
    
    public IgnoreHandler(String localName) {
        myElement = localName;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals(myElement)) {
            return true;
        }
        return false;
    }
}

class DeviceHandler implements SubContentHandler {

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // TODO Auto-generated method stub
        if (localName.equals("device")) {
            final String deviceName = attributes.getValue("name");
            final String packageName = attributes.getValue("package");
            System.err.printf("      <device name=\"%s\" package=\"%s\">\n", deviceName, packageName);
        } else {
            System.err.printf("        %s\n", localName);
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("devices")) {
            return true;
        }
        return false;
    }
    
}

class DeviceSetHandler implements SubContentHandler {
    private SubContentHandler currentHandler = null;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (currentHandler != null) {
            currentHandler.startElement(uri, localName, qName, attributes);
        } else {
    
            if (localName.equals("deviceset")) {
                final String deviceSetName = attributes.getValue("name");
                System.err.printf("    <deviceset name=\"%s\">\n", deviceSetName);
            } else if (localName.equals("devices")) {
                currentHandler = new DeviceHandler();
            }
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("devicesets")) {
            return true;
        }
        
        if (currentHandler != null) {
            if (currentHandler.endElement(uri, localName, qName)) {
                currentHandler = null;
            }
        }

        return false;
    }
}


class PackageHandler implements SubContentHandler {

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("package")) {
            final String packageName = attributes.getValue("name");
            System.err.printf("    <package name=\"%s\">\n", packageName);
        } else if (localName.equals("pad") ){
            final String padName = attributes.getValue("name");
            final Point2D padPos = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                               -Double.parseDouble(attributes.getValue("y")));
            // padNode.getAttribute("drill");
            // padNode.getAttribute("shape");
            // padNode.getAttribute("diameter");
            System.err.printf("      <pad name=%s pos=%s>\n", padName, padPos);
        } else if (localName.equals("smd") ){
            final String padName = attributes.getValue("name");
            final Point2D padPos = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                               -Double.parseDouble(attributes.getValue("y")));
            //Double padDx = Double.parseDouble(smdPadNode.getAttribute("dx"));
            //Double padDy = -Double.parseDouble(smdPadNode.getAttribute("dy"));

            System.err.printf("      <smd name=%s pos=%s>\n", padName, padPos);
        } else if (localName.equals("wire") ){
            final Point2D p1 = new Point2D(Double.parseDouble(attributes.getValue("x1")),
                                           -Double.parseDouble(attributes.getValue("y1")));
            final Point2D p2 = new Point2D(Double.parseDouble(attributes.getValue("x2")),
                                           -Double.parseDouble(attributes.getValue("y2")));

            final Double width = Double.parseDouble(attributes.getValue("width"));
            final String layer = attributes.getValue("layer");
            
            // if the "curve" attribute is defined, an arc is rendered instead of the line
            final String curveAttr = attributes.getValue("curve");
            if (curveAttr != null && !curveAttr.isEmpty()) {
                final double alpha = Double.parseDouble(curveAttr);

                // NOTE: -alpha is required due to the transformation of the y coordinate!
                System.err.printf("      <wire p1=%s p2=%s width=%s layer=%s curve=%s>\n", p1, p2, width, layer, alpha);
                // ArcParameters ap = ArcFactory.arcFromPointsAndAngle(p1,  p2, -alpha);
                //part.addShape(new PartArc(ap.getCenter(), ap.getRadius(), ap.getStartAngle(),
                //                          ap.getLength(), width, ap.getColor()));
            } else {
                System.err.printf("      <wire p1=%s p2=%s width=%s layer=%s>\n", p1, p2, width, layer);
                // part.addShape(new PartLine(p1, p2, width));
            }
        } else if (localName.equals("rectangle")) {
            final Point2D p1 = new Point2D(Double.parseDouble(attributes.getValue("x1")),
                                           -Double.parseDouble(attributes.getValue("y1")));
            final Point2D p2 = new Point2D(Double.parseDouble(attributes.getValue("x2")),
                                           -Double.parseDouble(attributes.getValue("y2")));
            final String layer = attributes.getValue("layer");

            System.err.printf("      <rectangle p1=%s p2=%s layer=%s>\n", p1, p2, layer);
            // part.addShape(new PartRectangle(p1, p2));
        } else if (localName.equals("circle")) {
            final Point2D center = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                               -Double.parseDouble(attributes.getValue("y")));
            final Double radius = Double.parseDouble(attributes.getValue("radius"));
            final Double width = Double.parseDouble(attributes.getValue("width"));

            System.err.printf("      <circle c=%s r=%s width=%s>\n", center, radius, width);
            // part.addShape(new PartCircle(center, radius, width));
        } else if (localName.equals("text")) {
            final Point2D textPos = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                                -Double.parseDouble(attributes.getValue("y")));
            final Double size = Double.parseDouble(attributes.getValue("size"));
            
            Double ratio = 1.0;
            try {
                final String value = attributes.getValue("ratio");
                if (value != null) {
                    ratio = Double.parseDouble(value);
                }
            } catch(NumberFormatException nfe) {
                // intentionally left blank - default value is 1.0
            }
            final String layer = attributes.getValue("layer");

//            String text = textNode.getTextContent();
//            if (text.equals(">NAME")) {
//                text = partName;
//            }
//            if (text.equals(">VALUE") && partValue != null) {
//                text = partValue;
//            }
            System.err.printf("      <text pos=%s size=%s ratio=%s layer=%s>\n", textPos, size, ratio, layer);
        }else {
            System.err.println("      " + localName);
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("packages")) {
            return true;
        }
        return false;
    }
}


class LibraryHandler implements SubContentHandler {
    private SubContentHandler currentHandler = null;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (currentHandler != null) {
            currentHandler.startElement(uri, localName, qName, atts);
        } else {
            System.err.println("  <library>Start Element: " + localName);

            if (localName.equals("packages")) {
                currentHandler = new PackageHandler();
            } else if (localName.equals("symbols") || localName.equals("packages3d")) {
                currentHandler = new IgnoreHandler(localName);
            } else if (localName.equals("devicesets")) {
                currentHandler = new DeviceSetHandler();
            }
        }
    }
    

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("libraries")) {
            return true;
        }

        if (currentHandler != null) {
            if (currentHandler.endElement(uri, localName, qName)) {
                currentHandler = null;
            }
        }

        return false;
    }

}

class PartHandler implements SubContentHandler {

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (localName.equals("part")) {

            String partName = atts.getValue("name");        // unique id (reference designator)
            String partValue = atts.getValue("value");      // might be null
            String partLibrary = atts.getValue("library");
            String partDeviceSet = atts.getValue("deviceset");
            String partDevice = atts.getValue("device");
            System.err.printf("    <part name=%s library=%s deviceset=%s device=%s value=%s>\n", partName, partLibrary, partDeviceSet, partDevice, partValue);
        }
    }
    

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("parts")) {
            return true;
        }

        return false;
    }

}

class NetHandler implements SubContentHandler {

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("pinref")) {
            final String part = attributes.getValue("part");
            final String gate = attributes.getValue("gate");
            final String pin = attributes.getValue("pin");

            System.err.printf("    <pinref part=\"%s\" gate=\"%s\" pin=\"%s\">\n", part, gate, pin);
        }        
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("net")) {
            return true;
        }

        return false;
    }
    
}

class SheetHandler implements SubContentHandler {

    private SubContentHandler currentHandler = null;
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (currentHandler != null) {
            currentHandler.startElement(uri, localName, qName, attributes);
        } else {
            if (localName.equals("instance")) {
                final String partName = attributes.getValue("part");        // unique id (reference designator)
                final Point2D pos = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                                -Double.parseDouble(attributes.getValue("y")));
    
                System.err.printf("  <instance part=\"%s\" pos=%s>\n", partName, pos);
            } else if (localName.equals("bus")) {
               
            } else if (localName.equals("net")) {
                final String netName = attributes.getValue("name");
                System.err.printf("  <net name=\"%s\">\n", netName);
                currentHandler = new NetHandler();
            }
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("sheets")) {
            return true;
        }
        
        if (currentHandler != null) {
            if (currentHandler.endElement(uri, localName, qName)) {
                currentHandler = null;
            }
        }

        return false;
    }
}

class EagleSchematicHandler extends DefaultHandler {

    private SubContentHandler currentHandler = null;
    
    @Override
    public void startDocument() throws SAXException {
        System.err.println("Start document");
    }

    @Override
    public void endDocument() throws SAXException {
        System.err.println("End document");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (currentHandler != null) {
            currentHandler.startElement(uri, localName, qName, atts);
        } else {
            if (localName.equals("libraries")) {
                currentHandler = new LibraryHandler();
            } else if (localName.equals("parts")) {
                currentHandler = new PartHandler();
            } else if (localName.equals("sheets")) {
                currentHandler = new SheetHandler();
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
    
}

public class EagleNetImportNew extends NetImport {

    private File schematicFile = null;

    public EagleNetImportNew(File file) {
        schematicFile = file;
    }

    @Override
	public void importFile(Board board) {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(false);
        spf.setNamespaceAware(true);

        try {
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(new EagleSchematicHandler());
            InputStream is = new FileInputStream(schematicFile);
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
    }
}
