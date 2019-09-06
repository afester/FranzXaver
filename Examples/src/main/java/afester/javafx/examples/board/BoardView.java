package afester.javafx.examples.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import afester.javafx.examples.board.model.AbstractWire;
import afester.javafx.examples.board.model.Board;
import afester.javafx.examples.board.model.Junction;
import afester.javafx.examples.board.model.Part;
import afester.javafx.examples.board.BoardShape;
import afester.javafx.examples.board.tools.Polygon2D;
import afester.javafx.examples.board.model.Net;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


class DoubleVal {
    public double val;
}


public class BoardView extends Pane {

    private Board board;
    private Color padColor   = // Color.rgb(0xB2, 0x99, 0x70);
                                // Color.rgb(0xf8, 0xe7, 0xbe, 1.0);
                                //Color.rgb(0xfe, 0xcc, 0x00, 1.0);
                                Color.rgb(0xb8, 0x73, 0x33, 1.0);   // COPPER

    private final Point2D padOffset = new Point2D(2.5, 2.0);
    private BoardShape boardShape;

    private Group boardGroup;       // The board itself, including dimensions
    private Group dimensionGroup;   // The board dimensions - a children of boardGroup
    private Group partsGroup;       // all parts (and their pads) on the board
    private Group airWireGroup;     // all AirWires,
    private Group traceGroup;       //     Traces,
    private Group bridgeGroup;      // and Bridges on the board
    private Group junctionGroup;    // all junctions
    private Group handleGroup;      // all dynamic handles (topmost layer)

    private Interactor interactor = null;
    private boolean isReadOnly = false;

    // The interactable object which is currently selected; TODO: Can this be moved to the Interactor?
    private final ObjectProperty<Interactable> selectedObject = new SimpleObjectProperty<>();
    public ObjectProperty<Interactable> selectedObjectProperty() { return selectedObject; }
    public Interactable getSelectedObject() { return selectedObject.get(); }
    public void setSelectedObject(Interactable obj) { selectedObject.set(obj); }


    /**
     * Creates a new BoardView for an existing Board.
     *
     * @param board The board for which to create the new view.
     */
    public BoardView(Board board) {
        String css = BoardView.class.getResource("boardStyle.css").toExternalForm();
        getStylesheets().add(css);
        setBoard(board);
    }

    private static <T> void pointIterator(Iterable<T> iterable, BiConsumer<T, T> consumer) {
        Iterator<T> it = iterable.iterator();
        while(it.hasNext()) {
            T first = it.next();
            if(!it.hasNext()) return;
            T second = it.next();
            consumer.accept(first, second);
        }
    }


    private static void lineIterator(Iterable<Double> iterable, BiConsumer<Point2D, Point2D> consumer) {
        Iterator<Double> it = iterable.iterator();

        if(!it.hasNext()) return;
        Double firstX = it.next();
        if(!it.hasNext()) return;
        Double firstY = it.next();

        Double x1 = firstX;
        Double y1 = firstY;
        while(it.hasNext()) {
            Double x2 = it.next();
            if(!it.hasNext()) return;
            Double y2 = it.next();

            consumer.accept(new Point2D(x1, y1), new Point2D(x2, y2));
            x1 = x2;
            y1 = y2;
        }

        // close the polygon
        consumer.accept(new Point2D(x1, y1), new Point2D(firstX, firstY));
    }


    private void netUpdater(Net net) {
     // Handling traces
        Map<AbstractWire, TraceView> tMap = new HashMap<>();
        System.err.println("VIEW: creating Net " + net.getName());
        net.getTraces().forEach(trace -> {
            System.err.printf("  VIEW: creating Trace %s\n", trace);
            TraceView traceView = new TraceView(trace);
            tMap.put(trace, traceView);

            switch(trace.getType()) {
            case AIRWIRE: airWireGroup.getChildren().add(traceView);
                break;

            case BRIDGE:  bridgeGroup.getChildren().add(traceView);
                break;

            case TRACE: traceGroup.getChildren().add(traceView);
                break;

            default:
                break;
            }
        });
        net.getTraces().addListener((javafx.collections.ListChangeListener.Change<? extends AbstractWire> change) -> {
            change.next();
            change.getRemoved().forEach(trace -> {
                TraceView traceView = tMap.get(trace);
                switch(trace.getType()) {
                case AIRWIRE: airWireGroup.getChildren().remove(traceView);
                    break;
                case BRIDGE:  bridgeGroup.getChildren().remove(traceView);
                    break;
                case TRACE: traceGroup.getChildren().remove(traceView);
                    break;
                default:
                    break;
                }
            });


//!!! Essentially, this is working, but the coordinates of the AirWire are not correct!                

//           Intentionally left non-compilable

            change.getAddedSubList().forEach(trace -> {
                TraceView traceView = new TraceView(trace);
                tMap.put(trace, traceView);
                System.err.println("ADDING TRACE VIEW: " + traceView);

                switch(trace.getType()) {
                case AIRWIRE: airWireGroup.getChildren().add(traceView);
                    break;

                case BRIDGE:  bridgeGroup.getChildren().add(traceView);
                    break;

                case TRACE: traceGroup.getChildren().add(traceView);
                    break;

                default:
                    break;
                }  
            });
        });

//Handling junctions
        Map<Junction, JunctionView> jMap = new HashMap<>();
        net.getJunctions().forEach(junction -> {
            JunctionView junctionView = new JunctionView(junction);
            jMap.put(junction,  junctionView);
            junctionGroup.getChildren().add(junctionView);
        });
        net.getJunctions().addListener((javafx.collections.ListChangeListener.Change<? extends Junction> change) -> {
            change.next();
            change.getRemoved().forEach(junction -> {
                JunctionView jView = jMap.remove(junction);
                junctionGroup.getChildren().remove(jView);
            });
            change.getAddedSubList().forEach(junction -> {
                JunctionView jView = new JunctionView(junction);
                jMap.put(junction,  jView);
                junctionGroup.getChildren().add(jView);
            });
        });
    }

    public void setBoard(Board board) {
        this.board = board;

        boardGroup = new Group();
        boardGroup.setId("boardGroup");
        partsGroup = new Group();
        partsGroup.setId("partsGroup");
        airWireGroup = new Group();
        airWireGroup.setId("airWireGroup");
        traceGroup = new Group();
        traceGroup.setId("traceGroup");
        bridgeGroup = new Group();
        bridgeGroup.setId("bridgeGroup");
        dimensionGroup = new Group();
        dimensionGroup.setId("dimensionGroup");
        junctionGroup = new Group();
        junctionGroup.setId("junctionGroup");
        handleGroup = new Group();
        handleGroup.setId("handleGroup");

        getChildren().addAll(boardGroup, partsGroup,
                             traceGroup, bridgeGroup, airWireGroup,
                             junctionGroup, handleGroup);

        createPlainBoard();
        board.getBoardCorners().addListener((javafx.collections.ListChangeListener.Change<? extends Point2D> change) -> {
            // When the board shape changes, we simply recreate the whole board
            // This might be too heavy, but it at least works and ensures that the whole board shape is consistent
            createPlainBoard();
            createBoardDimensions();
        });

// Handling nets
        System.err.println("Adding Nets ...");
        board.getNets().forEach((netName, net) -> {
            System.err.println("  " + net);
            netUpdater(net);
        });
        board.getNets().addListener((javafx.collections.MapChangeListener.Change<? extends String, ? extends Net> change) -> {
            if (change.wasRemoved()) {
                // not yet implemented - but the complete net can be removed from the model!
            }

            if (change.wasAdded()) {
                System.err.println("View: NET CHANGE:" + change);
                Net net = change.getValueAdded();
                netUpdater(net);
            }
        });

// Handling parts
        Map<Part, PartView> pMap = new HashMap<>();
        System.err.println("Adding Parts ...");
        board.getParts().forEach((k, g) -> {
            // Create a PartView from the model
            PartView partView = new PartView(g);
            System.err.println("  " + g);
            partsGroup.getChildren().add(partView);

            pMap.put(g, partView);
        });
        board.getParts().addListener((javafx.collections.MapChangeListener.Change<? extends String, ? extends Part> change) -> {
            if (change.wasRemoved()) {
                Part removed = change.getValueRemoved();
                PartView partView = pMap.remove(removed);
                if (partView != null) {
                    partsGroup.getChildren().remove(partView);
                }
            }

            if (change.wasAdded()) {
                Part added = change.getValueAdded();
                PartView partView = new PartView(added);
                partsGroup.getChildren().add(partView);

                pMap.put(added, partView);
            }
        });

        setOnMousePressed(e -> { 
            if (interactor != null) {
                interactor.mousePressed(e);
            }
        });

        setOnMouseDragged(e -> {
            if (interactor != null) {
                interactor.mouseDragged(e);
            }
         });
        
        setOnMouseReleased(e -> {
            if (interactor != null) {
                interactor.mouseReleased(e);
            }
        });
    }
    

    private void createPlainBoard() {
        boardGroup.getChildren().clear();

        System.err.println("\nCreating board background ...");

// Board shape
        boardShape = new BoardShape();
        final ObservableList<Point2D> boardDims = board.getBoardCorners();
        boardShape.setPoints(boardDims);

// Holes / Pads (this is a rectangle!)
        Bounds b = boardShape.getBoundsInParent();
        Group padsGroup = new Group();
        for (double ypos = padOffset.getY();  ypos < b.getHeight();  ypos += 2.54 ) {
            for (double xpos = padOffset.getX();  xpos < b.getWidth();  xpos += 2.54) {

                // pad (backside of board)
//                Circle c = new Circle(xpos, ypos, 0.7);
//                c.setFill(Color.WHITE);
//                c.setStroke(padColor);
//                c.setStrokeWidth(0.6);
//                getChildren().add(c);

                // hole (frontside of board)
                Circle c = new Circle(xpos, ypos, 0.4);
                c.setFill(Color.WHITE);
                c.setStroke(null);
                padsGroup.getChildren().add(c);
            }
        }

// Real shape of the board to clip the Holes/Pads 
        Polygon2D clipShape = new Polygon2D();
        clipShape.setPoints(boardDims);
        padsGroup.setClip(clipShape);

        boardGroup.getChildren().addAll(boardShape, padsGroup, dimensionGroup);
    }

    
    /**
     * Enable or disable showing the board dimensions.
     *
     * @param doShow Defines whether to show or hide the board dimensions.
     */
    public void showBoardDimensions(boolean doShow) {
        dimensionGroup.getChildren().clear();
        if (doShow) {
            createBoardDimensions();
        }
    }


    class IntVal {
        int val;
    }

    private void createBoardDimensions() {
        // add the board dimensions
        System.err.println("Adding Board dimensions ...");
        dimensionGroup.getChildren().clear();

// Handles for each corner of the board        
        final List<BoardCorner> corners = new ArrayList<>();
        IntVal idx = new IntVal();
        pointIterator(boardShape.getPoints(), (xpos, ypos) -> {
            BoardCorner c = new BoardCorner(xpos, ypos, getBoard(), idx.val);
            idx.val++;
            dimensionGroup.getChildren().add(c);
            corners.add(c);
        });

// Board dimensions
        Font dimFont = Font.font("Courier", 2.0);
        final Point2D unitVec = new Point2D(1.0, 0.0);
        lineIterator(boardShape.getPoints(), (p1, p2) -> {
            System.err.printf("%s/%s\n", p1, p2);
            
            Point2D vecDir = p2.subtract(p1);                               // direction vector
            Point2D vecNorm = new Point2D(vecDir.getY(), -vecDir.getX());   // norm vector
            vecNorm = vecNorm.normalize();                                  // normalized norm vector ...
            vecNorm = vecNorm.multiply(3.0);                                // ... of length 3.0

            Point2D p1_1 = p1.add(vecNorm);                                 // line parallel to existing line, outside the shape
            Point2D p2_1 = p2.add(vecNorm);

            Line l = new Line(p1_1.getX(), p1_1.getY(), p2_1.getX(), p2_1.getY());
            l.setStroke(Color.BLUE);
            l.setStrokeWidth(0.5);
            dimensionGroup.getChildren().add(l);

            Point2D midpoint = p2_1.midpoint(p1_1);
//            Circle c = new Circle(midpoint.getX(), midpoint.getY(), 0.5);
//            c.setFill(null);
//            c.setStroke(Color.RED);
//            c.setStrokeWidth(0.3);
//            getChildren().add(c);

            Double angle = unitVec.angle(vecDir);
//            if (angle > 180) {
//                angle -= 180;
//            }
            Double length = p1.distance(p2);
            System.err.printf("%s/%s => %s\n", Point2D.ZERO, vecDir, angle);
            Text value = new Text(midpoint.getX(), midpoint.getY(), String.format("%.1f mm", length));
            // value.setRotationAxis(new Point3D(midpoint.getX(), midpoint.getY(), 0.0));
            value.setTextOrigin(VPos.BASELINE);
            value.setRotate(angle);
            value.setFont(dimFont);

            dimensionGroup.getChildren().add(value);
        });
    }

    public Board getBoard() {
        return board;
    }

	public Point2D getPadOffset() {
		return padOffset;
	}

    public void clearSelection() {
        if (getSelectedObject() != null) {
            getSelectedObject().setSelected(false);
        }
        setSelectedObject(null);
    }


    public void setInteractor(Interactor newInteractor) {
        if (!isReadOnly) {
            System.err.println("Setting " + newInteractor);
            interactor = newInteractor;
        } else {
            interactor = null;
        }
    }
   

    public Group getHandleGroup() {
        return handleGroup;
    }
    

    public void setReadOnly(boolean b) {
        interactor = null;
        isReadOnly = b;
    }
}
