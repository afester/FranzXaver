package afester.javafx.examples.board.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import afester.javafx.examples.board.ApplicationProperties;
import afester.javafx.examples.board.StyleSelector;
import afester.javafx.examples.board.model.AbstractEdge;
import afester.javafx.examples.board.model.Board;
import afester.javafx.examples.board.model.Part;
import afester.javafx.examples.board.model.Net;
import afester.javafx.examples.board.tools.PointTools;
import afester.javafx.examples.board.tools.Polygon2D;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;


public abstract class BoardView extends Pane {
    private final static Logger log = LogManager.getLogger();

    private Board board;
    private final Point2D padOffset = new Point2D(2.5, 2.0);
    private BoardShape boardShape;

    private Group boardGroup;               // The board itself, including dimensions
    private Group dimensionGroup;           // The board dimensions - a children of boardGroup
    private LookupGroup partsGroup;         // all parts (and their pads) on the board
    private LookupGroup netsGroup;          // all nets (Parent group for airWireGroup, traceGroup, bridgeGroup)
    private StyleGroup airWireGroup;        // all AirWires,
    private StyleGroup traceGroup;          //     Traces,
    private StyleGroup bridgeGroup;         // and Bridges on the board
    private LookupGroup handleGroup;        // all dynamic handles (topmost layer)

    private ApplicationProperties props;
    private boolean isReadOnly = false;
    private boolean isBottom;

    // The currently selected objects.
    private final ListProperty<Interactable> selectedObjects = 
                        new SimpleListProperty<>(FXCollections.observableArrayList());
    public ListProperty<Interactable> selectedObjectsProperty() { return selectedObjects; }
    public ObservableList<Interactable> getSelectedObjects() { return selectedObjects.get(); }

    // A flag to indicate whether to show the parts as drafts or as SVG graphics
    private final BooleanProperty showSvg = new SimpleBooleanProperty(false);
    public BooleanProperty showSvgProperty() { return showSvg; }
    public boolean isShowSvg() { return showSvg.get(); }
    public void setShowSvg(boolean flag) { showSvg.set(flag); }

    // A flag to indicate whether to show or hide the traces (routed segments)
    private final BooleanProperty showTraces = new SimpleBooleanProperty(true);
    public BooleanProperty showTracesProperty() { return showTraces; }
    public boolean isShowTraces() { return showTraces.get(); }
    public void setShowTraces(boolean flag) { showTraces.set(flag); }

    // A flag to indicate whether to show or hide airwires (unrouted segments)
    private final BooleanProperty showAirwires= new SimpleBooleanProperty(true);
    public BooleanProperty showAirwiresProperty() { return showAirwires; }
    public boolean isShowAirwires() { return showAirwires.get(); }
    public void setShowAirwires(boolean flag) { showAirwires.set(flag); }

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


    /**
     * Creates a new BoardView for an existing Board.
     *
     * @param board The board for which to create the new view.
     */
    public BoardView(Board board, ApplicationProperties props, boolean isBottom) {
        this.isBottom = isBottom;
        this.board = board;
        this.props = props;

        String css = BoardView.class.getResource("boardStyle.css").toExternalForm();
        getStylesheets().add(css);

//        setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), 
//                new Insets(20))));
        setupBoard();
//        setStyle("-fx-border-style: solid; -fx-border-color: red;");

        showSvgProperty().addListener((obj, oldValue, newValue) -> { partsGroup.getChildren().forEach(part -> ((PartView) part).render(newValue)); });
        traceGroup.visibleProperty().bind(showTracesProperty());
        airWireGroup.visibleProperty().bind(showAirwiresProperty());
        dimensionGroup.visibleProperty().bind(showDimensionsProperty());

        if (isBottom) {
            traceGroup.shapeStyleProperty().bind(props.getStyle(StyleSelector.BOTTOMTRACE_NORMAL));
            airWireGroup.shapeStyleProperty().bind(props.getStyle(StyleSelector.BOTTOMAIRWIRE_NORMAL));
            bridgeGroup.shapeStyleProperty().bind(props.getStyle(StyleSelector.BOTTOMBRIDGE_NORMAL));
        } else {
            traceGroup.shapeStyleProperty().bind(props.getStyle(StyleSelector.TOPTRACE_NORMAL));
            airWireGroup.shapeStyleProperty().bind(props.getStyle(StyleSelector.TOPAIRWIRE_NORMAL));
            bridgeGroup.shapeStyleProperty().bind(props.getStyle(StyleSelector.TOPBRIDGE_NORMAL));
        }

        showBoardHandlesProperty().addListener((obj, oldValue, newValue) -> {
            handleGroup.getChildren().clear();
            if (newValue) {
               // Handles for each corner of the board        
                  final List<BoardHandle> corners = new ArrayList<>();
                  IntVal idx = new IntVal();
                  PointTools.pointIterator(boardShape.getPoints(), (xpos, ypos) -> {
                      BoardHandle c = new BoardHandle(xpos, ypos, getBoard(), idx.val);
                      idx.val++;
                      handleGroup.getChildren().add(c);
                      corners.add(c);
                  });
            }
        });

        setManaged(false);  // !!!!!!!!!!!!
    }



    private void netUpdater(Net net) {
        // Handling traces
        Map<AbstractEdge, TraceView> tMap = new HashMap<>();
        log.info("Creating view for Net: {}", net);
        net.getTraces().forEach(trace -> {
            log.debug("  Creating TraceView for: {}", trace);

            TraceView traceView = new TraceView(trace, isBottom, props);
            tMap.put(trace, traceView);

            switch(trace.getType()) {
            case AIRWIRE: 
                airWireGroup.getChildren().add(traceView);
                break;

            case BRIDGE:  
                bridgeGroup.getChildren().add(traceView);
                break;

            case TRACE: 
                traceGroup.getChildren().add(traceView);
                break;

            default:
                break;
            }
        });
        net.getTraces().addListener((javafx.collections.ListChangeListener.Change<? extends AbstractEdge> change) -> {
            change.next();
            change.getRemoved().forEach(trace -> {
                TraceView traceView = tMap.get(trace);
                switch(trace.getType()) {
                case AIRWIRE: 
                    Platform.runLater(
                            () -> airWireGroup.getChildren().remove(traceView));                            
                    break;

                case BRIDGE:  
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            bridgeGroup.getChildren().remove(traceView);                            
                        }
                    });
                    break;

                case TRACE: 
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            traceGroup.getChildren().remove(traceView);                            
                        }
                    });
                    break;

                default:
                    break;
                }
            });


//!!! Essentially, this is working, but the coordinates of the AirWire are not correct!                

            change.getAddedSubList().forEach(trace -> {
                TraceView traceView = new TraceView(trace, isBottom, props);
                tMap.put(trace, traceView);
                System.err.println("ADDING TRACE VIEW: " + traceView);

                switch(trace.getType()) {
                    case AIRWIRE: airWireGroup.getChildren().add(traceView);
                                  break;

                    case BRIDGE:  bridgeGroup.getChildren().add(traceView);
                                  break;

                    case TRACE: traceGroup.getChildren().add(traceView);
                                break;

                    default: break;
                }  
            });
        });
    }

    private void setupBoard() {
        getChildren().clear();

        boardGroup = new Group();
        boardGroup.setId("boardGroup");
        dimensionGroup = new Group();
        dimensionGroup.setId("dimensionGroup");

        partsGroup = new LookupGroup();
        partsGroup.setId("partsGroup");
        netsGroup = new LookupGroup();
        netsGroup.setId("netsGroup");

        airWireGroup = new StyleGroup();
        airWireGroup.setId("airWireGroup");
        traceGroup = new StyleGroup();
        traceGroup.setId("traceGroup");
        bridgeGroup = new StyleGroup();
        bridgeGroup.setId("bridgeGroup");
        netsGroup.getChildren().addAll(airWireGroup, traceGroup, bridgeGroup);

        handleGroup = new LookupGroup();
        handleGroup.setId("handleGroup");

        if (isBottom) {
            getChildren().addAll(boardGroup, dimensionGroup,
                                 partsGroup, netsGroup, handleGroup);
        } else {
            getChildren().addAll(boardGroup, dimensionGroup,
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
        log.info("Adding Nets ...");
        board.getNets().forEach((netName, net) -> {
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
        log.info("Adding Parts ...");
        board.getParts().forEach((k, g) -> {
            log.info("Creating view for Part: {}", g);

            // Create a PartView from the model
            PartView partView = new PartView(g, isBottom);
            partsGroup.getChildren().add(partView);
            pMap.put(g, partView);
            if (g.isHidden()) {
                partView.setVisible(false);
            }
        });
        board.getParts().addListener((javafx.collections.MapChangeListener.Change<? extends String, ? extends Part> change) -> {
            if (change.wasRemoved()) {
                Part removed = change.getValueRemoved();
                
                PartView partView = pMap.remove(removed);
                if (partView != null) {
                    // NOTE: We might be in a background thread here!
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            partsGroup.getChildren().remove(partView);                            
                        }
                    });
                }
            }

            if (change.wasAdded()) {
                Part added = change.getValueAdded();
                PartView partView = new PartView(added, isBottom);
                pMap.put(added, partView);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        partsGroup.getChildren().add(partView);
                    }
                });
            }
        });

        // NOTE: When parts are removed, they contain a duplicate listener!!!!
        board.getParts().forEach((partName, part) -> {
           part.isHiddenProperty().addListener((obj, oldValue, isHidden) -> {
              PartView p = pMap.get(part);
              p.visibleProperty().set(!isHidden);
           });
        });

    }
    

    private void createPlainBoard() {
        boardGroup.getChildren().clear();

        log.info("Creating plain board ...");

// Board shape
        boardShape = new BoardShape();
        final ObservableList<Point2D> boardDims = board.getBoardCorners();
        boardShape.setPoints(boardDims);

// Holes / Pads (this is a rectangle!)
        Bounds b = boardShape.getBoundsInParent();

        Group padsGroup = new Group();
        for (double ypos = b.getMinY() + padOffset.getY();  ypos < b.getMinY() + b.getHeight();  ypos += 2.54 ) {
            for (double xpos = b.getMinX() + padOffset.getX();  xpos < b.getMinX() + b.getWidth();  xpos += 2.54) {
                final var c = new HoleView(xpos, ypos, isBottom);
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
        log.info("Adding Board dimensions ...");
        dimensionGroup.getChildren().clear();
        PointTools.lineIterator(boardShape.getPoints(), (p1, p2) -> {
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

    public LookupGroup getHandleGroup() {
        return handleGroup;
    }

    public LookupGroup getNetsGroup() {
        return netsGroup;
    }


//    public void setReadOnly(boolean b) {
//        interactor = null;
//        isReadOnly = b;
//    }

    /**
     * @return A list of all Interactable objects at the given position
     * which are either Parts or Segments of a net
     * 
     * @param mpos The position which the objects need to contain. 
     */
    public List<Interactable> getPartsAndNets(Point2D mpos) {
        List<Interactable> result = partsGroup.pickAll(mpos);
        result.addAll(netsGroup.pickAll(mpos));

        System.err.println("getPartsAndNets:" + result);
        return result;
    }


    // private final static double GRID = 2.54;
    private final static double GRID = 1.27;   // for now, we also allow positions between pads - this is        
                                               // required to properly position the Eagle parts ...

    public Point2D snapToGrid(Point2D pos){
        double xpos = pos.getX();
        double ypos = pos.getY();

        xpos = (int) ( (xpos - getPadOffset().getX()) / GRID);
        ypos = (int) ( (ypos - getPadOffset().getY()) / GRID);

        xpos = xpos * GRID + getPadOffset().getX();
        ypos = ypos * GRID + getPadOffset().getY();

        return new Point2D(xpos, ypos);
    }


    public void clearSelection() {
        getSelectedObjects().forEach(node -> node.setSelected(this, false));
        selectedObjectsProperty().clear();
    }
    
    public void setReadOnly(boolean b) {
        isReadOnly = b;    
    }
}
