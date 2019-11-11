package afester.javafx.examples.board.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import afester.javafx.examples.board.eagle.EagleImport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Point2D;

public class Board {

    final ObservableList<Point2D> boardShapePoints = FXCollections.observableArrayList();
    private final ObservableMap<String, Part> parts = FXCollections.observableHashMap();
    private final ObservableMap<String, Net> nets = FXCollections.observableHashMap();
    String schematicFile;
    private File boardFile;

    
    /**
     * Creates a new empty board with a default dimension of 100mm X 160mm
     */
    public Board() {
        boardShapePoints.add(new Point2D(  0,   0));
        boardShapePoints.add(new Point2D(100,   0));
        boardShapePoints.add(new Point2D(100, 160));
        boardShapePoints.add(new Point2D(  0, 160));
    }

    private final static double GRID = 1.0;        

    private Point2D snapToGrid(Point2D pos, double grid) {
        return new Point2D(((int) ( pos.getX() / grid)) * grid,
                           ((int) ( pos.getY() / grid)) * grid);
    }

    public void setCornerPos(int cornerIdx, Point2D newPos) {
        Point2D snappedPos = snapToGrid(newPos, GRID);
        System.err.printf("setCornerPos %s: %s\n", cornerIdx, snappedPos);
        boardShapePoints.set(cornerIdx, snappedPos);
    }

    public ObservableList<Point2D> getBoardCorners() {
        return boardShapePoints;
    }

    public void deleteCorner(int cornerIdx) {
        // at least three points are always required
        if (boardShapePoints.size() > 3) {
            boardShapePoints.remove(cornerIdx);
        }
    }

    public void addCorner(Point2D pos) {
        Point2D snappedPos = snapToGrid(pos, GRID);
        System.err.println("Adding corner at " + snappedPos);

        // calculate the index of the nearest point (TODO: This needs to be improved.
        // The hull should remain convex after adding a new point. Probably need to calculate the
        // shortest distance to the line connecting two points.)
        double dist = Double.MAX_VALUE;
        int result = 0;
        for (int idx = 0;  idx < boardShapePoints.size();  idx++) {
            final Point2D p = boardShapePoints.get(idx);
            if (p.distance(pos) < dist) {
                result = idx;
                dist = p.distance(pos);
            }
        }

        boardShapePoints.add(result, pos);
    }

    public void addPart(Part pkg) {
        parts.put(pkg.getName(), pkg);
    }

    public Part getPart(String ref) {
        return parts.get(ref);
    }

    public  ObservableMap<String, Part> getParts() {
        return parts;
    }

    public ObservableMap<String, Net> getNets() {
        return nets;
    }

    public void addNet(Net net) {
        nets.put(net.getName(), net);
    }

    public class IntVal {
        public int val = 0;
    }
    

    public void save() {
        saveAs(boardFile);
    }

    public void saveAs(File destFile) {
        System.err.println("Saving " + destFile.getAbsolutePath());

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
            doc.appendChild(rootNode);

            // the board shape
            Element boardShape = doc.createElement("boardShape");
            for (Point2D coords : getBoardCorners()) {
                Element point = doc.createElement("point");
                point.setAttribute("x", Double.toString(coords.getX()));
                point.setAttribute("y", Double.toString(coords.getY()));
                boardShape.appendChild(point);
            }
            rootNode.appendChild(boardShape);

            // The packages
            Set<Package> packages = new HashSet<>();
            parts.values().forEach(p -> {
                Package thePackage = p.getPackage();
                packages.add(thePackage);
            });
            packages.forEach(p -> {
                Element packageNode = p.getXml(doc);
                rootNode.appendChild(packageNode);
            });

            // The parts
            IntVal junctionId = new IntVal();
            parts.forEach( (k, v) -> {
                Element partNode = v.getXml(doc, junctionId);
                rootNode.appendChild(partNode);
            });

            // The nets
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

            FileOutputStream fos = new FileOutputStream(destFile);
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

        System.err.println("Saved " + destFile.getAbsolutePath());
    }


//    public void load(File file) {
//        boardFile = file;
//
//        BoardLoader bl = new BoardLoader(file);
//        bl.load(this);
//    }


    public String getSchematicFile() {
        return schematicFile;
    }


    public void setSchematicFile(String name) {
        this.schematicFile = name;
    }


    /**
     * Updates this board from the board passed as parameter.
     * All parts are compared and updated if they have changed.
     * Pending: Also update nets
     *
     * @param updatedBoard The new board
     */
    public void update(Board updatedBoard) {
        //getNets().forEach( (k, n) -> n.dumpNet());
        //System.err.println("==================================");

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
            Part partOld = getParts().get(partName);
            Part partNew = updatedBoard.getParts().get(partName);

            System.err.printf("  %s => %s\n", partOld, partNew);
            for (Pin p : partNew.getPins()) {
                // try to reconnect pins with the same name
                String pinNr = p.getPadName();
                Pin oldPad = partOld.getPin(pinNr);
                if (oldPad != null) {
                    p.traceStarts = oldPad.traceStarts;
                    p.traceStarts.forEach(wire -> wire.from = p);
    
                    p.traceEnds = oldPad.traceEnds;
                    p.traceEnds.forEach(wire -> wire.to = p);
                }
            }

            parts.remove(partName);
            parts.put(partName, partNew);

            partNew.setRotation(partOld.getRotation());
            partNew.setPosition(partOld.getPosition());
        });
        
        removedParts.forEach(partName -> {
            removePart(partName);
        });

        addedParts.forEach(partName -> {
            Part newPart = updatedBoard.getParts().get(partName);
            newPart.getPins().forEach(pad -> {
                pad.traceStarts.clear();    // remove references to "new" board
                pad.traceEnds.clear();      // remove references to "new" board
            });
            addPart(newPart);
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
            Net oldNet = getNets().get(netName);
            Net newNet = updatedBoard.getNets().get(netName);
            System.err.println("    " + oldNet);
            System.err.println(" <=>" + newNet);
            if (!oldNet.sameAs(newNet)) {
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
            System.err.printf("Recreating Net %s\n", netName);

            Net oldnet = getNets().get(netName);
            oldnet.clear();
            getNets().remove(netName);

            // TODO: The following code is the same as in addedNets below! 
            List<Pin> padList = new ArrayList<>();
            Net newNet = updatedBoard.getNets().get(netName);
            newNet.getPads().forEach(pad -> {
                String partName = pad.getPart().getName();
                Part part = getParts().get(partName);

                if (part != null) {
                    final String padName = pad.getPadName();
                    System.err.printf("   %s, %s\n", part, padName);

                    final Pin p = part.getPin(padName);

                    if (p != null) {
                        padList.add(p);
                    } else {
                        System.err.printf("WARNING: Pin %s not found in Part %s\n", padName, partName);
                    }
                } else {
                    System.err.printf("WARNING: Part %s not found in board\n", partName);
                }
            });

            // Create a new net and connect all pads through an AirWire (TODO: duplicate code in EagleNetImport)
            Net net = new Net(netName);
            Pin p1 = null;
            for (Pin p2 : padList) {
                if (p1 != null) {
                    net.addTrace(new AirWire(p1, p2, net));
                }
                p1 = p2;
            }

            addNet(net);
        });

        addedNets.forEach(netName -> {
            List<Pin> padList = new ArrayList<>();
            Net newNet = updatedBoard.getNets().get(netName);
            newNet.getPads().forEach(pad -> {
                String partName = pad.getPart().getName();
                Part part = getParts().get(partName);
                if (part != null) {
                    final String padName = pad.getPadName();
                    final Pin p = part.getPin(padName);
                    if (p != null) {
                        padList.add(p);
                    } else {
                        System.err.printf("WARNING: Pin %s not found in Part %s\n", padName, partName);
                    }
                } else {
                    System.err.printf("WARNING: Part %s not found in board\n", partName);
                }
            });

            // Create a new net and connect all pads through an AirWire (TODO: duplicate code in EagleNetImport)
            Net net = new Net(netName);
            Pin p1 = null;
            for (Pin p2 : padList) {
                if (p1 != null) {
                    net.addTrace(new AirWire(p1, p2, net));
                }
                p1 = p2;
            }

            addNet(net);
        });

        // getNets().forEach( (k, n) -> n.dumpNet());
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
            part.getPins().forEach(pad -> {
                pad.traceStarts.forEach(trace -> {
                    Net net = trace.getNet();
                    net.getTraces().remove(trace);
                });
                pad.traceStarts.clear();

                pad.traceEnds.forEach(trace -> {
                    Net net = trace.getNet();
                    net.getTraces().remove(trace);
                    
                });
                pad.traceEnds.clear();
            });

            parts.remove(partName);
        }
    }

    public double getWidth() {
        return getBoardCorners().stream()
                              .max((a, b) -> Double.compare(a.getX(), b.getX())).get().getX();
    }

    public void importSchematic(NetImport ni) {
        ni.importFile(this);
    }

    public void synchronizeSchematic() {
        System.err.println("Synchronizing " + schematicFile);

        Board updatedBoard = new Board();
        NetImport ni = new EagleImport(new File(schematicFile));
        ni.importFile(updatedBoard);
        update(updatedBoard);
    }


}
