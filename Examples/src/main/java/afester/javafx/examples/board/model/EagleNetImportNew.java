package afester.javafx.examples.board.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
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

import afester.javafx.shapes.ArcFactory;
import afester.javafx.shapes.ArcParameters;
import javafx.geometry.Point2D;


// A device is the package and the mapping of pins to the pads on the package
class Device {

    private String name;
    private Package thePackage;

    // Maps gate/pin to pad
    private Map<String, String> connections = new HashMap<>();
    
    public Device(String deviceName, Package pkg) {
        this.name = deviceName;
        this.thePackage = pkg;
    }

    public String getName() {
        return name;
    }

    public Package getPackage() {
        return thePackage;
    }

    public Map<String, String> getPinPadMapping() {
        return connections;
    }

    public void addPinPadMapping(String pinId, String pad) {
        connections.put(pinId, pad);
    }

    @Override
    public String toString() {
        return String.format("Device[name=\"%s\"]", name);
    }

}

// A collection of devices
class DeviceSet {
    private String name;
    private Map<String, Device> devices = new HashMap<>();

    public DeviceSet(String deviceSetName) {
        this.name = deviceSetName;
    }

    public void addDevice(Device device) {
        devices.put(device.getName(), device);
    }

    public Device getDevice(String name) {
        return devices.get(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("DeviceSet[name=\"%s\"]", name);
    }
}


// A collection of devicesets and packages
class Library {
    private String name;        // library name
    private Map<String, Package> packages = new HashMap<>();    // package name => package
    private Map<String, DeviceSet> deviceSets = new HashMap<>();

    public Library(String libraryName) {
        this.name = libraryName;
    }

    public String getName() {
        return name;
    }

    public void addPackage(Package result) {
        packages.put(result.getName(), result);
    }


    public Package getPackage(String packageName) {
        return packages.get(packageName);
    }

    public void addDeviceSet(DeviceSet ds) {
        deviceSets.put(ds.getName(), ds);
    }

    public DeviceSet getDeviceSet(String name) {
        return deviceSets.get(name);
    }


    @Override
    public String toString() {
        return String.format("Library[name=\"%s\"]", name);
    }
}


abstract class SubContentHandler {
    protected SubContentHandler currentHandler = null;

    abstract void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException;
 
    abstract boolean endElement(String uri, String localName, String qName) throws SAXException;

    void characters(char[] ch, int start, int length) {
        if (currentHandler != null) {
            currentHandler.characters(ch,  start, length);
        }
    }
}


class IgnoreHandler extends SubContentHandler {

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
    

    @Override
    public String toString() {
        return String.format("IgnoreHandler[endElement=\"%s\"]", myElement);
    }
    
}

class ConnectHandler extends SubContentHandler {

    private DeviceHandler deviceHandler;

    public ConnectHandler(DeviceHandler deviceHandler) {
        this.deviceHandler = deviceHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("connect")) {
            final String gate = attributes.getValue("gate");
            final String pin = attributes.getValue("pin");
            final String pad = attributes.getValue("pad");

            final String pinId = pin + "@" + gate;
            System.err.printf("        %s => %s\n", pinId, pad);
            deviceHandler.currentDevice.addPinPadMapping(pinId, pad);
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("connects")) {
            return true;
        }

        return false;
    }
  
}

class DeviceHandler extends SubContentHandler {
    public Device currentDevice = null;
    private DeviceSetHandler deviceSetHandler;

    public DeviceHandler(DeviceSetHandler deviceSetHandler) {
        this.deviceSetHandler = deviceSetHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (currentHandler != null) {
            currentHandler.startElement(uri, localName, qName, attributes);
        } else {
            if (localName.equals("device")) {
                final String deviceName = attributes.getValue("name");
                final String packageName = attributes.getValue("package");

                // get the package from the current library
                // TODO: improve API
                Library lib = deviceSetHandler.libHandler.currentLibrary;
                Package pkg = lib.getPackage(packageName);

                currentDevice = new Device(deviceName, pkg);
                System.err.printf("      %s\n", currentDevice);
            } else if (localName.equals("connects")) {
                currentHandler = new ConnectHandler(this);
            } else if (localName.equals("package3dinstances")) {
                currentHandler = new IgnoreHandler("package3dinstances");
            } else if (localName.equals("technologies")) {
                currentHandler = new IgnoreHandler("technologies");
            } else {
                System.err.printf("        %s\n", localName);
            }
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("devices")) {
            return true;
        } else if (localName.equals("device" )) {
            deviceSetHandler.addDevice(currentDevice);
        }
        
        if (currentHandler != null) {
            if (currentHandler.endElement(uri, localName, qName)) {
                currentHandler = null;
            }
        }

        return false;
    }
    
}

class DeviceSetHandler extends SubContentHandler {

    private DeviceSet currentDeviceSet = null;
    public LibraryHandler libHandler;

    public DeviceSetHandler(LibraryHandler libraryHandler) {
        this.libHandler = libraryHandler;
    }

    public void addDevice(Device device) {
        currentDeviceSet.addDevice(device);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (currentHandler != null) {
            currentHandler.startElement(uri, localName, qName, attributes);
        } else {
    
            if (localName.equals("deviceset")) {
                final String deviceSetName = attributes.getValue("name");
                currentDeviceSet = new DeviceSet(deviceSetName);
                System.err.printf("    %s\n", currentDeviceSet);
            } else if (localName.equals("devices")) {
                currentHandler = new DeviceHandler(this);
            }
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("devicesets")) {
            return true;
        } else if (localName.equals("deviceset" )) {
            libHandler.addDeviceset(currentDeviceSet);
        }
        
        if (currentHandler != null) {
            if (currentHandler.endElement(uri, localName, qName)) {
                currentHandler = null;
            }
        }

        return false;
    }
}


class PackageHandler extends SubContentHandler {

    private Package result;
    private PartText currentText = null;
    private LibraryHandler libHandler;

    public PackageHandler(LibraryHandler libraryHandler) {
        this.libHandler = libraryHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("package")) {
            final String packageName = attributes.getValue("name");
            System.err.printf("    <package name=\"%s\"\n", packageName);

            result = new Package(packageName);
        } else if (localName.equals("pad") ){
            final String padName = attributes.getValue("name");
            final Point2D padPos = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                               -Double.parseDouble(attributes.getValue("y")));
            // padNode.getAttribute("drill");
            // padNode.getAttribute("shape");
            // padNode.getAttribute("diameter");
            // System.err.printf("      <pad name=\"%s\" pos=%s>\n", padName, padPos); // Note: padPos is local to package!

            final PartPad pad = new PartPad(padName, padPos);
            System.err.printf("      %s\n", pad);
            result.addPad(pad);
        } else if (localName.equals("smd") ){
            final String padName = attributes.getValue("name");
            final Point2D padPos = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                               -Double.parseDouble(attributes.getValue("y")));
            //Double padDx = Double.parseDouble(smdPadNode.getAttribute("dx"));
            //Double padDy = -Double.parseDouble(smdPadNode.getAttribute("dy"));

            // System.err.printf("      <smd name=%s pos=%s>\n", padName, padPos);

            final PartPad pad = new PartPad(padName, padPos); 
            System.err.printf("      %s\n", pad);
            result.addPad(pad);
        } else if (localName.equals("wire") ){
            final Point2D p1 = new Point2D(Double.parseDouble(attributes.getValue("x1")),
                                           -Double.parseDouble(attributes.getValue("y1")));
            final Point2D p2 = new Point2D(Double.parseDouble(attributes.getValue("x2")),
                                           -Double.parseDouble(attributes.getValue("y2")));

            final Double width = Double.parseDouble(attributes.getValue("width"));
            // final String layer = attributes.getValue("layer");
            
            // if the "curve" attribute is defined, an arc is rendered instead of the line
            final String curveAttr = attributes.getValue("curve");
            
            PartShape wireShape;
            if (curveAttr != null && !curveAttr.isEmpty()) {
                final double alpha = Double.parseDouble(curveAttr);

                // NOTE: -alpha is required due to the transformation of the y coordinate!
                // System.err.printf("      <wire p1=%s p2=%s width=%s layer=%s curve=%s>\n", p1, p2, width, layer, alpha);
                ArcParameters ap = ArcFactory.arcFromPointsAndAngle(p1,  p2, -alpha);
                wireShape = new PartArc(ap.getCenter(), ap.getRadius(), ap.getStartAngle(),
                                        ap.getLength(), width, ap.getColor());
            } else {
                // System.err.printf("      <wire p1=%s p2=%s width=%s layer=%s>\n", p1, p2, width, layer);
                wireShape = new PartLine(p1, p2, width);
            }
            System.err.printf("      %s\n", wireShape);
            result.addShape(wireShape);
            
        } else if (localName.equals("rectangle")) {
            final Point2D p1 = new Point2D(Double.parseDouble(attributes.getValue("x1")),
                                           -Double.parseDouble(attributes.getValue("y1")));
            final Point2D p2 = new Point2D(Double.parseDouble(attributes.getValue("x2")),
                                           -Double.parseDouble(attributes.getValue("y2")));
            // final String layer = attributes.getValue("layer");

            // System.err.printf("      <rectangle p1=%s p2=%s layer=%s>\n", p1, p2, layer);
            PartShape rectangle = new PartRectangle(p1, p2);
            result.addShape(rectangle);
            System.err.printf("      %s\n", rectangle);
        } else if (localName.equals("circle")) {
            final Point2D center = new Point2D(Double.parseDouble(attributes.getValue("x")),
                                               -Double.parseDouble(attributes.getValue("y")));
            final Double radius = Double.parseDouble(attributes.getValue("radius"));
            final Double width = Double.parseDouble(attributes.getValue("width"));

            System.err.printf("      <circle c=%s r=%s width=%s>\n", center, radius, width);
            result.addShape(new PartCircle(center, radius, width));
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
            // final String layer = attributes.getValue("layer");

//            String text = textNode.getTextContent();
//            if (text.equals(">NAME")) {
//                text = partName;
//            }
//            if (text.equals(">VALUE") && partValue != null) {
//                text = partValue;
//            }

            currentText = new PartText(textPos, size * ratio / 10);
        } else if (localName.equals("description")) {
            // ignored
        } else {
            System.err.println("      " + localName);
        }
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("packages")) {
            return true;
        } else if (localName.equals("package")) {
            System.err.printf("    %s\n", result);
            libHandler.addPackage(result);
        } else if (localName.equals("text")) {
            System.err.println("      " + currentText);
            currentText = null;
        }
        return false;
    }


    @Override
    public void characters(char[] ch, int start, int length) {
        if (currentText != null) {
            currentText.append(new String(ch, start, length));
        }
    }

}


class LibraryHandler extends SubContentHandler {

    private EagleSchematicHandler mainHandler;
    public Library currentLibrary = null;

    public LibraryHandler(EagleSchematicHandler eagleSchematicHandler) {
        mainHandler = eagleSchematicHandler;
    }


    public void addDeviceset(DeviceSet ds) {
        currentLibrary.addDeviceSet(ds);
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (currentHandler != null) {
            currentHandler.startElement(uri, localName, qName, atts);
        } else {

            if (localName.equals("library")) {
                final String libraryName = atts.getValue("name");
                currentLibrary = new Library(libraryName);
                System.err.printf("  %s\n", currentLibrary);    
            } else if (localName.equals("packages")) {
                currentHandler = new PackageHandler(this);
            } else if (localName.equals("symbols") || localName.equals("packages3d")) {
                currentHandler = new IgnoreHandler(localName);
            } else if (localName.equals("devicesets")) {
                currentHandler = new DeviceSetHandler(this);
            }
        }
    }
    

    public void addPackage(Package result) {
        currentLibrary.addPackage(result);
    }


    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("libraries")) {
            return true;
        } else if (localName.equals("library")) {
            System.err.printf("  Loaded %s\n", currentLibrary);
            mainHandler.addLibrary(currentLibrary);
        }

        if (currentHandler != null) {
            if (currentHandler.endElement(uri, localName, qName)) {
                currentHandler = null;
            }
        }

        return false;
    }

}

class PartHandler extends SubContentHandler {

    private EagleSchematicHandler mainHandler;

    public PartHandler(EagleSchematicHandler eagleSchematicHandler) {
        this.mainHandler = eagleSchematicHandler;
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (localName.equals("part")) {

            String partName = atts.getValue("name");        // unique id (reference designator)
            String partValue = atts.getValue("value");      // might be null - then we can use "deviceset" as value

            String partLibrary = atts.getValue("library");
            String partDeviceSet = atts.getValue("deviceset");
            String partDevice = atts.getValue("device");

            System.err.printf("  <part name=\"%s\" library=\"%s\" deviceset=\"%s\" device=\"%s\" value=\"%s\">\n", partName, partLibrary, partDeviceSet, partDevice, partValue);

            // get reference to package
            Library lib = mainHandler.getLibrary(partLibrary);
            // System.err.println("    " + lib);
            DeviceSet deviceSet = lib.getDeviceSet(partDeviceSet);
            // System.err.println("    " + deviceSet);
            Device dev = deviceSet.getDevice(partDevice);

            // NOTE: Device connects the package and the part by the Pin/Pad mapping!
            // In other words, we need the pin/pad mapping to later reference the proper pad from the net list!

            // System.err.println("    " + dev);

            Package partPackage = dev.getPackage();
            if (partPackage != null)  {
                Map<String, String> pinPadMapping = dev.getPinPadMapping();
                System.err.printf("    %s\n", partPackage);
                System.err.printf("    %s\n", pinPadMapping);
                Part part = new Part(partName, partValue, partPackage);
                partPackage.getPads().forEach(pad -> part.addPad(new Pad(part, pad)));
                mainHandler.addPart(part, pinPadMapping);
            } else {
                System.err.printf("    Ignored %s since it does not have a package\n", partName);
            }
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

class NetHandler extends SubContentHandler {
    private EagleSchematicHandler mainHandler;
    private SheetHandler sheetHandler;
    private final List<Pad> padList = new ArrayList<>();

    public NetHandler(SheetHandler sh, EagleSchematicHandler mainHandler) {
        this.sheetHandler = sh;
        this.mainHandler = mainHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("pinref")) {
            final String part = attributes.getValue("part");
            final String gate = attributes.getValue("gate");
            final String pin = attributes.getValue("pin");

            Part p = mainHandler.getPart(part);
            if (p == null) {
                System.err.printf("    Ignoring <pinref part=\"%s\" gate=\"%s\" pin=\"%s\"> since part has no package\n", part, gate, pin);
            } else {
                // Map the pin reference for the current part to a Pad name in the current part
                Map<String, String> pinPadMappings = mainHandler.getPinPadMapping(p);
                String padName = pinPadMappings.get(pin + "@" + gate);

                // Here we need the "global" Pad as a graph node, not the pad template from the Package! 
                Pad pad = p.getPad(padName);
                System.err.printf("    %s\n", pad);

                padList.add(pad);
            }
        }        
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("net")) {
            Pad p1 = null;
            for (Pad p2 : padList) {
                if (p1 != null) {
                    // TODO: can we simply add the Pads to the net here and afterwards to a "Reset Net" to create the actual AirWires??
                    sheetHandler.currentNet.addTrace(new AirWire(p1, p2, sheetHandler.currentNet));
                }
                p1 = p2;
            }

            return true;
        }

        return false;
    }
    
}

class SheetHandler extends SubContentHandler {
    private EagleSchematicHandler mainHandler;
    public Net currentNet;

    public SheetHandler(EagleSchematicHandler eagleSchematicHandler) {
        this.mainHandler = eagleSchematicHandler;
    }

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

                Part part = mainHandler.getPart(partName);
                if (part != null) {
                    part.setPosition(pos);
                } else {
                    System.err.printf("WARNING: %s not found in sheet\n", partName);
                }
            } else if (localName.equals("bus")) {
               
            } else if (localName.equals("net")) {
                final String netName = attributes.getValue("name");
                System.err.printf("  <net name=\"%s\">\n", netName);
                currentNet = new Net(netName);
                mainHandler.addNet(currentNet);
                currentHandler = new NetHandler(this, mainHandler);
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
    private final Map<String, Part> parts = new HashMap<>();
    private final List<Net> nets = new ArrayList<>();
    private final Map<Part, Map<String, String>> pinPadMappings = new HashMap<>();
    private final Map<String, Library> libraries = new HashMap<>();

    private SubContentHandler currentHandler = null;

    public List<Net> getNets() {
        return nets; 
    }

    public Collection<Part> getParts() {
        return parts.values();
    }

    public void addPart(Part part, Map<String, String> pinPadMapping) {
        pinPadMappings.put(part, pinPadMapping);
        parts.put(part.getName(), part);
    }

    public void addNet(Net net) {
        nets.add(net);
    }

    public Library getLibrary(String name) {
        return libraries.get(name);
    }

    public void addLibrary(Library lib) {
        libraries.put(lib.getName(), lib);
    }

    public Part getPart(String partName) {
        return parts.get(partName);
    }

    public Map<String, String> getPinPadMapping(Part part) {
        return pinPadMappings.get(part);
    }

    @Override
    public void startDocument() throws SAXException {
        System.err.println("Start document");
    }

    @Override
    public void endDocument() throws SAXException {
        System.err.println("End document");
        System.err.printf("Loaded %s package libraries.\n", libraries.size());

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (currentHandler != null) {
            currentHandler.startElement(uri, localName, qName, atts);
        } else {
            if (localName.equals("libraries")) {
                currentHandler = new LibraryHandler(this);
            } else if (localName.equals("parts")) {
                currentHandler = new PartHandler(this);
            } else if (localName.equals("sheets")) {
                currentHandler = new SheetHandler(this);
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
            EagleSchematicHandler handler = new EagleSchematicHandler();
            xmlReader.setContentHandler(handler);
            InputStream is = new FileInputStream(schematicFile);
            xmlReader.parse(new InputSource(is));

            Collection<Part> parts = handler.getParts();
            System.err.printf("Imported %s parts:\n", parts.size());
            parts.forEach(p -> System.err.println("    " + p));
            parts.forEach(p -> board.addPart(p));

            List<Net> nets = handler.getNets();
            System.err.printf("Imported %s nets:\n", nets.size());
            nets.forEach(net -> System.err.println("    " + net));
            nets.forEach(net -> board.addNet(net));
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
