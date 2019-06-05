package afester.javafx.examples.board;

import java.io.File;

import afester.javafx.components.StatusBar;
import afester.javafx.components.ToolbarButton;
import afester.javafx.components.ToolbarToggleButton;
import afester.javafx.examples.board.model.AbstractNode;
import afester.javafx.examples.board.model.Board;
import afester.javafx.examples.board.model.EagleNetImport;
import afester.javafx.examples.board.model.Net;
import afester.javafx.examples.board.model.NetImport;
import afester.javafx.examples.board.model.Trace;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
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
    private BoardView bv;
    private Tab mirrorTab;
    private BoardView bottomView;

    @Override
    public void start(Stage stage){


        //NetImport ni = new EagleNetImport();
        //Board board = ni.importFile(new File("schem.xml"));

        bv = new BoardView();

        Board board = new Board();
//        board.load(new File("small.brd"));
        board.load(new File("large.brd"));
//        board.load(new File("first.brd"));
        bv.setBoard(board);
        

        // The pane is exactly the size of the center component. Its children (which is the BoardView) are clipped
        // and the view can be panned and zoomed.
        DrawingView drawingView = new DrawingView(bv);

        TabPane tabPane = new TabPane();
        tabPane.getSelectionModel().selectedIndexProperty().addListener((obj, oldIdx, newIdx) -> switchTab(newIdx.intValue()));
        Tab editTab = new Tab("Top view");
        editTab.setContent(drawingView);
        mirrorTab = new Tab("Bottom view");
        // mirrorTab.setOnSelectionChanged(e -> System.err.println(e.getTarget()));
        tabPane.getTabs().addAll(editTab, mirrorTab);

        // Create the menu bar

        Menu fileMenu = new Menu("File");
        MenuItem menuItem0 = new MenuItem("New board ...");
        menuItem0.setOnAction(e -> newBoard());
        MenuItem menuItem1 = new MenuItem("Open board ...");
        menuItem1.setOnAction(e -> openBoard());
        MenuItem menuItem2 = new MenuItem("Save board");
        menuItem2.setOnAction(e -> saveBoard());
        MenuItem menuItem3 = new MenuItem("Save board as ...");
        menuItem3.setOnAction(e -> saveBoardAs());
        MenuItem menuItem4 = new MenuItem("Import schematic ...");
        menuItem4.setOnAction(e -> importSchematic());
        MenuItem menuItem5 = new MenuItem("Synchronize schematic ...");
        menuItem5.setOnAction(e -> synchronizeSchematic());
        MenuItem menuItem6 = new MenuItem("Quit");
        menuItem6.setOnAction(e -> stage.close());

        fileMenu.getItems().addAll(menuItem0, menuItem1, menuItem2, menuItem3, menuItem4, menuItem5, menuItem6);

        Menu viewMenu = new Menu("View");
        MenuItem viewItem1 = new MenuItem("Center");
        viewItem1.setOnAction(e -> drawingView.centerContent());
        MenuItem viewItem2 = new MenuItem("Fit to Window");
        viewItem2.setOnAction(e -> drawingView.fitContentToWindow());
        viewMenu.getItems().addAll(viewItem1, viewItem2);

        Menu editMenu = new Menu("Edit");
        MenuItem editItem1 = new MenuItem("Select");
        MenuItem editItem2 = new MenuItem("Trace");
        editMenu.getItems().addAll(editItem1, editItem2);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, viewMenu, editMenu);

        // Create the toolbar
        final Button shortestPathButton = new ToolbarButton("Shortest", "afester/javafx/examples/board/net-shortest.png");
        shortestPathButton.setOnAction(e -> calculateShortestPath());

        final Button shortestAllButton = new ToolbarButton("Shortest all", "afester/javafx/examples/board/net-shortestall.png");
        shortestAllButton.setOnAction(e -> calculateShortestPathAll());

        final Button traceToBridge = new ToolbarButton("Bridge", "afester/javafx/examples/board/net-tracetobridge.png");
        traceToBridge.setOnAction(e -> traceToBridge());

        final Button resetNetButton = new ToolbarButton("Reset net", "afester/javafx/examples/board/net-reset.png");
        resetNetButton.setOnAction(e -> resetNet());

        final Button cleanupNetButton = new ToolbarButton("Validate/Cleanup net", "afester/javafx/examples/board/net-cleanup.png");
        cleanupNetButton.setOnAction(e -> cleanupNet());

        final Button deleteSegmentButton = new ToolbarButton("Delete segment", "afester/javafx/examples/board/net-delsegment.png");
        deleteSegmentButton.setOnAction(e -> deleteSegment());

        
        final Interactor editInteractor = new EditInteractor(bv);
        final Interactor traceInteractor = new TraceInteractor(bv);
        // final Interactor editTraceInteractor = new EditTraceInteractor(bv);
        final Interactor splitTraceInteractor = new SplitTraceInteractor(bv);
        
        ToggleGroup toggleGroup = new ToggleGroup();
        ToolbarToggleButton selectToolButton = new ToolbarToggleButton("Select", "afester/javafx/examples/board/mode-select.png");
        selectToolButton.selectedProperty().addListener((value, oldValue, newValue) -> {
            if (newValue) {
                bv.setInteractor(editInteractor);
            }
        });
        ToolbarToggleButton traceToolButton = new ToolbarToggleButton("Trace", "afester/javafx/examples/board/mode-trace.png");
        traceToolButton.selectedProperty().addListener((value, oldValue, newValue) -> {
            if (newValue) {
                bv.setInteractor(traceInteractor);   
            }
        });
//        ToolbarToggleButton editTraceToolButton = new ToolbarToggleButton("Edit Trace", "afester/javafx/examples/board/mode-edittrace.png");
//        editTraceToolButton.selectedProperty().addListener((value, oldValue, newValue) -> {
//            if (newValue) {
//                bv.setInteractor(editTraceInteractor);   
//            }
//        });
        ToolbarToggleButton splitTraceToolButton = new ToolbarToggleButton("Split Trace", "afester/javafx/examples/board/mode-splittrace.png");
        splitTraceToolButton.selectedProperty().addListener((value, oldValue, newValue) -> {
            if (newValue) {
                bv.setInteractor(splitTraceInteractor);   
            }
        });

        selectToolButton.setToggleGroup(toggleGroup);
        traceToolButton.setToggleGroup(toggleGroup);
        // editTraceToolButton.setToggleGroup(toggleGroup);
        splitTraceToolButton.setToggleGroup(toggleGroup);

        selectToolButton.setSelected(true);

        ToolBar toolBar = new ToolBar(
                new ToolbarButton("New board", "afester/javafx/examples/board/file-new.png"),
                new ToolbarButton("Open board", "afester/javafx/examples/board/file-open.png"),
                new ToolbarButton("Save board", "afester/javafx/examples/board/file-save.png"),
                new ToolbarButton("Save board as", "afester/javafx/examples/board/file-saveas.png"),
                new Separator(),
                selectToolButton,
                traceToolButton,
                // editTraceToolButton,
                splitTraceToolButton,
                new Separator(),
                shortestPathButton,
                shortestAllButton,
                traceToBridge,
                resetNetButton,
                cleanupNetButton,
                deleteSegmentButton
            );

        VBox topBar = new VBox();
        topBar.getChildren().addAll(menuBar, toolBar);
        
        StatusBar sb = new StatusBar();
        sb.textProperty().bindBidirectional(bv.selectedObjectProperty(), new StringConverter<Interactable>() {

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
        drawingView.fitContentToWindow();
    }


    private void switchTab(int newIdx) {
        if (newIdx == 0) {
            System.err.println("Switch to EDIT tab");
        } else if (newIdx == 1) {
            System.err.println("Switch to MIRROR tab");

            if (bottomView == null) {
                Board b = bv.getBoard();
                bottomView = new BoardView(b);
                bottomView.setReadOnly(true);

                bottomView.getTransforms().add(Transform.scale(-1, 1));
    
                DrawingView mirrorView = new DrawingView(bottomView);
                mirrorTab.setContent(mirrorView);
                mirrorView.centerContent();
            }
        }
    }


    private void cleanupNet() {
        Interactable selectedObject = bv.getSelectedObject();
        if (selectedObject instanceof Trace) {
            bv.clearSelection();
            Trace trace = (Trace) selectedObject;
            Net net = trace.getNet();
            System.err.println("Cleaning up " + net);
            
            net.cleanup();
        }
    }


    private void deleteSegment() {
        Interactable selectedObject = bv.getSelectedObject();
        if (selectedObject instanceof Trace) {
            bv.clearSelection();
            
            Trace trace = (Trace) selectedObject;

            Net net  = trace.getNet();
            AbstractNode from = trace.getFrom();
            AbstractNode to = trace.getTo();

            if (from.traceStarts.size() > 1 || to.traceEnds.size() > 1) {
                System.err.println("Currently only intermediate traces can be removed");
                return;
            }

            System.err.println("Removing segment ...");
            net.removeTraceAndFrom(trace);

            // re-render board
            bv.setBoard(bv.getBoard());
        }
    }


    private void resetNet() {
        Interactable selectedObject = bv.getSelectedObject();
        if (selectedObject instanceof TraceView) {
            bv.clearSelection();
            TraceView wire = (TraceView) selectedObject;
            Net net = wire.getTrace().getNet();
            System.err.printf("NET: %s\n", net.getName());

            // calculate the shortest path of the net
            net.resetNet();

            // re-render board
            //bv.setBoard(bv.getBoard());
        }
    }


    /**
     * Converts the currently selected trace into a bridge wire.
     */
    private void traceToBridge() {
        Interactable obj = bv.getSelectedObject();
        if (obj != null && obj instanceof Trace) {
            Trace t = (Trace) obj;
            t.setAsBridge();
        }
    }

    /**
     * Calculates the shortest path for the currently selected net. 
     */
    private void calculateShortestPath() {
        Interactable selectedObject = bv.getSelectedObject();
        if (selectedObject instanceof Trace) {
            bv.clearSelection();

            Trace trace = (Trace) selectedObject;
            Net net = (Net) trace.getNet();
            System.err.printf("NET: %s\n", net.getName());

            // calculate the shortest path of the net
            net.calculateShortestPath();

            // re-render board
            bv.setBoard(bv.getBoard());
        }
    }


    /**
     * Calculates the shortest path for all nets.
     */
    private void calculateShortestPathAll() {
        bv.clearSelection();

        // calculate the shortest path for all nets
        bv.getBoard().getNets().values().forEach(net -> net.calculateShortestPath());

        // re-render board
        bv.setBoard(bv.getBoard());
    }


    private void synchronizeSchematic() {
        String schematicFile = bv.getBoard().getSchematicFile();
        System.err.println("Synchronizing " + schematicFile);
        NetImport ni = new EagleNetImport();
        Board updatedBoard = ni.importFile(new File(schematicFile));
        Board currentBoard = bv.getBoard();
        currentBoard.update(updatedBoard);

        bv.setBoard(currentBoard);  // re-render board
    }


    private void newBoard() {
        Board board = new Board();
        bv.setBoard(board);
    }


    private void openBoard() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Board ...");
        File result = fileChooser.showOpenDialog(stage);
        if (result != null) {
            Board board = new Board();
            board.load(result);
            bv.setBoard(board);
        }
    }

    private void saveBoard() {
        Board board = bv.getBoard();
        board.save();
    }

    private void saveBoardAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Board as ...");
        File result = fileChooser.showSaveDialog(stage);
        if (result != null) {
            Board board = bv.getBoard();
            board.saveAs(result);
        }
    }

    private void importSchematic() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import schematic ...");
        File result = fileChooser.showOpenDialog(stage);
        if (result != null) {
            System.err.println("Importing " + result.getAbsolutePath());
            NetImport ni = new EagleNetImport();
            Board board = ni.importFile(result);
            bv.setBoard(board);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }
}
