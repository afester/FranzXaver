package afester.javafx.examples.board;

import java.io.File;

import afester.javafx.components.StatusBar;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class BreadBoardEditor extends Application {

    private Stage stage;
    private BoardView bv;

    @Override
    public void start(Stage stage){


        //NetImport ni = new EagleNetImport();
        //Board board = ni.importFile(new File("schem.xml"));

        bv = new BoardView();
        bv.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(0), new Insets(0))));

        Board board = new Board();
//        board.load(new File("small.brd"));
        board.load(new File("large.brd"));
//        board.load(new File("first.brd"));
        bv.setBoard(board);
        

        // The pane is exactly the size of the center component. Its children (which is the BoardView) are clipped
        // and the view can be panned and zoomed.
        DrawingView drawingView = new DrawingView(bv);

        // Create the menu bar

        Menu fileMenu = new Menu("File");
        MenuItem menuItem0 = new MenuItem("New board ...");
        menuItem0.setOnAction(e -> newBoard());
        MenuItem menuItem1 = new MenuItem("Open board ...");
        menuItem1.setOnAction(e -> openBoard());
        MenuItem menuItem2 = new MenuItem("Save board ...");
        menuItem2.setOnAction(e -> saveBoard());
        MenuItem menuItem3 = new MenuItem("Import schematic ...");
        menuItem3.setOnAction(e -> importSchematic());
        MenuItem menuItem4 = new MenuItem("Synchronize schematic ...");
        menuItem4.setOnAction(e -> synchronizeSchematic());
        MenuItem menuItem5 = new MenuItem("Quit");
        menuItem5.setOnAction(e -> stage.close());

        fileMenu.getItems().addAll(menuItem0, menuItem1, menuItem2, menuItem3, menuItem4, menuItem5);

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
        final Button shortestPathButton = new Button("Shortest");
        shortestPathButton.setOnAction(e -> calculateShortestPath());

        final Button resetNetButton = new Button("Shortest all");
        resetNetButton.setOnAction(e -> calculateShortestPathAll());

        ToolBar toolBar = new ToolBar(
                new Button("New"),
                new Button("Open"),
                new Button("Save"),
                new Separator(),
                shortestPathButton,
                resetNetButton
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
        mainLayout.setCenter(drawingView);

        Scene mainScene = new Scene(mainLayout, 800, 600);

        stage.setScene(mainScene);
        this.stage = stage;

        stage.show();
        drawingView.fitContentToWindow();
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Board ...");
        File result = fileChooser.showSaveDialog(stage);
        if (result != null) {
            Board board = bv.getBoard();
            board.save(result);
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
