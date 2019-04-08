package afester.javafx.examples.board;

import java.io.File;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
        MenuItem menuItem0 = new MenuItem("New Board ...");
        menuItem0.setOnAction(e -> newBoard());
        MenuItem menuItem1 = new MenuItem("Open Board ...");
        menuItem1.setOnAction(e -> openBoard());
        MenuItem menuItem2 = new MenuItem("Save Board ...");
        menuItem2.setOnAction(e -> saveBoard());
        MenuItem menuItem3 = new MenuItem("Import Schematic ...");
        menuItem3.setOnAction(e -> importSchematic());

        fileMenu.getItems().addAll(menuItem0, menuItem1, menuItem2, menuItem3);

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
        
        HBox statusBar = new HBox();
        statusBar.getChildren().add(new Text("Ready."));
        statusBar.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(0), new Insets(0))));
        HBox leftBar = new HBox();
        leftBar.getChildren().add(new Text("L"));
        leftBar.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), new Insets(0))));
        HBox rightBar = new HBox();
        rightBar.getChildren().add(new Text("L"));
        rightBar.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(0), new Insets(0))));

//        BorderPane mainLayout = new BorderPane();
//        mainLayout.setTop(menuBar);
//        mainLayout.setBottom(statusBar);
//        mainLayout.setLeft(leftBar);
//        mainLayout.setRight(rightBar);
//        mainLayout.setCenter(drawingView);

//        Scene mainScene = new Scene(mainLayout, 800, 600);
        Scene mainScene = new Scene(drawingView, 800, 600);

        stage.setScene(mainScene);
        this.stage = stage;

        stage.show();
        drawingView.fitContentToWindow();
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
