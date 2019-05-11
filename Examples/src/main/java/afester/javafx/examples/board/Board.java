package afester.javafx.examples.board;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Board {

    private Map<String, Part> parts = new HashMap<>();
    private Map<String, Net> nets = new HashMap<>();
    private String schematicFile;
    private File boardFile;

    public void addDevice(Part pkg) {
        parts.put(pkg.getName(), pkg);
    }

    public Map<String, Part> getParts() {
        return parts;
    }

    public Map<String, Net> getNets() {
        return nets;
    }

    public Part getDevice(String partName) {
        return parts.get(partName);
    }

    public void addNet(Net net) {
        nets.put(net.getName(), net);
    }

    public class IntVal {
        public int val = 0;
    }
    

    public void saveAs(File file) {
    }

    public void save() {
        System.err.println("Saving " + boardFile.getAbsolutePath());

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        dbFactory.setValidating(false);
        try {
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // The root node represents the complete board
            Element rootNode = doc.createElement("breadboard");
            rootNode.setAttribute("schematic", schematicFile);
//            rootNode.setAttribute("width",  Double.toString(getWidth()));
//            rootNode.setAttribute("height", Double.toString(getHeight()));
            doc.appendChild(rootNode);

            IntVal junctionId = new IntVal();
            parts.forEach( (k, v) -> {
                Element partNode = v.getXml(doc, junctionId);
                rootNode.appendChild(partNode);
            });

            nets.forEach((netName, net) -> {
                Element netNode = doc.createElement("net");
                netNode.setAttribute("name", net.getName());

                // Junctions are just points which connect traces WITHIN a net
                for (Junction j : net.getJunctions()) {
                    j.setId(junctionId.val++);
                    Node junctionNode = j.getXML(doc);
                    netNode.appendChild(junctionNode);
                }

                // Traces are direct lines which connect two Junctions and/or Pads
                for (AbstractWire t : net.getTraces()) {
                    Node traceNode = t.getXML(doc);
                    netNode.appendChild(traceNode);
                }

                rootNode.appendChild(netNode);
            });

            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            FileOutputStream fos = new FileOutputStream(boardFile);
            Writer out = new OutputStreamWriter(fos, "UTF-8");
            tf.transform(new DOMSource(doc), new StreamResult(out));
            out.close();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.err.println("Saved " + boardFile.getAbsolutePath());
    }

    public void load(File file) {
        System.err.println("Loading " + file.getAbsolutePath());
        boardFile = file;

        XPath xPath = XPathFactory.newInstance().newXPath();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setValidating(false);
        try {
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            
            Element breadboardNode = (Element) xPath.evaluate("/breadboard", doc, XPathConstants.NODE);
            //String widthAttr = breadboardNode.getAttribute("width");
            //String heightAttr = breadboardNode.getAttribute("height");
            schematicFile = breadboardNode.getAttribute("schematic");

            //width = Double.parseDouble(widthAttr);
            //height = Double.parseDouble(heightAttr);

            Map<String, Junction> junctions = new HashMap<>();
            Map<String, Pad> pads = new HashMap<>();

            NodeList partNodes = (NodeList) xPath.evaluate("part", breadboardNode, XPathConstants.NODESET);
            for (int i = 0; i < partNodes.getLength(); ++i) {
                Element partNode = (Element) partNodes.item(i);
                String partName = partNode.getAttribute("name");
                String partValue = partNode.getAttribute("value");
                String packageRef = partNode.getAttribute("package");
                Double rotation = Double.parseDouble(partNode.getAttribute("rotation"));
                Double xpos = Double.parseDouble(partNode.getAttribute("x"));
                Double ypos = Double.parseDouble(partNode.getAttribute("y"));

                Part part = new Part(partName, partValue, packageRef);
                part.setLayoutX(xpos);
                part.setLayoutY(ypos);
                part.setRotation(rotation);

                NodeList padNodes = (NodeList) xPath.evaluate("./pad", partNode, XPathConstants.NODESET);
                for (int j = 0; j < padNodes.getLength(); ++j) {
                    Element padNode = (Element) padNodes.item(j);
                    String pinNumber = padNode.getAttribute("pinNumber");
                    String padId = padNode.getAttribute("id");
                    Double padX = Double.parseDouble(padNode.getAttribute("x"));
                    Double padY = Double.parseDouble(padNode.getAttribute("y"));

                    Pad junction = new Pad(part, pinNumber, padX, padY);
                    pads.put(padId, junction);
                    part.addPad(junction, pinNumber);
                }

                NodeList lineNodes = (NodeList) xPath.evaluate("./line", partNode, XPathConstants.NODESET);
                for (int j = 0; j < lineNodes.getLength(); ++j) {
                    Element lineNode = (Element) lineNodes.item(j);
                    Point2D p1 = new Point2D(Double.parseDouble(lineNode.getAttribute("x1")),
                                             Double.parseDouble(lineNode.getAttribute("y1")));
                    Point2D p2 = new Point2D(Double.parseDouble(lineNode.getAttribute("x2")),
                                             Double.parseDouble(lineNode.getAttribute("y2")));
                    Double width = Double.parseDouble(lineNode.getAttribute("width"));

                    part.addShape(new PartLine(p1, p2, width));
                }

                NodeList rectNodes = (NodeList) xPath.evaluate("./rectangle", partNode, XPathConstants.NODESET);
                for (int j = 0; j < rectNodes.getLength(); ++j) {
                    Element rectNode = (Element) rectNodes.item(j);

                    Point2D p1 = new Point2D(Double.parseDouble(rectNode.getAttribute("x1")),
                                             Double.parseDouble(rectNode.getAttribute("y1")));
                    Point2D p2 = new Point2D(Double.parseDouble(rectNode.getAttribute("x2")),
                                             Double.parseDouble(rectNode.getAttribute("y2")));

                    part.addShape(new PartRectangle(p1, p2));
                }

                NodeList circleNodes = (NodeList) xPath.evaluate("./circle", partNode, XPathConstants.NODESET);
                for (int j = 0; j < circleNodes.getLength(); ++j) {
                    Element circleNode = (Element) circleNodes.item(j);

                    Point2D center = new Point2D(Double.parseDouble(circleNode.getAttribute("x")),
                                                 Double.parseDouble(circleNode.getAttribute("y")));
                    double radius = Double.parseDouble(circleNode.getAttribute("radius"));
                    double width = Double.parseDouble(circleNode.getAttribute("width"));

                    part.addShape(new PartCircle(center, radius, width));
                }

                NodeList arcNodes = (NodeList) xPath.evaluate("./arc", partNode, XPathConstants.NODESET);
                for (int j = 0; j < arcNodes.getLength(); ++j) {
                    Element arcNode = (Element) arcNodes.item(j);
                    
                    Point2D cx = new Point2D(Double.parseDouble(arcNode.getAttribute("cx")),
                                             Double.parseDouble(arcNode.getAttribute("cy")));
                    Double radius = Double.parseDouble(arcNode.getAttribute("radius"));
                    Double start = Double.parseDouble(arcNode.getAttribute("start"));
                    Double angle = Double.parseDouble(arcNode.getAttribute("angle"));
                    Double width = Double.parseDouble(arcNode.getAttribute("width"));

                    part.addShape(new PartArc(cx, radius, start, angle, width, Color.GREEN));
                }

                NodeList textNodes = (NodeList) xPath.evaluate("./text", partNode, XPathConstants.NODESET);
                for (int j = 0; j < textNodes.getLength(); ++j) {
                    Element textNode = (Element) textNodes.item(j);
                    Double x = Double.parseDouble(textNode.getAttribute("x"));
                    Double y = Double.parseDouble(textNode.getAttribute("y"));
                    Double size = Double.parseDouble(textNode.getAttribute("size"));
                    // String layer = textNode.getAttribute("layer");
                    String text = textNode.getTextContent();
//                    if (text.isEmpty()) {
//                        text = "???";
//                    }
                    part.addShape(new PartText(x, y, text, size));
                }

                System.err.printf("Part: %s\n", part);
                addDevice(part);
                part.createNode();
            }

            NodeList netNodes = (NodeList) xPath.evaluate("net", breadboardNode, XPathConstants.NODESET);
            for (int i = 0; i < netNodes.getLength(); ++i) {
                Element netNode = (Element) netNodes.item(i);
                String netName = netNode.getAttribute("name");
                Net net = new Net(netName); // , netPads);

                // List<Pad> netPads = new ArrayList<>();
                NodeList junctionNodes = (NodeList) xPath.evaluate("./junction", netNode, XPathConstants.NODESET);
                for (int j = 0; j < junctionNodes.getLength(); ++j) {
                    Element junctionNode = (Element) junctionNodes.item(j);
                    String junctionId = junctionNode.getAttribute("id");
                    Double xpos = Double.parseDouble(junctionNode.getAttribute("x"));
                    Double ypos = Double.parseDouble(junctionNode.getAttribute("y"));

                    Junction junction = new Junction(xpos, ypos);
                    junctions.put(junctionId, junction);
                    System.err.printf("  %s\n", junction);
                    
                    net.addJunction(junction);
                }

                NodeList airwireNodes = (NodeList) xPath.evaluate("./airwire", netNode, XPathConstants.NODESET);
                for (int j = 0; j < airwireNodes.getLength(); ++j) {
                    Element airwireNode = (Element) airwireNodes.item(j);
                    String fromId = airwireNode.getAttribute("from");
                    String toId = airwireNode.getAttribute("to");

                    AbstractNode from = junctions.get(fromId);
                    if (from == null) from = pads.get(fromId);  // TODO: This is a bad hack!!!!
                    AbstractNode to = junctions.get(toId);
                    if (to == null) to = pads.get(toId);        // TODO: This is a bad hack!!!!
                    System.err.printf("  AW: %s -> %s\n", from, to);

                    net.addTrace(new AirWire(from, to));
                }

                NodeList traceNodes = (NodeList) xPath.evaluate("./trace", netNode, XPathConstants.NODESET);
                for (int j = 0; j < traceNodes.getLength(); ++j) {
                    Element traceNode = (Element) traceNodes.item(j);
                    String fromId = traceNode.getAttribute("from");
                    String toId = traceNode.getAttribute("to");
                    boolean isBridge = Boolean.parseBoolean(traceNode.getAttribute("isBridge"));

                    AbstractNode from = junctions.get(fromId);
                    if (from == null) from = pads.get(fromId);      // TODO: This is a bad hack!!!!
                    AbstractNode to = junctions.get(toId);
                    if (to == null) to = pads.get(toId);          // TODO: This is a bad hack!!!!
                    System.err.printf("  T : %s -> %s\n", from, to);

                    Trace t = new Trace(from, to);
                    if (isBridge) {
                        t.setAsBridge();
                    }
                    net.addTrace(t);
                }

                System.err.printf("Net: %s\n", net);

                addNet(net);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    public String getSchematicFile() {
        return schematicFile;
    }

    /**
     * Updates this board from the board passed as parameter.
     * All parts are compared and updated if they have changed.
     * Pending: Also update nets
     *
     * @param updatedBoard The new board
     */
    public void update(Board updatedBoard) {
        //System.err.printf("New    : %s parts and %s nets ...\n", updatedBoard.getParts().size(), updatedBoard.getNets().size());
        //System.err.printf("Current: %s parts and %s nets ...\n", getParts().size(), getNets().size());

        // verify parts - changed, added, deleted!
        Set<String> updatedParts = new HashSet<>(updatedBoard.getParts().keySet());
        
        Set<String> removedParts = new HashSet<>(getParts().keySet());
        removedParts.removeAll(updatedParts);

        Set<String> addedParts = new HashSet<>(updatedBoard.getParts().keySet());
        addedParts.removeAll(getParts().keySet());

        Set<String> potentiallyModified = new HashSet<>(getParts().keySet());
        potentiallyModified.removeAll(removedParts);
        potentiallyModified.removeAll(addedParts);


        Set<String> modifiedParts = new HashSet<>();

        // System.err.printf("Potentially replaced: %s parts\n", potentiallyModified.size());
        potentiallyModified.forEach(partName -> {
            Part p1 = getParts().get(partName);
            Part p2 = updatedBoard.getParts().get(partName);
            if (p1.replacedWith(p2)) {
                modifiedParts.add(partName);
            }
        });

        System.err.printf("Removed  parts: %s\n", removedParts);
        System.err.printf("Added    parts: %s\n", addedParts);
        System.err.printf("Modified parts: %s\n", modifiedParts);
        modifiedParts.forEach(partName -> {
            Part p1 = getParts().get(partName);
            Part p2 = updatedBoard.getParts().get(partName);

            System.err.printf("  %s => %s\n", p1, p2);
            for (Pad p : p2.getPads()) {
                String pinNr = p.getPinNumber();
                Pad oldPad = p1.getPad(pinNr);

                p.traceStarts = oldPad.traceStarts;
                p.traceStarts.forEach(wire -> wire.setFrom(p));
            
                p.traceEnds = oldPad.traceEnds;
                p.traceEnds.forEach(wire -> wire.setTo(p));
            }

            parts.remove(partName);
            parts.put(partName, p2);

            p2.setRotation(p1.getRotation());
            p2.setLayoutX(p1.getLayoutX());
            p2.setLayoutY(p1.getLayoutY());
        });
        
        removedParts.forEach(partName -> {
            removePart(partName);
        });

        addedParts.forEach(partName -> {
            Part newPart = updatedBoard.getParts().get(partName);
            newPart.pads.values().forEach(pad -> {
                pad.traceStarts.clear();    // remove references to "new" board
                pad.traceEnds.clear();      // remove references to "new" board
            });
            addDevice(newPart);
        });

        // ********************* Nets *********************************
        final Set<String> existingNetNames = getNets().keySet();
        final Set<String> newNetNames = updatedBoard.getNets().keySet();
        
        Set<String> removedNets = new HashSet<>(existingNetNames);
        removedNets.removeAll(newNetNames);

        Set<String> addedNets = new HashSet<>(newNetNames);
        addedNets.removeAll(existingNetNames);

        Set<String> potentiallyModifiedNets = new HashSet<>(existingNetNames);
        potentiallyModifiedNets.removeAll(removedNets);
        /// potentiallyModified.removeAll(addedParts);

        Set<String> modifiedNets = new HashSet<>();
        potentiallyModifiedNets.forEach(netName -> {
            Net n1 = getNets().get(netName);
            Net n2 = updatedBoard.getNets().get(netName);
            System.err.println("    " + n1);
            System.err.println(" <=>" + n2);
            if (!n1.sameAs(n2)) {
                modifiedNets.add(netName);
            }
        });

        System.err.printf("Removed  nets: %s\n", removedNets);
        System.err.printf("Added    nets: %s\n", addedNets);
        System.err.printf("Modified nets: %s\n", modifiedNets);

        removedNets.forEach(netName -> {
            Net net = getNets().get(netName);
            net.clear();
            getNets().remove(netName);
        });

        modifiedNets.forEach(netName -> {
            Net oldnet = getNets().get(netName);
            oldnet.clear();
            getNets().remove(netName);
            
            // TODO: The following code is the same as in addedNets below! 
            List<Pad> padList = new ArrayList<>();
            Net newNet = updatedBoard.getNets().get(netName);
            newNet.getPads().forEach(pad -> {
                String partName = pad.getPart().getName();
                Part part = getParts().get(partName);
                if (part != null) {
                    String pinNumber = pad.getPinNumber();
                    Pad p = part.getPad(pinNumber);
                    if (p != null) {
                        padList.add(p);
                    } else {
                        System.err.printf("WARNING: Pin %s not found in Part %s\n", pinNumber, partName);
                    }
                } else {
                    System.err.printf("WARNING: Part %s not found in board\n", partName);
                }
            });

            // Create a new net and connect all pads through an AirWire (TODO: duplicate code in EagleNetImport)
            Net net = new Net(netName);
            Pad p1 = null;
            for (Pad p2 : padList) {
                if (p1 != null) {
                    net.addTrace(new AirWire(p1, p2));
                }
                p1 = p2;
            }

            addNet(net);
        });

        addedNets.forEach(netName -> {
            List<Pad> padList = new ArrayList<>();
            Net newNet = updatedBoard.getNets().get(netName);
            newNet.getPads().forEach(pad -> {
                String partName = pad.getPart().getName();
                Part part = getParts().get(partName);
                if (part != null) {
                    String pinNumber = pad.getPinNumber();
                    Pad p = part.getPad(pinNumber);
                    if (p != null) {
                        padList.add(p);
                    } else {
                        System.err.printf("WARNING: Pin %s not found in Part %s\n", pinNumber, partName);
                    }
                } else {
                    System.err.printf("WARNING: Part %s not found in board\n", partName);
                }
            });

            // Create a new net and connect all pads through an AirWire (TODO: duplicate code in EagleNetImport)
            Net net = new Net(netName);
            Pad p1 = null;
            for (Pad p2 : padList) {
                if (p1 != null) {
                    net.addTrace(new AirWire(p1, p2));
                }
                p1 = p2;
            }

            addNet(net);
        });
    }

    /**
     * Removes a part from this board.
     * This removes the part and all airwires which originate from this part.
     *
     * @param partName The name of the part to remove.
     */
    private void removePart(String partName) {
        Part part = getParts().get(partName);
        if (part != null) {
            part.getPads().forEach(pad -> {
                pad.traceStarts.forEach(trace -> {
                    Net net = (Net) trace.getParent();    // todo: provide explicit access path  
                    net.getTraces().remove(trace);
                });
                pad.traceStarts.clear();

                pad.traceEnds.forEach(trace -> {
                    Net net = (Net) trace.getParent();    // todo: provide explicit access path
                    net.getTraces().remove(trace);
                    
                });
                pad.traceEnds.clear();
            });

            parts.remove(partName);
        }
    }
}
