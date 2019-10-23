package afester.javafx.examples.board.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import afester.javafx.examples.board.Interactable;
import afester.javafx.examples.board.Interactor;
import afester.javafx.examples.board.model.AbstractWire;
import afester.javafx.examples.board.model.Board;
import afester.javafx.examples.board.model.Part;
import afester.javafx.examples.board.model.Net;
import afester.javafx.examples.board.tools.Polygon2D;

import javafx.scene.shape.Circle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;


class DoubleVal {
    public double val;
}


public class BoardView extends Pane {

    private Board board;
    private final Point2D padOffset = new Point2D(2.5, 2.0);
    private BoardShape boardShape;

    private Group boardGroup;       // The board itself, including dimensions
    private LookupGroup boardHandlesGroup;// The handles for the board
    private Group dimensionGroup;   // The board dimensions - a children of boardGroup
    private LookupGroup partsGroup; // all parts (and their pads) on the board
    private LookupGroup netsGroup;        // all nets (Parent group for airWireGroup, traceGroup, bridgeGroup)
    private Group airWireGroup;     // all AirWires,
    private Group traceGroup;       //     Traces,
    private Group bridgeGroup;      // and Bridges on the board
    private LookupGroup handleGroup;      // all dynamic handles (topmost layer)

    private Interactor interactor = null;

    private boolean isReadOnly = false;
    private boolean isBottom;

    // The interactable object which is currently selected; TODO: Can this be moved to the Interactor?
    private final ObjectProperty<Interactable> selectedObject = new SimpleObjectProperty<>();
    public ObjectProperty<Interactable> selectedObjectProperty() { return selectedObject; }
    public Interactable getSelectedObject() { return selectedObject.get(); }
    public void setSelectedObject(Interactable obj) { selectedObject.set(obj); }

    // A flag to indicate whether to show the parts as drafts or as SVG graphics
    private final BooleanProperty showSvg = new SimpleBooleanProperty(false);
    public BooleanProperty showSvgProperty() { return showSvg; }
    public boolean isShowSvg() { return showSvg.get(); }
    public void setShowSvg(boolean flag) { showSvg.set(flag); }

    // A flag to indicate whether to show or hide the nets
    private final BooleanProperty showNets = new SimpleBooleanProperty(true);
    public BooleanProperty showNetsProperty() { return showNets; }
    public boolean isShowNets() { return showNets.get(); }
    public void setShowNets(boolean flag) { showNets.set(flag); }

    // A flag to indicate whether to show or hide the dimensions
    private final BooleanProperty showDimensions = new SimpleBooleanProperty(true);
    public BooleanProperty showDimensionsProperty() { return showDimensions; }
    public boolean isShowDimensions() { return showDimensions.get(); }
    public void setShowDimensions(boolean flag) { showDimensions.set(flag); }

    // A flag to indicate whether to show or hide the board handles
    private final BooleanProperty showBoardHandles = new SimpleBooleanProperty(false);
    public BooleanProperty showBoardHandlesProperty() { return showBoardHandles; }
    public boolean isShowBoardHandles() { return showBoardHandles.get(); }
    public void setShowBoardHandles(boolean flag) { showBoardHandles.set(flag); }

    // A flag to indicate whether to move nodes or to reconnect nodes
    private final BooleanProperty reconnectMode = new SimpleBooleanProperty(false);
    public BooleanProperty reconnectModeProperty() { return reconnectMode; }
    public boolean isReconnectMode() { return reconnectMode.get(); }
    public void setReconnectMode(boolean flag) { reconnectMode.set(flag); }

    // TODO: Move to a central place and make it customizable
    private boolean isPanelAction(MouseEvent e) {
        return (e.isControlDown() && e.isShiftDown()); 
    }

    /**
     * Creates a new BoardView for an existing Board.
     *
     * @param board The board for which to create the new view.
     */
    public BoardView(Board board) {
        this(board, false);
    }

    public BoardView(Board board, boolean isBottom) {
        this.isBottom = isBottom;
        String css = BoardView.class.getResource("boardStyle.css").toExternalForm();
        getStylesheets().add(css);
        setBoard(board);
        
        showSvg.addListener((obj, oldValue, newValue) -> { partsGroup.getChildren().forEach(part -> ((PartView) part).render(newValue)); });
        netsGroup.visibleProperty().bind(showNetsProperty());
        dimensionGroup.visibleProperty().bind(showDimensionsProperty());
        boardHandlesGroup.visibleProperty().bind(showBoardHandlesProperty());

        setOnMousePressed(e -> {
            if (!isPanelAction(e) && interactor != null) {
                interactor.mousePressed(e);
            }
        });

        setOnMouseDragged(e -> {
            if (!isPanelAction(e) && interactor != null) {
                interactor.mouseDragged(e);
            }
         });

        setOnMouseReleased(e -> {
            if (!isPanelAction(e) && interactor != null) {
                interactor.mouseReleased(e);
            }
        });
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
    }

    public void setBoard(Board board) {
        getChildren().clear();
        this.board = board;

        boardGroup = new Group();
        boardGroup.setId("boardGroup");
        boardHandlesGroup = new LookupGroup();
        boardHandlesGroup.setId("boardHandlesGroup");
        dimensionGroup = new Group();
        dimensionGroup.setId("dimensionGroup");

        partsGroup = new LookupGroup();
        partsGroup.setId("partsGroup");
        netsGroup = new LookupGroup();
        netsGroup.setId("netsGroup");

        airWireGroup = new Group();
        airWireGroup.setId("airWireGroup");
        traceGroup = new Group();
        traceGroup.setId("traceGroup");
        bridgeGroup = new Group();
        bridgeGroup.setId("bridgeGroup");
        netsGroup.getChildren().addAll(airWireGroup, traceGroup, bridgeGroup);

        handleGroup = new LookupGroup();
        handleGroup.setId("handleGroup");

        if (isBottom) {
            getChildren().addAll(boardGroup, dimensionGroup, boardHandlesGroup,
                                 partsGroup, netsGroup, handleGroup);
        } else {
            getChildren().addAll(boardGroup, dimensionGroup, boardHandlesGroup,
                                 netsGroup, partsGroup, handleGroup);
        }

        createPlainBoard();
        createBoardDimensions();
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
            PartView partView = new PartView(g, isBottom);
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
                PartView partView = new PartView(added, isBottom);
                partsGroup.getChildren().add(partView);

                pMap.put(added, partView);
            }
        });
    }
    

    private void createPlainBoard() {
        boardGroup.getChildren().clear();

        System.err.println("\nCreating plain board ...");

// Board shape
        boardShape = new BoardShape();
        final ObservableList<Point2D> boardDims = board.getBoardCorners();
        boardShape.setPoints(boardDims);

// Holes / Pads (this is a rectangle!)
        Bounds b = boardShape.getBoundsInParent();
        Group padsGroup = new Group();
        for (double ypos = padOffset.getY();  ypos < b.getHeight();  ypos += 2.54 ) {
            for (double xpos = padOffset.getX();  xpos < b.getWidth();  xpos += 2.54) {
                Circle c = null;
                if (isBottom) {
                    c = new HoleViewBottom(xpos, ypos);
                } else  {
                    c = new HoleViewTop(xpos, ypos);
                }
                
                padsGroup.getChildren().add(c);
            }
        }

// Real shape of the board to clip the Holes/Pads 
        Polygon2D clipShape = new Polygon2D();
        clipShape.setPoints(boardDims);
        padsGroup.setClip(clipShape);       // note: a single Polygon2D can only be either 
                                            // assigned as clip area OR as child of a Parent!

        boardGroup.getChildren().addAll(boardShape, padsGroup, dimensionGroup);
    }


    class IntVal {
        int val;
    }

    private void createBoardDimensions() {
        // add the board dimensions
        System.err.println("Adding Board dimensions ...");

// Handles for each corner of the board        
        boardHandlesGroup.getChildren().clear();
        final List<BoardHandle> corners = new ArrayList<>();
        IntVal idx = new IntVal();
        pointIterator(boardShape.getPoints(), (xpos, ypos) -> {
            BoardHandle c = new BoardHandle(xpos, ypos, getBoard(), idx.val);
            System.err.println("BoardHanadle: " + c);
            idx.val++;
            boardHandlesGroup.getChildren().add(c);
            corners.add(c);
        });

// Board dimensions
        dimensionGroup.getChildren().clear();
        lineIterator(boardShape.getPoints(), (p1, p2) -> {
            Group dim = new DimensionView(p1, p2);
            dimensionGroup.getChildren().add(dim);
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
            getSelectedObject().setSelected(this, false);
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
   

    public LookupGroup getHandleGroup() {
        return handleGroup;
    }


    public LookupGroup getBoardHandleGroup() {
        return boardHandlesGroup;
    }


    public void setReadOnly(boolean b) {
        interactor = null;
        isReadOnly = b;
    }

    /**
     * @return A list of all Interactable objects at the given position
     * which are either Parts or Segments of a net
     * 
     * @param mpos The position which the objects need to contain. 
     */
    public List<Interactable> getPartsAndNets(Point2D mpos) {
        List<Interactable> result = partsGroup.pickAll(mpos);
        result.addAll(netsGroup.pickAll(mpos));

        // Handles (which includes Junctions) are objects which can be dragged immediately
        // (No selection step required!)
        // TODO: Probably JunctionView can be changed to simply be a Handle also????
        // Means, they do not need to be instantiated immediately for each Node ???
//         result.addAll(junctionGroup.pickAll(mpos));
//         result.addAll(handleGroup.pickAll(mpos));

        return result;
    }

    
    
    // private final static double GRID = 2.54;
    private final static double GRID = 1.27;   // for now, we also allow positions between pads - this is        
                                               // required to properly position the Eagle parts ...

//    @Deprecated
//    protected Point2D snapToGrid(Point2D pos, BoardView bv, Point2D offset) {                                                      
//        System.err.println("OFFSET:" + offset);
//        System.err.println("POS   :" + pos);
//        double xpos = offset.getX() + pos.getX();                                                                        
//        double ypos = offset.getY() + pos.getY();                                                                        
//
//        xpos = (int) ( (xpos - bv.getPadOffset().getX()) / GRID);                                         
//        ypos = (int) ( (ypos - bv.getPadOffset().getY()) / GRID);                                         
//    
//        xpos = xpos * GRID + bv.getPadOffset().getX();                                                    
//        ypos = ypos * GRID + bv.getPadOffset().getY();                                                    
//    
//        return new Point2D(xpos, ypos);                                                                   
//    }


    protected Point2D snapToGrid(Point2D pos, boolean useOffset){
//        if (useOffset) {
//            System.err.println("OFFSET:" + offset);
//            System.err.println("POS   :" + pos);
//            double xpos = offset.getX() + pos.getX();                                                                        
//            double ypos = offset.getY() + pos.getY();                                                                        
//    
//            xpos = (int) ( (xpos - bv.getPadOffset().getX()) / GRID);                                         
//            ypos = (int) ( (ypos - bv.getPadOffset().getY()) / GRID);                                         
//        
//            xpos = xpos * GRID + bv.getPadOffset().getX();                                                    
//            ypos = ypos * GRID + bv.getPadOffset().getY();                                                    
//        
//            return new Point2D(xpos, ypos);
//        } else {
            System.err.println("POS   :" + pos);
            double xpos = pos.getX();
            double ypos = pos.getY();

            xpos = (int) ( (xpos - getPadOffset().getX()) / GRID);
            ypos = (int) ( (ypos - getPadOffset().getY()) / GRID);

            xpos = xpos * GRID + getPadOffset().getX();
            ypos = ypos * GRID + getPadOffset().getY();

            return new Point2D(xpos, ypos);
//        }
    }
}
