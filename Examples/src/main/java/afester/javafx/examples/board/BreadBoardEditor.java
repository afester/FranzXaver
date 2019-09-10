package afester.javafx.examples.board;

import java.io.File;

import afester.javafx.components.StatusBar;
import afester.javafx.components.ToolbarButton;
import afester.javafx.components.ToolbarToggleButton;
import afester.javafx.examples.board.model.AbstractNode;
import afester.javafx.examples.board.model.AbstractWire;
import afester.javafx.examples.board.model.Board;
import afester.javafx.examples.board.model.EagleNetImport;
import afester.javafx.examples.board.model.Net;
import afester.javafx.examples.board.model.NetImport;
import afester.javafx.examples.board.model.Trace;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class BreadBoardEditor extends Application {

    private Stage stage;
    
    private BoardView topView;
    private BoardView bottomView;
    
    private Tab editTab;
    private Tab bottomViewTab;
    private Tab printTab;

    private DrawingView currentDrawingView;
    private DrawingView topDrawingView;
    private DrawingView bottomDrawingView;
    private PrintPanel printPanel;

    @Override
    public void start(Stage stage){

        Board board = new Board();
        board.load(new File("supply.brd"));
//        board.load(new File("small.brd"));
//        board.load(new File("large.brd"));
//        board.load(new File("first.brd"));

        topView = new BoardView(board);
        topView.showBoardDimensions(true);

        // The pane is exactly the size of the center component. Its children (which is the BoardView) are clipped
        // and the view can be panned and zoomed.
        topDrawingView = new DrawingView(topView);

        editTab = new Tab("Top view");
        editTab.setClosable(false);
        editTab.setContent(topDrawingView);

        bottomViewTab = new Tab("Bottom view");
        bottomViewTab.setClosable(false);
        
        printTab = new Tab("Print preview");
        printTab.setClosable(false);

        TabPane tabPane = new TabPane();
        tabPane.getSelectionModel().selectedIndexProperty().addListener((obj, oldIdx, newIdx) -> switchTab(newIdx.intValue()));
        tabPane.getTabs().addAll(editTab, bottomViewTab, printTab);

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
        viewMenu.getItems().addAll(viewItem1, viewItem2);

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

        // Create the toolbar
        final Button shortestPathButton = new ToolbarButton("Shortest", "afester/javafx/examples/board/net-shortest.png");
        shortestPathButton.setOnAction(e -> calculateShortestPath());

        final Button shortestAllButton = new ToolbarButton("Shortest all", "afester/javafx/examples/board/net-shortestall.png");
        shortestAllButton.setOnAction(e -> calculateShortestPathAll());

        final Button resetNetButton = new ToolbarButton("Reset net", "afester/javafx/examples/board/net-reset.png");
        resetNetButton.setOnAction(e -> resetNet());

        final Button cleanupNetButton = new ToolbarButton("Validate/Cleanup net", "afester/javafx/examples/board/net-cleanup.png");
        cleanupNetButton.setOnAction(e -> cleanupNet());

        final Button deleteSegmentButton = new ToolbarButton("Delete segment", "afester/javafx/examples/board/net-delsegment.png");
        deleteSegmentButton.setOnAction(e -> deleteSegment());

//        final Button printButton = new ToolbarButton("Print", "afester/javafx/examples/board/print.png");
//        printButton.setOnAction(e -> printLayout());

        final Interactor editInteractor = new EditInteractor(topView);
        final Interactor traceInteractor = new TraceInteractor(topView);
        // final Interactor editTraceInteractor = new EditTraceInteractor(bv);
        final Interactor splitTraceInteractor = new SplitTraceInteractor(topView);
        final Interactor editShapeInteractor = new EditShapeInteractor(topView);

        ToggleGroup toggleGroup = new ToggleGroup();
        final ToolbarToggleButton selectToolButton = new ToolbarToggleButton("Select", "afester/javafx/examples/board/mode-select.png");
        selectToolButton.selectedProperty().addListener((value, oldValue, newValue) -> {
            if (newValue) {
                topView.setInteractor(editInteractor);
            }
        });
        final ToolbarToggleButton toTraceToolButton = new ToolbarToggleButton("Trace", "afester/javafx/examples/board/mode-trace.png");
        toTraceToolButton.selectedProperty().addListener((value, oldValue, newValue) -> {
            if (newValue) {
                topView.setInteractor(traceInteractor);   
            }
        });

        final ToolbarToggleButton toBridgeToolButton = new ToolbarToggleButton("Bridge", "afester/javafx/examples/board/mode-bridge.png");
        toBridgeToolButton.selectedProperty().addListener((value, oldValue, newValue) -> {
            if (newValue) {
                topView.setInteractor(traceInteractor);   
            }
        });

        final ToolbarToggleButton toAirwireToolButton = new ToolbarToggleButton("Airwire", "afester/javafx/examples/board/mode-airwire.png");
        toAirwireToolButton.selectedProperty().addListener((value, oldValue, newValue) -> {
            if (newValue) {
                topView.setInteractor(traceInteractor);   
            }
        });

        final ToolbarToggleButton splitTraceToolButton = new ToolbarToggleButton("Split Trace", "afester/javafx/examples/board/mode-splittrace.png");
        splitTraceToolButton.selectedProperty().addListener((value, oldValue, newValue) -> {
            if (newValue) {
                topView.setInteractor(splitTraceInteractor);   
            }
        });
        ToolbarToggleButton editShapeToolButton = new ToolbarToggleButton("Edit shape", "afester/javafx/examples/board/mode-editshape.png");
        editShapeToolButton.selectedProperty().addListener((value, oldValue, newValue) -> {
            if (newValue) {
                topView.setInteractor(editShapeInteractor);
            }
        });

        selectToolButton.setToggleGroup(toggleGroup);
        toTraceToolButton.setToggleGroup(toggleGroup);
        toBridgeToolButton.setToggleGroup(toggleGroup);
        splitTraceToolButton.setToggleGroup(toggleGroup);
        editShapeToolButton.setToggleGroup(toggleGroup);

        selectToolButton.setSelected(true);

        
        final ToolbarToggleButton reconnectTraceModeToolButton = new ToolbarToggleButton("Reconnect Trace", "afester/javafx/examples/board/mode-reconnect.png");
//        splitTraceToolButton.selectedProperty().addListener((value, oldValue, newValue) -> {
//            if (newValue) {
//                topView.setInteractor(splitTraceInteractor);   
//            }
//        });
        ToolbarToggleButton moveJunctionToolButton = new ToolbarToggleButton("Move Junction", "afester/javafx/examples/board/mode-movejunction.png");
//        editShapeToolButton.selectedProperty().addListener((value, oldValue, newValue) -> {
//            if (newValue) {
//                topView.setInteractor(editShapeInteractor);
//            }
//        });
        ToggleGroup junctionModeToggleGroup = new ToggleGroup();

        reconnectTraceModeToolButton.setToggleGroup(junctionModeToggleGroup);
        moveJunctionToolButton.setToggleGroup(junctionModeToggleGroup);
        moveJunctionToolButton.setSelected(true);

        ToolBar toolBar = new ToolBar(
                new ToolbarButton("New board", "afester/javafx/examples/board/file-new.png"),
                new ToolbarButton("Open board", "afester/javafx/examples/board/file-open.png"),
                new ToolbarButton("Save board", "afester/javafx/examples/board/file-save.png"),
                new ToolbarButton("Save board as", "afester/javafx/examples/board/file-saveas.png"),
                new Separator(),

                selectToolButton,
                toTraceToolButton,
                toBridgeToolButton,
                toAirwireToolButton,
                splitTraceToolButton,
                editShapeToolButton,
                new Separator(),

                reconnectTraceModeToolButton,
                moveJunctionToolButton,
                new Separator(),

                shortestPathButton,
                resetNetButton,
                cleanupNetButton,
                deleteSegmentButton,
                new Separator(),

                shortestAllButton
            );

        VBox topBar = new VBox();
        topBar.getChildren().addAll(menuBar, toolBar);
        
        StatusBar sb = new StatusBar();
        sb.textProperty().bindBidirectional(topView.selectedObjectProperty(), new StringConverter<Interactable>() {

            @Override
            public Interactable fromString(String string) {
                return null;
            }

            @Override
            public String toString(Interactable object) {
                if (object != null) {
                    return object.getRepr();
                }
                return "No selection.";
            }
            
        });

        HBox leftBar = new HBox();
        leftBar.getChildren().add(new Text("L"));
        leftBar.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), new Insets(0))));
        HBox rightBar = new HBox();
        rightBar.getChildren().add(new Text("L"));
        rightBar.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(0), new Insets(0))));

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topBar);
        mainLayout.setBottom(sb);
        mainLayout.setLeft(leftBar);
        mainLayout.setRight(rightBar);
        mainLayout.setCenter(tabPane);

        Scene mainScene = new Scene(mainLayout, 1024, 768);

        stage.setScene(mainScene);
        this.stage = stage;

        stage.show();
        //stage.sizeToScene();
        topDrawingView.fitContentToWindow();
    }


    private void showAbout() {
        Dialog<Void> dialog = new AboutDialog();
        dialog.showAndWait();
    }


    private void switchTab(int newIdx) {
        if (newIdx == 0) {
            System.err.println("Switch to TOP tab");
            currentDrawingView = topDrawingView;
        } else if (newIdx == 1) {
            System.err.println("Switch to BOTTOM tab");

            if (bottomView == null) {
                Board b = topView.getBoard();
                bottomView = new BoardView(b, true);
                bottomView.showBoardDimensions(true);
                bottomView.setReadOnly(true);

                bottomView.getTransforms().add(Transform.scale(-1, 1));
    
                final Group g = new Group(bottomView);
                
                bottomDrawingView = new DrawingView(g);
                bottomViewTab.setContent(bottomDrawingView);

                stage.sizeToScene();    // required to properly fit the content to the window
                bottomDrawingView.fitContentToWindow();
            }
            
            currentDrawingView = bottomDrawingView;
        } else if (newIdx == 2) {
            System.err.println("Switch to PRINT tab");

            if (printPanel == null) {
                printPanel = new PrintPanel(topView.getBoard(), stage);
                printTab.setContent(printPanel);

                stage.sizeToScene();    // required to properly fit the content to the window
                printPanel.getDrawingView().fitContentToWindow();
            }

            currentDrawingView = printPanel.getDrawingView();
        }
    }


    private void cleanupNet() {
        Interactable selectedObject = topView.getSelectedObject();
        if (selectedObject instanceof Trace) {
            topView.clearSelection();
            Trace trace = (Trace) selectedObject;
            Net net = trace.getNet();
            System.err.println("Cleaning up " + net);
            
            net.cleanup();
        }
    }


    private void deleteSegment() {
        Interactable selectedObject = topView.getSelectedObject();
        if (selectedObject instanceof TraceView) {
            topView.clearSelection();

            AbstractWire trace = ((TraceView) selectedObject).getTrace();
            Net net  = trace.getNet();
            AbstractNode from = trace.getFrom();
            AbstractNode to = trace.getTo();

            if (from.traceStarts.size() > 1 || to.traceEnds.size() > 1) {
                System.err.println("Currently only intermediate traces can be removed");
                return;
            }

            System.err.println("Removing segment ...");
            net.removeTraceAndFrom(trace);
        }
    }


    /**
     * Removes all junctions and all wires from the net and recreates the net by
     * connecting all pads of the net with the shortest path algorithm. 
     */
    private void resetNet() {
        Interactable selectedObject = topView.getSelectedObject();
        if (selectedObject instanceof TraceView) {
            topView.clearSelection();
            TraceView wire = (TraceView) selectedObject;
            Net net = wire.getTrace().getNet();

            // calculate the shortest path of the net
            net.resetNet();
        }
    }


    /**
     * Converts the currently selected trace into a bridge wire.
     */
    private void traceToBridge() {
        Interactable obj = topView.getSelectedObject();
        if (obj != null && obj instanceof TraceView) {
            TraceView traceView = (TraceView) obj;

            if (traceView.getTrace() instanceof Trace) {
                ((Trace) traceView.getTrace()).setAsBridge();    
            }
            
        }
    }

    /**
     * Calculates the shortest path for the currently selected net. 
     */
    private void calculateShortestPath() {
        Interactable selectedObject = topView.getSelectedObject();
        if (selectedObject instanceof Trace) {
            topView.clearSelection();

            Trace trace = (Trace) selectedObject;
            Net net = (Net) trace.getNet();
            System.err.printf("NET: %s\n", net.getName());

            // calculate the shortest path of the net
            net.calculateShortestPath();

            // re-render board
           // bv.setBoard(bv.getBoard());
        }
    }


    /**
     * Calculates the shortest path for all nets.
     */
    private void calculateShortestPathAll() {
        topView.clearSelection();

        // calculate the shortest path for all nets
        topView.getBoard().getNets().values().forEach(net -> net.calculateShortestPath());

        // re-render board
        // bv.setBoard(bv.getBoard());
    }


    private void synchronizeSchematic() {
        Board board = topView.getBoard();
        board.synchronizeSchematic();
    }


    private void newBoard() {
        Board board = new Board();
        topView.setBoard(board);
        topView.showBoardDimensions(true);
        bottomView = null;
    }


    private void loadBoard() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Board ...");
        File result = fileChooser.showOpenDialog(stage);
        if (result != null) {
            Board board = new Board();
            board.load(result);
            topView.setBoard(board);
            bottomView = null;
        }
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
            System.err.println("Importing " + result.getAbsolutePath());

            NetImport ni = new EagleNetImport(result);
            Board board = topView.getBoard();
            board.importSchematic(ni);
            topView.setBoard(board);
            bottomView = null;
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }
}
