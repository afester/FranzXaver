package afester.javafx.examples.board.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

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
import afester.javafx.examples.board.tools.PointTools;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Point2D;

public class Board {

    // The list of corners which make up the basic shape of the board.
    private final ObservableList<Point2D> boardShapePoints = FXCollections.observableArrayList();

    // The mapping from part names to parts.
    private final ObservableMap<String, Part> parts = FXCollections.observableHashMap();

    // The mapping from net names to Nets.
    private final ObservableMap<String, Net> nets = FXCollections.observableHashMap();

    // The name of the schematic file which is the source for this Board.
    private String schematicFile;

    private File boardFile;


    /**
     * Creates a new empty board with a default dimension of 100mm X 160mm
     */
    public Board() {
        this(null);
    }

    public Board(File sourceFile) {
        boardShapePoints.add(new Point2D(  0,   0));
        boardShapePoints.add(new Point2D(100,   0));
        boardShapePoints.add(new Point2D(100, 160));
        boardShapePoints.add(new Point2D(  0, 160));
        this.boardFile = sourceFile;
    }

    private final static double GRID = 1.0;        

    private Point2D snapToGrid(Point2D pos, double grid) {
        return new Point2D(((int) ( pos.getX() / grid)) * grid,
                           ((int) ( pos.getY() / grid)) * grid);
    }

    public void setCornerPos(int cornerIdx, Point2D newPos) {
        // Point2D snappedPos = snapToGrid(newPos, GRID);
        System.err.printf("setCornerPos %s: %s\n", cornerIdx, newPos);
        boardShapePoints.set(cornerIdx, newPos);
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

    
    
    private double dist = Double.MAX_VALUE;
    private int result = 0;
    private int idx = 0;
    public void addCorner(Point2D pos) {
        Point2D snappedPos = snapToGrid(pos, GRID);
        System.err.println("Adding corner at " + snappedPos);
        
        // calculate the nearest line.
        // Note: This is not the correct algorithm since the StraightLines are really "straight lines" - they 
        // do not really have a start or end point:
        //
        //  1    
        //   \  *:
        //    \  :
        //     \ :
        //      \:
        //       |.
        //       | .
        //       |  .
        //       2
        //
        // In the above diagram, for the point at the asterisk, this algorithm 
        // would take Line 2 instead of Line 1!!!!
        //
        // Taking the center point of each line would also not work.
        //
        // At least, we need to check if the foot point of the orthogonal line from the given point to the 
        // respective straight line is inbetween the start and the end point.

        dist = Double.MAX_VALUE;
        result = -1;
        idx = 0;
        PointTools.straightLineIteratorFromPoints(boardShapePoints, line -> {
            // Get the projection of the clicked point on the current line 
            final Point2D fp = line.getFootpoint(snappedPos);

            // This is a hack - but it should probably work in this case ...
            // TODO: This needs some improvement. At least checking whether a Point p
            // is between the start and the end line should be encapsulated in a proper class.
            if ((line.getStart().getX() <= fp.getX() && fp.getX() <= line.getEnd().getX() ||
                 line.getEnd().getX() <= fp.getX()   && fp.getX() <= line.getStart().getX())
                 &&
                (line.getStart().getY() <= fp.getY() && fp.getY() <= line.getEnd().getY() ||
                 line.getEnd().getY() <= fp.getY() && fp.getY() <= line.getStart().getY())) {

                // check if the current foot point is closer to the clicked point as
                // the previous one
                final double d = fp.distance(snappedPos);
                if (d < dist) {
                    result = idx;
                    dist = d;
                }
            }

            idx++;
        });

        if (result >= 0) {
            result = Math.min(result + 1, boardShapePoints.size());         // safety net
            boardShapePoints.add(result, pos);
        }
    }

    public void addPart(Part part) {
        parts.put(part.getName(), part);
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
                for (AbstractEdge t : net.getTraces()) {
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


    /**
     * @return The name of the schematic file on which this board is based.
     */
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
    public void update(Board updatedBoard, Consumer<String> log) {
        log.accept("Comparing boards ...\n");

//        //getNets().forEach( (k, n) -> n.dumpNet());
//        //System.err.println("==================================");

        log.accept(String.format("Current Board: %s parts and %s nets\n", 
                                 getParts().size(), getNets().size()));
        log.accept(String.format("Updated Board: %s parts and %s nets\n", 
                                 updatedBoard.getParts().size(), updatedBoard.getNets().size()));

        // verify parts - changed, added, deleted!
        Set<String> updatedParts = new HashSet<>(updatedBoard.getParts().keySet());
        
        Set<String> removedParts = new HashSet<>(getParts().keySet());
        removedParts.removeAll(updatedParts);

        Set<String> addedParts = new HashSet<>(updatedBoard.getParts().keySet());
        addedParts.removeAll(getParts().keySet());

        Set<String> potentiallyModified = new HashSet<>(getParts().keySet());
        potentiallyModified.removeAll(removedParts);
        potentiallyModified.removeAll(addedParts);

        final var modifiedParts = new HashSet<>();
        potentiallyModified.forEach(partName -> {
            Part p1 = getParts().get(partName);
            Part p2 = updatedBoard.getParts().get(partName);
            
            //log.accept(String.format("%-5s: \"%s\" \"%s\"\n",  partName, p1.getValue(), p1.getPackage().getName()));
            //log.accept(String.format("       \"%s\" \"%s\"\n", p2.getValue(), p2.getPackage().getName()));
            if (p1.replacedWith(p2)) {
                //log.accept("    => MODIFIED\n");
                modifiedParts.add(partName);
            }
        });

        log.accept(String.format("\nRemoved  parts: %s\n", removedParts));
        log.accept(String.format("Added    parts: %s\n", addedParts));
        log.accept(String.format("Modified parts: %s\n", modifiedParts));

        modifiedParts.forEach(partName -> {
            Part partOld = getParts().get(partName);
            Part updPart = updatedBoard.getParts().get(partName);
            Part partNew = new Part(updPart.getName(), updPart.getValue(), updPart.getPackage());

            replacePart(log, partOld, partNew);

            log.accept(String.format("     Removing %s\n", partOld));
            // Note: We are not (necessarily) on the Application Thread here,
            // but the listener which listens to the model changes
            // MUST be executed in the Application Thread!
            // However, the listener is executed synchronously which means 
            // that it also executes in the background thread!
            // We do not want to introduce a dependency to the view layer here,
            // hence the view needs to take care that the JavaFX API calls
            // are executed on the application thread.
        });

        removedParts.forEach(partName -> {
            removePart(partName);
        });

        addedParts.forEach(partName -> {
            Part newPart = updatedBoard.getParts().get(partName);
            newPart.getPins().forEach(pad -> {
                // TODO!!!!
                //pad.traceStarts.clear();    // remove references to "new" board
                //pad.traceEnds.clear();      // remove references to "new" board
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
//            System.err.println("    " + oldNet);
//            System.err.println(" <=>" + newNet);
            if (!oldNet.sameAs(newNet)) {
                modifiedNets.add(netName);
            }
        });

        log.accept(String.format("\nRemoved  nets: %s\n", removedNets));
        log.accept(String.format("Added    nets: %s\n", addedNets));
        log.accept(String.format("Modified nets: %s\n", modifiedNets));

//        removedNets.forEach(netName -> {
//            Net net = getNets().get(netName);
//            net.clear();
//            getNets().remove(netName);
//        });
//
//        modifiedNets.forEach(netName -> {
//            System.err.printf("Recreating Net %s\n", netName);
//
//            Net oldnet = getNets().get(netName);
//            oldnet.clear();
//            getNets().remove(netName);
//
//            // TODO: The following code is the same as in addedNets below! 
//            List<Pin> padList = new ArrayList<>();
//            Net newNet = updatedBoard.getNets().get(netName);
//            newNet.getPads().forEach(pad -> {
//                String partName = pad.getPart().getName();
//                Part part = getParts().get(partName);
//
//                if (part != null) {
//                    final String padName = pad.getPadName();
//                    System.err.printf("   %s, %s\n", part, padName);
//
//                    final Pin p = part.getPin(padName);
//
//                    if (p != null) {
//                        padList.add(p);
//                    } else {
//                        System.err.printf("WARNING: Pin %s not found in Part %s\n", padName, partName);
//                    }
//                } else {
//                    System.err.printf("WARNING: Part %s not found in board\n", partName);
//                }
//            });
//
//            // Create a new net and connect all pads through an AirWire (TODO: duplicate code in EagleNetImport)
//            Net net = new Net(netName);
//            Pin p1 = null;
//            for (Pin p2 : padList) {
//                if (p1 != null) {
//                    net.addTrace(new AirWire(p1, p2, net));
//                }
//                p1 = p2;
//            }
//
//            addNet(net);
//        });
//
//        addedNets.forEach(netName -> {
//            List<Pin> padList = new ArrayList<>();
//            Net newNet = updatedBoard.getNets().get(netName);
//            newNet.getPads().forEach(pad -> {
//                String partName = pad.getPart().getName();
//                Part part = getParts().get(partName);
//                if (part != null) {
//                    final String padName = pad.getPadName();
//                    final Pin p = part.getPin(padName);
//                    if (p != null) {
//                        padList.add(p);
//                    } else {
//                        System.err.printf("WARNING: Pin %s not found in Part %s\n", padName, partName);
//                    }
//                } else {
//                    System.err.printf("WARNING: Part %s not found in board\n", partName);
//                }
//            });
//
//            // Create a new net and connect all pads through an AirWire (TODO: duplicate code in EagleNetImport)
//            Net net = new Net(netName);
//            Pin p1 = null;
//            for (Pin p2 : padList) {
//                if (p1 != null) {
//                    net.addTrace(new AirWire(p1, p2, net));
//                }
//                p1 = p2;
//            }
//
//            addNet(net);
//        });
//
//        // getNets().forEach( (k, n) -> n.dumpNet());
    }

    
    /**
     * Replaces one part with another part. Identical pins will be reconnected
     * accordingly.
     *
     * @param log A consumer for logging output.
     * @param partOld The part to replace.
     * @param partNew The new part which replaces the old part.
     */
    public void replacePart(Consumer<String> log, Part partOld, Part partNew) {
        parts.remove(partOld.getName());

        addPart(partNew);
        partNew.setRotation(partOld.getRotation());
        partNew.setPosition(partOld.getPosition());

        for (Pin newPin : partNew.getPins()) {
            // try to reconnect pins with the same name
            String pinNr = newPin.getPadName();
            Pin oldPin = partOld.getPin(pinNr);

            log.accept(String.format("  %s => %s\n", oldPin, newPin));

            if (oldPin != null) {
                oldPin.getEdges().forEach(e -> {
                    log.accept(String.format("  Adding %s to %s\n", e, newPin));

                    // e.reconnect(oldPin, newPin);  // throws CME
                    newPin.addEdge(e);
                    if (e.getFrom() == oldPin) {
                        e.fromProperty().setValue(newPin);
                    } else  if (e.getTo() == oldPin) {
                        e.toProperty().setValue(newPin);
                    } else {
                        log.accept("ERROR: pin is neither FROM nor TO!");
                    }
                });
            }
        }
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
            removePart(part);
        }
    }

    private void removePart(Part part) {
        part.getPins().forEach(pad -> {
            pad.getEdges().forEach(trace -> {
                Net net = trace.getNet();
                net.getTraces().remove(trace);
            });
            pad.getEdges().clear();
        });

        parts.remove(part.getName());
    }

    public double getWidth() {
        return getBoardCorners().stream()
                              .max((a, b) -> Double.compare(a.getX(), b.getX())).get().getX();
    }

    public void importSchematic(NetImport ni) {
        ni.importFile(this);
    }

    public void synchronizeSchematic(Consumer<String> log) {
        Board updatedBoard = new Board();
        NetImport ni = new EagleImport(new File(schematicFile));
        ni.importFile(updatedBoard);
        update(updatedBoard, log);
    }

    /**
     * @return The absolute path name of this Board file.
     */
    public String getFileName() {
        return boardFile.getAbsolutePath();
    }
}
