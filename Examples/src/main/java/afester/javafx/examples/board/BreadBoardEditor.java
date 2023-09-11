package afester.javafx.examples.board;

import java.io.File;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import afester.javafx.components.DrawingArea;
import afester.javafx.components.StatusBar;
import afester.javafx.components.ToolbarToggleButton;
import afester.javafx.examples.board.eagle.EagleImport;
import afester.javafx.examples.board.model.AbstractEdge;
import afester.javafx.examples.board.model.Board;
import afester.javafx.examples.board.model.BoardLoader;
import afester.javafx.examples.board.model.Net;
import afester.javafx.examples.board.model.NetImport;
import afester.javafx.examples.board.model.Trace;
import afester.javafx.examples.board.model.TraceType;
import afester.javafx.examples.board.tools.Action;
import afester.javafx.examples.board.tools.ActionChoice;
import afester.javafx.examples.board.tools.ActionRadio;
import afester.javafx.examples.board.tools.ActionToggle;
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
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


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

    private static final Logger log = LogManager.getLogger();

    private Scene mainScene;
    private Stage stage;

    private final SplitPane splitPane = new SplitPane();
    private final SplitPane logSplitPane = new SplitPane();
    private final TabPane tabPane = new TabPane();
    private final TextArea logOutput = new TextArea();

    private StackPane leftGroup = new StackPane();
    private BomView bomView;
    private BoardView topView;
    private BoardView bottomView;
    
    private Tab editTab;
    private Tab bottomViewTab;
    private Tab printTab;
    private ToolBar editCornersToolBar; 
    private ToolBar routingToolbar; 


    private DrawingArea currentDrawingView;
    private DrawingArea topDrawingView;
    private DrawingArea bottomDrawingView;
    private PrintPanel printPanel;

    private ApplicationProperties props;

    // define all actions which are supported by the application
    private final Action actionNewBoard = 
            new Action("New board ...", "Creates a new default board", 
                       new Image("afester/javafx/examples/board/file-new.png"), 
                       e -> newBoard());
    private final Action actionLoadBoard = 
            new Action("Load board ...",       "",
                       new Image("afester/javafx/examples/board/file-open.png"),
                       e -> loadBoard());
    private final Action actionSaveBoard = 
            new Action("Save board",           "", 
                       new Image("afester/javafx/examples/board/file-save.png"),
                       e -> saveBoard());
    private final Action actionSaveBoardAs = 
            new Action("Save board as ...",    "", 
                       new Image("afester/javafx/examples/board/file-saveas.png"),
                       e -> saveBoardAs());
    private final Action actionImportSchematic = 
            new Action("Import schematic ...", "", 
                       e -> importSchematic());
    private final Action actionSynchronizeSchematic =
            new Action("Synchronize schematic ...", "", 
                       e -> synchronizeSchematic());
    private final Action actionLoadSchematicInEagle =
            new Action("Load schematic in Eagle ...", "", 
                       e -> loadSchematicInEagle());
    private final Action actionQuit =
            new Action("Quit", "", 
                       e -> stage.close());

    private final Action actionCenter =
            new Action("Center", "",
                       e -> currentDrawingView.centerContent());
    private final Action actionFitToWindow =
            new Action("Fit to Window", "", 
                       e -> currentDrawingView.fitContentToWindow());
    private final Action actionColorSettings =
            new Action("Color settings ...", "", 
                       e -> setupColors());
    private final ActionToggle actionToggleShowSvg = 
            new ActionToggle("Toggle draft / SVG", "", 
                             new Image("afester/javafx/examples/board/view-svg.png"));
    private final ActionToggle actionToggleShowTraces = 
            new ActionToggle("Show / hide traces",
                             "", new Image("afester/javafx/examples/board/view-traces.png"));
    private final ActionToggle actionToggleShowAirwires = 
            new ActionToggle("Show / hide airwires",   "", new Image("afester/javafx/examples/board/view-airwires.png"));
    private final ActionToggle actionToggleShowDimensions = 
            new ActionToggle("Show / hide dimensions", "", new Image("afester/javafx/examples/board/view-dimensions.png"));

    private final Action actionUndo =
            new Action("Undo", "", e -> {});
    private final Action actionRedo =
            new Action("Redo", "", e -> {});

    // Junction mode toggles
    private static enum MoveMode {RECONNECT, MOVE}

    private final ActionRadio<MoveMode> actionSelectJunctionMode = 
            new ActionRadio<>(
                new ActionChoice<>(MoveMode.RECONNECT, "Reconnect Trace",      "", 
                                   new Image("afester/javafx/examples/board/mode-reconnect.png")),
                new ActionChoice<>(MoveMode.MOVE, "Move Junction", "", 
                                   new Image("afester/javafx/examples/board/mode-movejunction.png")));

    private final Action actionShortestpath =
            new Action("Shortest", "",
                       new Image("afester/javafx/examples/board/net-shortest.png"),
                       e -> calculateShortestPath());
    private final Action actionResetNet =
            new Action("Reset net", "",
                       new Image("afester/javafx/examples/board/net-reset.png"),
                       e -> resetNet());
    private final Action actionCleanupNet =
            new Action("Validate/Cleanup net", "",
                       new Image("afester/javafx/examples/board/net-cleanup.png"),
                       e -> cleanupNet());
    private final Action actionDeleteSegment =
            new Action("Delete segment", "",
                       new Image("afester/javafx/examples/board/net-delsegment.png"),
                       e -> deleteSegment());
    private final Action actionShortestAll =
            new Action("Shortest all", "",
                       new Image("afester/javafx/examples/board/net-shortestall.png"),
                       e -> calculateShortestPathAll());
    private final Action actionToBridge =
            new Action("Bridge", "",
                       new Image("afester/javafx/examples/board/mode-bridge.png"),
                       e -> toBridge());
    private final Action actionToTrace =
            new Action("Trace", "",
                       new Image("afester/javafx/examples/board/mode-trace.png"),
                       e -> toTrace());

    private final Action actionAbout = new Action("About ...", "", e -> showAbout());

    // Edit mode toggles
    private static enum Interactors {SELECT, SPLIT_TRACE, EDIT_SHAPE}

    private final ActionRadio<Interactors> actionSelectInteractor = 
            new ActionRadio<>(
                new ActionChoice<>(Interactors.SELECT, "Select",      "", 
                                   new Image("afester/javafx/examples/board/mode-select.png")),
                new ActionChoice<>(Interactors.SPLIT_TRACE, "Split Trace", "", 
                                   new Image("afester/javafx/examples/board/mode-splittrace.png")),
                new ActionChoice<>(Interactors.EDIT_SHAPE, "Edit shape",  "", 
                                   new Image("afester/javafx/examples/board/mode-editshape.png")));

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
        Menu fileMenu = Action.createMenu("File", 
                          actionNewBoard, actionLoadBoard,
                          new Action.Separator(),
                          actionSaveBoard, actionSaveBoardAs,
                          new Action.Separator(),
                          actionImportSchematic, actionSynchronizeSchematic,
                          actionLoadSchematicInEagle,
                          new Action.Separator(),
                          actionQuit);

        Menu viewMenu = Action.createMenu("View", 
                          actionCenter, actionFitToWindow, actionColorSettings,
                          new Action.Separator(),
                          actionToggleShowSvg, actionToggleShowTraces,
                          actionToggleShowAirwires, actionToggleShowDimensions);

        Menu editMenu = Action.createMenu("Edit",
                         actionUndo,
                         actionRedo);
                         //new Action.Separator(),
                         //selectInteractorAction

        Menu helpMenu = Action.createMenu("Help", 
                          actionAbout);
        helpMenu.setAccelerator(KeyCombination.keyCombination("Alt+H"));

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

        logOutput.setEditable(false);
        logOutput.setFont(Font.font("Courier New",  12));
        logSplitPane.getItems().clear();
        logSplitPane.setOrientation(Orientation.VERTICAL);
        logSplitPane.getItems().addAll(tabPane, logOutput);

        SplitPane.setResizableWithParent(leftGroup, false);   // do not resize the BOM list
        splitPane.getItems().clear();
        splitPane.getItems().addAll(leftGroup, logSplitPane);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topBar);
        mainLayout.setBottom(sb);
        mainLayout.setRight(rightBar);
        mainLayout.setCenter(splitPane);

        // load application state
        props = ApplicationProperties.load();

        logSplitPane.setDividerPosition(0, props.getDouble("verticalSplitter", 0.9));
        splitPane.setDividerPosition(0, props.getDouble("leftSplitter", 0.15));

        final var width = props.getDouble("appWidth", 1024.0);
        final var height = props.getDouble("appHeight", 768.0);
        mainScene = new Scene(mainLayout, width, height);

        stage.setScene(mainScene);
        this.stage = stage;

        stage.show();

        InputStream is = getClass().getResourceAsStream("PCBius.ttf");
        Font.loadFont(is, 0);

        final var lastFile = props.getString("lastFile", null);
        if (lastFile != null) {
            loadBoard(new File(lastFile));
        }
    }


    private void setupColors() {

        StyleDialog cs = new StyleDialog(props);
        cs.show();

//        cs.shapeStyleProperty().addListener((obj, oldVal, newVal) -> {
//           System.err.println("STYLE:" + newVal); 
//        });
    }

    
    private ToolBar createRoutingToolbar() {
        // Create the toolbar
        actionSelectJunctionMode.selectedChoiceProperty().addListener((obj, oldValue, newValue) -> {
            switch(newValue) {
                case MOVE:
                    topView.setReconnectMode(false);
                    break;

                case RECONNECT:
                    topView.setReconnectMode(true);
                    break;
    
                default:
                    break;
            }
        });

        ToolBar toolBar = Action.createToolBar(
                actionSelectJunctionMode,
                new Action.Separator(),
                actionShortestpath,
                actionResetNet,
                actionCleanupNet,
                actionDeleteSegment,
                actionToBridge,
                actionToTrace,
                new Action.Separator(),
                actionShortestAll);

        toolBar.setOrientation(Orientation.VERTICAL);
        toolBar.managedProperty().bind(toolBar.visibleProperty());
        return toolBar;
    }


    private ToolBar createMainToolbar() {
        actionSelectInteractor.selectedChoiceProperty().addListener((obj, oldValue, newValue) -> {
            if (topView != null) {
                switch(newValue) {
                    case EDIT_SHAPE:
                        setShapeEditMode(true);
                        break;
                    case SELECT:
                        setShapeEditMode(false);
                        topDrawingView.setInteractor(new EditInteractor(topView));
                        break;
                    case SPLIT_TRACE:
                        setShapeEditMode(false);
                        topDrawingView.setInteractor(new SplitTraceInteractor(topView));
                        break;
                    default:
                        break;
                }
            }
        });

        ToolBar toolBar = Action.createToolBar(
                actionNewBoard,
                actionLoadBoard,
                actionSaveBoard,
                actionSaveBoardAs,
                new Action.Separator(),

                actionSelectInteractor,
                new Action.Separator(),

                actionToggleShowSvg, actionToggleShowTraces,
                actionToggleShowAirwires, actionToggleShowDimensions);
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

            // topView.showSvgProperty().bind(toggleSvgToolButton.selectedProperty());
            topView.showSvgProperty().bind(actionToggleShowSvg.selectedProperty());
            topView.showTracesProperty().bind(actionToggleShowTraces.selectedProperty());
            topView.showAirwiresProperty().bind(actionToggleShowAirwires.selectedProperty());
            topView.showDimensionsProperty().bind(actionToggleShowDimensions.selectedProperty());

            currentDrawingView = topDrawingView;
        } else if (newIdx == 1) {
            log.debug("Switch to BOTTOM tab");

            if (bottomView == null) {
                Board b = topView.getBoard();
                bottomView = new BottomBoardView(b, props);
                // bottomView.setReadOnly(true);

                bottomView.getTransforms().add(Transform.scale(-1, 1));

                bottomDrawingView = new DrawingArea();
                // bottomView.setReadOnly(true);
                bottomDrawingView.getPaper().getChildren().add(bottomView);
                bottomViewTab.setContent(bottomDrawingView);

                stage.sizeToScene();    // required to properly fit the content to the window
                bottomDrawingView.fitContentToWindow();
            }

            topView.showSvgProperty().unbind();
            topView.showTracesProperty().unbind();
            topView.showAirwiresProperty().unbind();
            topView.showDimensionsProperty().unbind();
            bottomView.showSvgProperty().bind(actionToggleShowSvg.selectedProperty());
            bottomView.showTracesProperty().bind(actionToggleShowTraces.selectedProperty());
            bottomView.showAirwiresProperty().bind(actionToggleShowAirwires.selectedProperty());
            bottomView.showDimensionsProperty().bind(actionToggleShowDimensions.selectedProperty());

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
                final Trace wire = traceView.getTrace();
                wire.setTraceType(TraceType.BRIDGE);

//                final Net net = wire.getNet();
//                // change the trace to a Bridge
//                net.changeToBridge(wire);
            }
        });

        topView.clearSelection();
    }


    /**
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


    private void loadSchematicInEagle() {
        
    }
    
    private void synchronizeSchematic() {
        
        // TODO: we need to block the UI while this task is running.
        var syncService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
//                    private int i = 0;

                    /**
                     * Append the given string to the end of the log TextArea.
                     *
                     * @param s The string to append.
                     */
                    private void appendLog(String s) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                logOutput.appendText(s);
                            }
                        });
                    }

                    @Override
                    protected Void call() throws Exception {
                        // This method is executed on the background thread.
                        // we must NOT call JavaFX methods directly from here!

                        // This would use the last value of "i" only because
                        // internally the same anonymous class instance is used
//                        for (i = 0;  i < 100;  i++) {
//                            Platform.runLater(() -> {
//                                logOutput.appendText(i + ". call\n");
//                            });
//                        }

//                          for (i = 0;  i < 100;  i++) {
//                              Platform.runLater(new Runnable() {
//                                // NOTE: We are referencing the variable "i" here.
//                                // When we do this here, the access occurs on the 
//                                // background thread and thus is in sequence with 
//                                // the for-loop:
//                                private final String s = i + ". call";
//                                // the private instance variable "s" is now part of the
//                                // current Runnable instance and independent
//                                // of the background thread.
//
//                                @Override
//                                public void run() {
//                                    // This method is executed on the Application Thread.
//                                    // We could still reference the "i" variable here,
//                                    // but it wil have been modified meanwhile!!!!
//                                    // Hence we must use the instance variable 
//                                    // from the current Runnable object.
//                                    logOutput.appendText(s + "\n");
//                                }
//                              });
//                          }

//                    for (i = 0;  i < 100;  i++) {
//                        appendLog(i + ". call");
//                    }

                        Board board = topView.getBoard();
                        appendLog("Synchronizing board: " + board.getFileName() + "\n");
                        appendLog("With schematic     : " + board.getSchematicFile() + "\n");
                        try {
                            board.synchronizeSchematic(log -> appendLog(log));
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        appendLog("Board synchronized." + "\n");
                    	
                        return null;
                    }
                };
            }
        };

        syncService.start();
//
//        // Track the message of the sync Service.
//        // TODO: How to properly remove the listener when finished?
//        syncService.messageProperty().addListener((obj, oldVal, newVal) -> {
//            logOutput.appendText(newVal);
//        });
    }


    private void newBoard() {
        Board board = new Board();
        setupUi(board);
    }

    private void setupUi(Board board) {
        final long startTime = System.currentTimeMillis();

        log.info("Setting up UI ...");
        stage.setTitle(String.format("%s (%s)", board.getFileName(), board.getSchematicFile()));
        topView = new TopBoardView(board, props);

        topDrawingView.getPaper().getChildren().clear();
        topDrawingView.getPaper().getChildren().add(topView);
        topDrawingView.setInteractor(new EditInteractor(topView));

        bottomView = null;

        bomView = new BomView(topView);
        leftGroup.getChildren().clear();
        leftGroup.getChildren().add(bomView);

        actionSelectJunctionMode.setEnabled(true); // reconnectTraceModeToolButton.setDisable(false);
                                                   // moveJunctionToolButton.setDisable(false);
        actionShortestpath.setEnabled(true); // shortestPathButton.setDisable(false);
        actionResetNet.setEnabled(true); // resetNetButton.setDisable(false);
        actionCleanupNet.setEnabled(true);  // cleanupNetButton.setDisable(false);
        actionDeleteSegment.setEnabled(true);// deleteSegmentButton.setDisable(false);
        actionShortestAll.setEnabled(true); // shortestAllButton.setDisable(false);
        actionToBridge.setEnabled(true); // toBridgeToolButton.setDisable(false);
        actionToTrace.setEnabled(true); // toTraceToolButton.setDisable(false);

        actionNewBoard.setEnabled(true);
        actionLoadBoard.setEnabled(true);
        actionSaveBoard.setEnabled(true);
        actionSaveBoardAs.setEnabled(true);
        actionToggleShowSvg.setEnabled(true);
        actionToggleShowTraces.setEnabled(true);
        actionToggleShowAirwires.setEnabled(true);
        actionToggleShowDimensions.setEnabled(true);
        actionSelectInteractor.setEnabled(true);

        stage.sizeToScene();
        switchTab(0);   // ???????????
        topDrawingView.fitContentToWindow();

        log.info(() -> String.format("UI setup done in %s ms.\n", System.currentTimeMillis() - startTime));
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
        logOutput.appendText("Saved " + board.getFileName() + "\n");
    }

    private void saveBoardAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Board as ...");
        File result = fileChooser.showSaveDialog(stage);
        if (result != null) {
            Board board = topView.getBoard();
            board.saveAs(result);
            logOutput.appendText("Saved " + board.getFileName() + "\n");
        }
    }

    private void importSchematic() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import schematic ...");
        File result = fileChooser.showOpenDialog(stage);
        if (result != null) {
            logOutput.appendText("Importing " + result.getAbsolutePath() + "\n");

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
            props.setDouble("leftSplitter", splitPane.getDividerPositions()[0]);
            props.setDouble("verticalSplitter", logSplitPane.getDividerPositions()[0]);

            props.save();
        }
    }


    public static void boardMain(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }
}
