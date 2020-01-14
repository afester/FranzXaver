package afester.javafx.examples.board;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import afester.javafx.components.DrawingArea;
import afester.javafx.components.StatusBar;
import afester.javafx.components.ToolbarButton;
import afester.javafx.components.ToolbarToggleButton;
import afester.javafx.examples.board.eagle.EagleImport;
import afester.javafx.examples.board.model.AbstractEdge;
import afester.javafx.examples.board.model.Board;
import afester.javafx.examples.board.model.BoardLoader;
import afester.javafx.examples.board.model.Net;
import afester.javafx.examples.board.model.NetImport;
import afester.javafx.examples.board.view.AddCornerInteractor;
import afester.javafx.examples.board.view.BoardView;
import afester.javafx.examples.board.view.BottomBoardView;
import afester.javafx.examples.board.view.DeleteCornerInteractor;
import afester.javafx.examples.board.view.EditInteractor;
import afester.javafx.examples.board.view.EditShapeInteractor;
import afester.javafx.examples.board.view.SplitTraceInteractor;
import afester.javafx.examples.board.view.TopBoardView;
import afester.javafx.examples.board.view.TraceView;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Scene
 *   BorderPane mainLayout
 *       Top: topBar
 *          menuBar
 *          toolBar
 *       Bottom: statusBar
 *       Left: leftBar (TBD)
 *       Right: rightBar (TBD)
 *       Center: tabPane
 *          Tab editTab
 *             Content: DrawingView(Pane) topDrawingView. Completely fills the Tab area.
 *                       Content: BoardView(Pane) topView. Is located at a specific position in the DrawingView.
 *          Tab bottomViewTab
 *          Tab printTab
 */
public class BreadBoardEditor extends Application {

    public static final Logger log = LogManager.getLogger();

    private Scene mainScene;
    private Stage stage;

    private final SplitPane splitPane = new SplitPane();
    private final TabPane tabPane = new TabPane();

    private BomView bomView;
    private BoardView topView;
    private BoardView bottomView;
    
    private Tab editTab;
    private Tab bottomViewTab;
    private Tab printTab;
    private ToolBar editCornersToolBar; 
    private ToolBar routingToolbar; 

    private final ToolbarToggleButton reconnectTraceModeToolButton = new ToolbarToggleButton("Reconnect Trace", "afester/javafx/examples/board/mode-reconnect.png");
    private final ToolbarToggleButton moveJunctionToolButton = new ToolbarToggleButton("Move Junction", "afester/javafx/examples/board/mode-movejunction.png");
    private final Button shortestPathButton = new ToolbarButton("Shortest", "afester/javafx/examples/board/net-shortest.png");
    private final Button resetNetButton = new ToolbarButton("Reset net", "afester/javafx/examples/board/net-reset.png");
    private final Button cleanupNetButton = new ToolbarButton("Validate/Cleanup net", "afester/javafx/examples/board/net-cleanup.png");
    private final Button deleteSegmentButton = new ToolbarButton("Delete segment", "afester/javafx/examples/board/net-delsegment.png");
    private final Button shortestAllButton = new ToolbarButton("Shortest all", "afester/javafx/examples/board/net-shortestall.png");
    private final Button toBridgeToolButton = new ToolbarButton("Bridge", "afester/javafx/examples/board/mode-bridge.png");
    private final Button toTraceToolButton = new ToolbarButton("Trace", "afester/javafx/examples/board/mode-trace.png");

    private final ToolbarButton newToolButton = new ToolbarButton("New board", "afester/javafx/examples/board/file-new.png");
    private final ToolbarButton openToolButton = new ToolbarButton("Open board", "afester/javafx/examples/board/file-open.png");
    private final ToolbarButton saveToolButton = new ToolbarButton("Save board", "afester/javafx/examples/board/file-save.png");
    private final ToolbarButton saveAsToolButton = new ToolbarButton("Save board as", "afester/javafx/examples/board/file-saveas.png");

    private final ToolbarToggleButton toggleSvgToolButton = new ToolbarToggleButton("Toggle draft / SVG", "afester/javafx/examples/board/view-svg.png");
    private final ToolbarToggleButton toggleShowTracesToolButton = new ToolbarToggleButton("Show / hide routes", "afester/javafx/examples/board/view-traces.png");
    private final ToolbarToggleButton toggleShowAirwiresToolButton = new ToolbarToggleButton("Show / hide unrouted wires", "afester/javafx/examples/board/view-airwires.png");
    private final ToolbarToggleButton toggleShowDimensionsToolButton = new ToolbarToggleButton("Show / hide dimensions", "afester/javafx/examples/board/view-dimensions.png");

    // Edit mode toggles
    private final ToolbarToggleButton selectToolButton = new ToolbarToggleButton("Select", "afester/javafx/examples/board/mode-select.png");
    private final ToolbarToggleButton splitTraceToolButton = new ToolbarToggleButton("Split Trace", "afester/javafx/examples/board/mode-splittrace.png");
    private final ToolbarToggleButton editShapeToolButton = new ToolbarToggleButton("Edit shape", "afester/javafx/examples/board/mode-editshape.png");

    private DrawingArea currentDrawingView;
    private DrawingArea topDrawingView;
    private DrawingArea bottomDrawingView;
    private PrintPanel printPanel;

    private ApplicationProperties props;

    @Override
    public void start(Stage stage){
        log.info("Starting Application");

        // The pane is exactly the size of the center component. Its children (which is the BoardView) are clipped
        // and the view can be panned and zoomed.
        topDrawingView = new DrawingArea();

        editTab = new Tab("Top view");
        editTab.setClosable(false);
        editTab.setContent(topDrawingView);

        bottomViewTab = new Tab("Bottom view");
        bottomViewTab.setClosable(false);
        
        printTab = new Tab("Print preview");
        printTab.setClosable(false);

        tabPane.getTabs().addAll(editTab, bottomViewTab, printTab);
        tabPane.getSelectionModel().selectedIndexProperty().addListener((obj, oldIdx, newIdx) -> switchTab(newIdx.intValue()));
        // Create the menu bar

        Menu fileMenu = new Menu("File");
        MenuItem menuItem0 = new MenuItem("New board ...");
        menuItem0.setOnAction(e -> newBoard());
        MenuItem menuItem1 = new MenuItem("Load board ...");
        menuItem1.setOnAction(e -> loadBoard());
        MenuItem menuItem2 = new MenuItem("Save board");
        menuItem2.setOnAction(e -> saveBoard());
        MenuItem menuItem3 = new MenuItem("Save board as ...");
        menuItem3.setOnAction(e -> saveBoardAs());
        MenuItem menuItem4 = new MenuItem("Import schematic ...");
        menuItem4.setOnAction(e -> importSchematic());
        MenuItem menuItem5 = new MenuItem("Synchronize schematic ...");
        menuItem5.setOnAction(e -> synchronizeSchematic());
        MenuItem menuItem7 = new MenuItem("Quit");
        menuItem7.setOnAction(e -> stage.close());

        fileMenu.getItems().addAll(menuItem0, menuItem1, new SeparatorMenuItem(),
                                   menuItem2, menuItem3, new SeparatorMenuItem(),
                                   menuItem4, menuItem5, new SeparatorMenuItem(),
                                   menuItem7);

        Menu viewMenu = new Menu("View");
        MenuItem viewItem1 = new MenuItem("Center");
        viewItem1.setOnAction(e -> currentDrawingView.centerContent());
        MenuItem viewItem2 = new MenuItem("Fit to Window");
        viewItem2.setOnAction(e -> currentDrawingView.fitContentToWindow());
        MenuItem colorSettings = new MenuItem("Color Settings ...");
        colorSettings.setOnAction(e -> setupColors());
        viewMenu.getItems().addAll(viewItem1, viewItem2, colorSettings);

        Menu editMenu = new Menu("Edit");
        MenuItem editItem1 = new MenuItem("Select");
        MenuItem editItem2 = new MenuItem("Trace");
        editMenu.getItems().addAll(editItem1, editItem2);

        Menu helpMenu = new Menu("_Help");
        helpMenu.setAccelerator(KeyCombination.keyCombination("Alt+H"));
        MenuItem menuItemAbout = new MenuItem("_About ...");
        menuItemAbout.setOnAction(e -> showAbout());
        helpMenu.getItems().addAll(menuItemAbout);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, viewMenu, editMenu, helpMenu);

        final ToolBar toolBar = createMainToolbar();
        editCornersToolBar = createCornerEditToolbar();
        routingToolbar = createRoutingToolbar();
        editCornersToolBar.setVisible(false);

        VBox topBar = new VBox();
        topBar.getChildren().addAll(menuBar, toolBar);

        StatusBar sb = new StatusBar();

//        topView.selectedObjectsProperty().addListener((obj, oldValue, newValue) -> {
//            SimpleListProperty<Interactable> slp = (SimpleListProperty<Interactable>) obj;
//            if (slp.getSize() == 0) {
//                sb.textProperty().set("");
//            } else if (slp.getSize() == 1) {
//                sb.textProperty().set(slp.get(0).getRepr());
//            } else if (slp.getSize() > 1) {
//                sb.textProperty().set("Multiple objects selected.");
//            }
//        });

        VBox rightBar = new VBox();
        rightBar.getChildren().addAll(editCornersToolBar, routingToolbar);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topBar);
        mainLayout.setBottom(sb);
        mainLayout.setRight(rightBar);
        mainLayout.setCenter(splitPane);

        // load application state
        props = ApplicationProperties.load();

        final var width = props.getDouble("appWidth", 1024.0);
        final var height = props.getDouble("appHeight", 768.0);
        mainScene = new Scene(mainLayout, width, height);

        stage.setScene(mainScene);
        this.stage = stage;

        stage.show();

        final var lastFile = props.getString("lastFile", null);
        if (lastFile != null) {
            loadBoard(new File(lastFile));
        }
    }


    private void setupColors() {

        if (bottomView != null) {
            ColorSettings cs = new ColorSettings(
                    new Pair<>(ColorClass.TOPBOARD, Color.DARKRED),
                    new Pair<>(ColorClass.TOPTRACE, Color.BLUE),
                    new Pair<>(ColorClass.TOPPAD, Color.GREEN),
                    new Pair<>(ColorClass.BOTTOMBOARD, Color.DARKRED),
                    new Pair<>(ColorClass.BOTTOMTRACE, props.getBottomTraceColor()),
                    new Pair<>(ColorClass.BOTTOMPAD, Color.GREEN));

            cs.setOnColorChanged((key, value) -> {
                switch (key) {
                case BOTTOMPAD:
                    break;

                case BOTTOMTRACE:
                    props.setBottomTraceColor(value);
                    break;

                default:
                    break;

                }
            });

            cs.setOnWidthChanged((key, value) -> {
                switch (key) {
                case BOTTOMPAD:
                    break;

                case BOTTOMTRACE:
                    props.setBottomTraceWidth(value);
                    break;

                default:
                    break;

                }
            });

            cs.show();
        }
    }

    
    private ToolBar createRoutingToolbar() {
        // Create the toolbar

        final ToggleGroup junctionModeToggleGroup = new ToggleGroup();

        reconnectTraceModeToolButton.setDisable(true);
        reconnectTraceModeToolButton.setToggleGroup(junctionModeToggleGroup);
        reconnectTraceModeToolButton.setOnAction(e -> topView.setReconnectMode(true));

        moveJunctionToolButton.setDisable(true);
        moveJunctionToolButton.setToggleGroup(junctionModeToggleGroup);
        moveJunctionToolButton.setOnAction(e -> topView.setReconnectMode(false));       
        moveJunctionToolButton.setSelected(true);

        shortestPathButton.setDisable(true);
        shortestPathButton.setOnAction(e -> calculateShortestPath());

        resetNetButton.setDisable(true);
        resetNetButton.setOnAction(e -> resetNet());

        cleanupNetButton.setDisable(true);
        cleanupNetButton.setOnAction(e -> cleanupNet());

        deleteSegmentButton.setDisable(true);
        deleteSegmentButton.setOnAction(e -> deleteSegment());

        shortestAllButton.setDisable(true);
        shortestAllButton.setOnAction(e -> calculateShortestPathAll());

        toBridgeToolButton.setDisable(true);
        toBridgeToolButton.setOnAction(e -> toBridge());

        toTraceToolButton.setDisable(true);
        toTraceToolButton.setOnAction(e -> toTrace());

        ToolBar toolBar = new ToolBar(
                reconnectTraceModeToolButton,
                moveJunctionToolButton,
                new Separator(),

                shortestPathButton,
                resetNetButton,
                cleanupNetButton,
                deleteSegmentButton,
                toBridgeToolButton,
                toTraceToolButton,
                new Separator(),

                shortestAllButton
            );
        toolBar.setOrientation(Orientation.VERTICAL);
        toolBar.managedProperty().bind(toolBar.visibleProperty());
        return toolBar;
    }


    private ToolBar createMainToolbar() {
        saveToolButton.setDisable(true);

        saveAsToolButton.setDisable(true);

        toggleSvgToolButton.setDisable(true);

        toggleShowTracesToolButton.setDisable(true);
        toggleShowTracesToolButton.setSelected(true);

        toggleShowAirwiresToolButton.setDisable(true);
        toggleShowAirwiresToolButton.setSelected(true);

        toggleShowDimensionsToolButton.setDisable(true);
        toggleShowDimensionsToolButton.setSelected(true);

        selectToolButton.setDisable(true);
        selectToolButton.setOnAction(e -> {
            if (topView != null) {
                topDrawingView.setInteractor(new EditInteractor(topView));
            }
        });

        splitTraceToolButton.setDisable(true);
        splitTraceToolButton.setOnAction(e -> {
            if (topView != null) {
                topDrawingView.setInteractor(new SplitTraceInteractor(topView));
            }
        });

        editShapeToolButton.setDisable(true);
        editShapeToolButton.selectedProperty().addListener((value, oldValue, newValue) -> setShapeEditMode(newValue));

        ToggleGroup toggleGroup = new ToggleGroup();
        selectToolButton.setToggleGroup(toggleGroup);
        splitTraceToolButton.setToggleGroup(toggleGroup);
        editShapeToolButton.setToggleGroup(toggleGroup);

        selectToolButton.setSelected(true);

        ToolBar toolBar = new ToolBar(
                newToolButton,
                openToolButton,
                saveToolButton,
                saveAsToolButton,
                new Separator(),

                selectToolButton,
                splitTraceToolButton,
                editShapeToolButton,
                new Separator(),

                toggleSvgToolButton,
                toggleShowTracesToolButton,
                toggleShowAirwiresToolButton,
                toggleShowDimensionsToolButton
            );
        return toolBar;
    }


    private void setShapeEditMode(Boolean newValue) {
        if (newValue) {
            topDrawingView.setInteractor(new EditShapeInteractor(topView));
        }
        topView.setShowBoardHandles(newValue);
        editCornersToolBar.setVisible(newValue);
        routingToolbar.setVisible(!newValue);
    }


    private ToolBar createCornerEditToolbar() {
        // Create the toolbar

        final ToolbarToggleButton editCornerButton = new ToolbarToggleButton("Move corner", "afester/javafx/examples/board/mode-select.png");
        editCornerButton.setOnAction(e -> topDrawingView.setInteractor(new EditShapeInteractor(topView)));

        final ToolbarToggleButton addCornerButton = new ToolbarToggleButton("Add corner", "afester/javafx/examples/board/editshape-addcorner.png");
        addCornerButton.selectedProperty().addListener((value, oldValue, newValue) -> {
            if (newValue) {
                topDrawingView.setInteractor(new AddCornerInteractor(topView));
            }
        });

        final ToolbarToggleButton deleteCornerButton = new ToolbarToggleButton("Delete corner", "afester/javafx/examples/board/editshape-deletecorner.png");
        deleteCornerButton.selectedProperty().addListener((value, oldValue, newValue) -> {
            if (newValue) {
                topDrawingView.setInteractor(new DeleteCornerInteractor(topView));
            }
        });

        var toggleGroup = new ToggleGroup();
        editCornerButton.setToggleGroup(toggleGroup);
        addCornerButton.setToggleGroup(toggleGroup);
        deleteCornerButton.setToggleGroup(toggleGroup);

        final ToolBar toolBar = new ToolBar(
                                        editCornerButton,
                                        addCornerButton,
                                        deleteCornerButton);
        toolBar.setOrientation(Orientation.VERTICAL);
        toolBar.managedProperty().bind(toolBar.visibleProperty());
        return toolBar;
    }

    private void showAbout() {
        Dialog<Void> dialog = new AboutDialog();
        dialog.showAndWait();
    }


    private void switchTab(int newIdx) {
        if (newIdx == 0) {
            log.info("Switch to TOP tab");

            if (bottomView != null) {
                bottomView.showSvgProperty().unbind();
                bottomView.showTracesProperty().unbind();
                bottomView.showAirwiresProperty().unbind();
                bottomView.showDimensionsProperty().unbind();
            }
            topView.showSvgProperty().bind(toggleSvgToolButton.selectedProperty());
            topView.showTracesProperty().bind(toggleShowTracesToolButton.selectedProperty());
            topView.showAirwiresProperty().bind(toggleShowAirwiresToolButton.selectedProperty());
            topView.showDimensionsProperty().bind(toggleShowDimensionsToolButton.selectedProperty());

            currentDrawingView = topDrawingView;
        } else if (newIdx == 1) {
            log.debug("Switch to BOTTOM tab");

            if (bottomView == null) {
                Board b = topView.getBoard();
                bottomView = new BottomBoardView(b, props);
                // bottomView.setReadOnly(true);

                bottomView.getTransforms().add(Transform.scale(-1, 1));

                bottomDrawingView = new DrawingArea();
                bottomView.setReadOnly(true);
                bottomDrawingView.getPaper().getChildren().add(bottomView);
                bottomViewTab.setContent(bottomDrawingView);

                stage.sizeToScene();    // required to properly fit the content to the window
                bottomDrawingView.fitContentToWindow();
            }

            topView.showSvgProperty().unbind();
            topView.showTracesProperty().unbind();
            topView.showAirwiresProperty().unbind();
            topView.showDimensionsProperty().unbind();
            bottomView.showSvgProperty().bind(toggleSvgToolButton.selectedProperty());
            bottomView.showTracesProperty().bind(toggleShowTracesToolButton.selectedProperty());
            bottomView.showAirwiresProperty().bind(toggleShowAirwiresToolButton.selectedProperty());
            bottomView.showDimensionsProperty().bind(toggleShowDimensionsToolButton.selectedProperty());

            currentDrawingView = bottomDrawingView;
        } else if (newIdx == 2) {
            log.debug("Switch to PRINT tab");

            if (printPanel == null) {
                printPanel = new PrintPanel(topView.getBoard(), stage, props);
                printTab.setContent(printPanel);

                stage.sizeToScene();    // required to properly fit the content to the window
                printPanel.getDrawingArea().fitContentToWindow();
            }

            currentDrawingView = printPanel.getDrawingArea();
        }
    }


    private void cleanupNet() {
//        Interactable selectedObject = topView.getSelectedObject();
//        if (selectedObject instanceof Trace) {
//            topView.clearSelection();
//            Trace trace = (Trace) selectedObject;
//            Net net = trace.getNet();
//            log.debug("Cleaning up " + net);
//            
//            net.cleanup();
//        }
    }


    private void deleteSegment() {
        topView.getSelectedObjects().forEach(selectedObject -> {
            if (selectedObject instanceof TraceView) {
                TraceView wire = (TraceView) selectedObject;
                AbstractEdge trace = wire.getTrace();

                // remove the segment, if possible
                trace.remove();
            }
        });

        topView.clearSelection();
    }


    /**
     * Removes all junctions and all wires from the selected nets and recreates the net by
     * connecting all pads of the net with the shortest path algorithm. 
     */
    private void resetNet() {
        topView.getSelectedObjects().forEach(selectedObject -> {
            if (selectedObject instanceof TraceView) {
                TraceView wire = (TraceView) selectedObject;
                Net net = wire.getTrace().getNet();

                // calculate the shortest path of the net
                net.resetNet();
            }

        });

        topView.clearSelection();
    }


    /**
     * Converts the currently selected trace into a bridge wire.
     */
    private void toBridge() {
        topView.getSelectedObjects().forEach(selectedObject -> {
            if (selectedObject instanceof TraceView) {
                TraceView traceView  = (TraceView) selectedObject;
                final AbstractEdge wire = traceView.getTrace();
                final Net net = wire.getNet();

                // change the trace to a Bridge
                net.changeToBridge(wire);
            }
        });

        topView.clearSelection();
    }


    /**
     * Converts the currently selected trace into a bridge wire.
     */
    private void toTrace() {
        topView.getSelectedObjects().forEach(selectedObject -> {
            if (selectedObject instanceof TraceView) {
                TraceView traceView  = (TraceView) selectedObject;
                final AbstractEdge wire = traceView.getTrace();

                // change the Segment to a Trace
                wire.convertToStraightTrace();
            }
        });

        topView.clearSelection();
    }


    /**
     * Calculates the shortest path for the currently selected net. 
     */
    private void calculateShortestPath() {
//        Interactable selectedObject = topView.getSelectedObject();
//        if (selectedObject instanceof Trace) {
//            topView.clearSelection();
//
//            Trace trace = (Trace) selectedObject;
//            Net net = (Net) trace.getNet();
//            System.err.printf("NET: %s\n", net.getName());
//
//            // calculate the shortest path of the net
//            net.calculateShortestPath();
//
//            // re-render board
//           // bv.setBoard(bv.getBoard());
//        }
    }


    /**
     * Calculates the shortest path for all nets.
     */
    private void calculateShortestPathAll() {
//        topView.clearSelection();
//
//        // calculate the shortest path for all nets
//        topView.getBoard().getNets().values().forEach(net -> net.calculateShortestPath());
//
//        // re-render board
//        // bv.setBoard(bv.getBoard());
    }


    private void synchronizeSchematic() {
        Board board = topView.getBoard();
        board.synchronizeSchematic();
    }


    private void newBoard() {
        Board board = new Board();
        setupUi(board);
    }

    private void setupUi(Board board) {
        topView = new TopBoardView(board, props);
        bomView = new BomView(topView);
        topDrawingView.getPaper().getChildren().clear();
        topDrawingView.getPaper().getChildren().add(topView);
        topDrawingView.setInteractor(new EditInteractor(topView));
        bottomView = null;

        splitPane.getItems().clear();
        splitPane.getItems().addAll(bomView, tabPane);
        splitPane.setDividerPosition(0, 0.15);  // TODO
        SplitPane.setResizableWithParent(bomView, false);   // do not resize the BOM list

        reconnectTraceModeToolButton.setDisable(false);
        moveJunctionToolButton.setDisable(false);
        shortestPathButton.setDisable(false);
        resetNetButton.setDisable(false);
        cleanupNetButton.setDisable(false);
        deleteSegmentButton.setDisable(false);
        shortestAllButton.setDisable(false);
        toBridgeToolButton.setDisable(false);
        toTraceToolButton.setDisable(false);

        newToolButton.setDisable(false);
        openToolButton.setDisable(false);
        saveToolButton.setDisable(false);
        saveAsToolButton.setDisable(false);
        toggleSvgToolButton.setDisable(false);
        toggleShowTracesToolButton.setDisable(false);
        toggleShowAirwiresToolButton.setDisable(false);
        toggleShowDimensionsToolButton.setDisable(false);
        selectToolButton.setDisable(false);
        splitTraceToolButton.setDisable(false);
        editShapeToolButton.setDisable(false);

        topView.showSvgProperty().bind(toggleSvgToolButton.selectedProperty());
        topView.showTracesProperty().bind(toggleShowTracesToolButton.selectedProperty());
        topView.showAirwiresProperty().bind(toggleShowAirwiresToolButton.selectedProperty());
        topView.showDimensionsProperty().bind(toggleShowDimensionsToolButton.selectedProperty());

        stage.sizeToScene();
        switchTab(0);   // ???????????
        topDrawingView.fitContentToWindow();
    }


    private void loadBoard() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Board ...");
        File result = fileChooser.showOpenDialog(stage);
        if (result != null) {
            loadBoard(result);
        }
    }


    private void loadBoard(File boardFile) {
        BoardLoader bl = new BoardLoader(boardFile);
        Board board = bl.load();

        setupUi(board);
    }


    private void saveBoard() {
        Board board = topView.getBoard();
        board.save();
    }

    private void saveBoardAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Board as ...");
        File result = fileChooser.showSaveDialog(stage);
        if (result != null) {
            Board board = topView.getBoard();
            board.saveAs(result);
        }
    }

    private void importSchematic() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import schematic ...");
        File result = fileChooser.showOpenDialog(stage);
        if (result != null) {
            log.debug("Importing " + result.getAbsolutePath());

            NetImport ni = new EagleImport(result);
            Board board = topView.getBoard();
            board.importSchematic(ni);

            topView = new TopBoardView(board, props);
            topDrawingView.getPaper().getChildren().clear();
            topDrawingView.getPaper().getChildren().add(topView);
            topDrawingView.fitContentToWindow();
            bottomView = null;
        }
    }


    @Override
    public void stop() {
        log.info("Exitting Application");
        if (topView != null) {
            
            props.setDouble("appWidth", mainScene.getWidth());
            props.setDouble("appHeight", mainScene.getHeight());
            props.setString("lastFile", topView.getBoard().getFileName());

            props.save();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }
}
