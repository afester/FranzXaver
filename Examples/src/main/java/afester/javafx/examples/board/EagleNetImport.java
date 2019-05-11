package afester.javafx.examples.board;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import afester.javafx.shapes.ArcFactory;
import afester.javafx.shapes.ArcParameters;
import javafx.geometry.Point2D;



public class EagleNetImport extends NetImport {

    private XPath xPath = XPathFactory.newInstance().newXPath();
    private Document doc = null;
    // private Map<String, Part> packages = new HashMap<>();

    @Override
	public Board importFile(File file) {
        Board board = new Board();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dbFactory.setValidating(false);
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file);

            // load all parts
            NodeList partNodes = (NodeList) xPath.evaluate("/eagle/drawing/schematic/parts/part", doc, XPathConstants.NODESET);
            for (int i = 0; i < partNodes.getLength(); ++i) {
                Element part = (Element) partNodes.item(i);
                String partName = part.getAttribute("name");
                String partValue = part.getAttribute("value");
                String partLibrary = part.getAttribute("library");
                String partDeviceSet = part.getAttribute("deviceset");
                String partDevice = part.getAttribute("device");

                // try to determine a reasonable position
                // The idea is to make the initial layout similar to the schematic so that the parts can easily be
                // located. Also, the assumption is that the schematic is already somewhat formatted. 
                Double xpos = Double.valueOf(0);
                Double ypos = Double.valueOf(0);
                NodeList partInstances = (NodeList) xPath.evaluate("/eagle/drawing/schematic/sheets/sheet/instances/instance[@part='"+partName+"']", doc, XPathConstants.NODESET);
                if (partInstances.getLength() > 0) {
                    Element partInstance = (Element) partInstances.item(0);   // take first if more than one gate is available on the sheet
                    
                    String gate = partInstance.getAttribute("gate");
                    xpos = Double.parseDouble(partInstance.getAttribute("x"));
                    ypos = -Double.parseDouble(partInstance.getAttribute("y"));

                    System.err.printf("PI: %s %s/%s\n", gate, xpos, ypos);
                }

                System.err.printf("Part: %s - library: \"%s\", deviceset: \"%s\", device: \"%s\"\n", partName, partLibrary, partDeviceSet, partDevice);

                // load the device
                String deviceSelector = "/eagle/drawing/schematic/libraries/library[@name='"+ partLibrary + 
                        "']/devicesets/deviceset[@name='"+ partDeviceSet + 
                        "']/devices/device[@name='"+partDevice+"']";
                Element deviceNode = (Element) xPath.evaluate(deviceSelector, doc, XPathConstants.NODE);
                String packageRef = deviceNode.getAttribute("package");
                System.err.println("   Package:" + packageRef);

                // load the package
                Part pkg = createPart(partName, partValue, partLibrary, packageRef, deviceNode);
                if (pkg != null) {
                    pkg.setLayoutX(xpos);
                    pkg.setLayoutY(ypos);
                    board.addDevice(pkg);
                }
            }

            // load all nets and the pin references
            NodeList nodes = (NodeList) xPath.evaluate("/eagle/drawing/schematic/sheets/sheet/nets/net", doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); ++i) {
                Element netNode = (Element) nodes.item(i);
                String netName = netNode.getAttribute("name");

                // get all pin references for the net
                List<Pad> padList = new ArrayList<>();
                NodeList pinrefNodes = (NodeList) xPath.evaluate("./segment/pinref", netNode, XPathConstants.NODESET);
                for (int j = 0; j < pinrefNodes.getLength(); ++j) {
                    Element pinref = (Element) pinrefNodes.item(j);
                    String partName = pinref.getAttribute("part");
                    String gate = pinref.getAttribute("gate");
                    String pin = pinref.getAttribute("pin");

                    // if there was no package earlier, there is now also no part ...
                    Part p = board.getDevice(partName);
                    if (p != null) {
                        Pad pad = p.getPad(pin + "@" + gate);
                        if (pad == null) {
                            System.err.printf("WARNING: Pad %s not found in part %s!\n", pin+"@"+gate, partName);
                        } else {
                            System.err.printf("  %s %s\n", partName, pad);
                            padList.add(pad);    
                        }
                    }

                }

                // Create a new net and connect all pads through an AirWire
                Net net = new Net(netName);
                Pad p1 = null;
                for (Pad p2 : padList) {
                    if (p1 != null) {
                        net.addTrace(new AirWire(p1, p2));
                    }
                    p1 = p2;
                }

                board.addNet(net);
            }
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return board;
    }


    int arcCount = 0;
    /**
     * Returns a device package as a JafaFX node.
     * 
     * @param partLibrary
     * @param packageRef
     * @param connectNodes 
     * @return
     * @throws XPathExpressionException
     */
    private Part createPart(String partName, String partValue, String partLibrary, String packageRef, Element connects) throws XPathExpressionException {

        // select the package for the given part
        String packageSelector = "/eagle/drawing/schematic/libraries/library[@name='"+ partLibrary + 
                "']/packages/package[@name='"+ packageRef + "']";
        Element packageNode = (Element) xPath.evaluate(packageSelector, doc, XPathConstants.NODE);

        // if there is no package for the part, there is nothing we can put onto the board
        if (packageNode == null) {
            System.err.println("WARNING: NO PACKAGE FOR " + partName);
            return null;
        }

        Part part = new Part(partName, partValue, packageRef);

        // load through-hole pads
        NodeList padNodes = (NodeList) xPath.evaluate("./pad", packageNode, XPathConstants.NODESET);
        for (int j = 0; j < padNodes.getLength(); ++j) {
            Element padNode = (Element) padNodes.item(j);
            String pinNumber = padNode.getAttribute("name");
            Double padX = Double.parseDouble(padNode.getAttribute("x"));
            Double padY = -Double.parseDouble(padNode.getAttribute("y"));
            // Double drill = Double.parseDouble(padNode.getAttribute("drill"));
            // padNode.getAttribute("shape");

            final String xpathQuery = "./connects/connect[@pad='" + pinNumber + "']";
            Element connect = (Element) xPath.evaluate(xpathQuery, connects, XPathConstants.NODE);
            // A Pad connection might not exist at all!
            if (connect != null) {
    
                // logical pin names - referenced by the nets
                String gate = connect.getAttribute("gate");
                String pin = connect.getAttribute("pin");
                System.err.printf("   Pad %s <=> Pin %s@%s\n", pinNumber, gate, pin);
    
                // Model
                part.addPad(new Pad(part, pinNumber, padX, padY), pin + "@" + gate);
            }
        }

        
        // load SMD pads
        NodeList smdPadNodes = (NodeList) xPath.evaluate("./smd", packageNode, XPathConstants.NODESET);
        for (int j = 0; j < smdPadNodes.getLength(); ++j) {
            Element smdPadNode = (Element) smdPadNodes.item(j);
            String pinNumber = smdPadNode.getAttribute("name");
            Double padX = Double.parseDouble(smdPadNode.getAttribute("x"));
            Double padY = -Double.parseDouble(smdPadNode.getAttribute("y"));
            Double padDx = Double.parseDouble(smdPadNode.getAttribute("dx"));
            Double padDy = -Double.parseDouble(smdPadNode.getAttribute("dy"));

            final String xpathQuery = "./connects/connect[@pad='" + pinNumber + "']";
            Element connect = (Element) xPath.evaluate(xpathQuery, connects, XPathConstants.NODE);

            // logical pin names - referenced by the nets
            String gate = connect.getAttribute("gate");
            String pin = connect.getAttribute("pin");
            System.err.printf("   SMD %s <=> Pin %s@%s\n", pinNumber, gate, pin);

            // Model
            part.addPad(new Pad(part, pinNumber, padX, padY), pin + "@" + gate);
        }

        NodeList wireNodes = (NodeList) xPath.evaluate("./wire", packageNode, XPathConstants.NODESET);
        for (int j = 0; j < wireNodes.getLength(); ++j) {
            Element wireNode = (Element) wireNodes.item(j);
            
            Point2D p1 = new Point2D(Double.parseDouble(wireNode.getAttribute("x1")),
                    				 -Double.parseDouble(wireNode.getAttribute("y1")));
            Point2D p2 = new Point2D(Double.parseDouble(wireNode.getAttribute("x2")),
                    				 -Double.parseDouble(wireNode.getAttribute("y2")));

            Double width = Double.parseDouble(wireNode.getAttribute("width"));
            String layer = wireNode.getAttribute("layer");

            // if the "curve" attribute is defined, an arc is rendered instead of the line
            String curveAttr = wireNode.getAttribute("curve");
            if (curveAttr != null && !curveAttr.isEmpty()) {
                final double alpha = Double.parseDouble(curveAttr);
                // NOTE: -alpha is required due to the transformation of the y coordinate!
            	ArcParameters ap = ArcFactory.arcFromPointsAndAngle(p1,  p2, -alpha);
                part.addShape(new PartArc(ap.getCenter(), ap.getRadius(), ap.getStartAngle(),
                                          ap.getLength(), width, ap.getColor()));
            } else {
                part.addShape(new PartLine(p1, p2, width));
            }
        }

        NodeList rectNodes = (NodeList) xPath.evaluate("./rectangle", packageNode, XPathConstants.NODESET);
        for (int j = 0; j < rectNodes.getLength(); ++j) {
            Element rectNode = (Element) rectNodes.item(j);
            Point2D p1 = new Point2D(Double.parseDouble(rectNode.getAttribute("x1")),
                                     -Double.parseDouble(rectNode.getAttribute("y1")));
            Point2D p2 = new Point2D(Double.parseDouble(rectNode.getAttribute("x2")),
                                     -Double.parseDouble(rectNode.getAttribute("y2")));
            String layer = rectNode.getAttribute("layer");

            part.addShape(new PartRectangle(p1, p2));
        }

        NodeList circleNodes = (NodeList) xPath.evaluate("./circle", packageNode, XPathConstants.NODESET);
        for (int j = 0; j < circleNodes.getLength(); ++j) {
            Element circleNode = (Element) circleNodes.item(j);
            Point2D center = new Point2D(Double.parseDouble(circleNode.getAttribute("x")),
                                         -Double.parseDouble(circleNode.getAttribute("y")));
            Double radius = Double.parseDouble(circleNode.getAttribute("radius"));
            Double width = Double.parseDouble(circleNode.getAttribute("width"));

            part.addShape(new PartCircle(center, radius, width));
        }

        NodeList textNodes = (NodeList) xPath.evaluate("./text", packageNode, XPathConstants.NODESET);
        for (int j = 0; j < textNodes.getLength(); ++j) {
            Element textNode = (Element) textNodes.item(j);
            Double x = Double.parseDouble(textNode.getAttribute("x"));
            Double y = -Double.parseDouble(textNode.getAttribute("y"));
            Double size = Double.parseDouble(textNode.getAttribute("size"));
            
            Double ratio = 1.0;
            try {
                ratio = Double.parseDouble(textNode.getAttribute("ratio"));
            } catch(NumberFormatException nfe) {
                // intentionally left blank - default value is 1.0
            }
            String layer = textNode.getAttribute("layer");
            String text = textNode.getTextContent();

            if (text.equals(">NAME")) {
                text = partName;
            }
            if (text.equals(">VALUE") && partValue != null) {
                text = partValue;
            }

            part.addShape(new PartText(x, y, text, size * ratio / 10));
        }

        part.createNode();
        return part;
    }
}
