package afester.javafx.examples.layouts;

import afester.javafx.examples.Example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;



@Example(desc = "Basic JavaFX layout panels",
         cat  = "Basic JavaFX")
public class LayoutExamples extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private HBox hboxLayout = new HBox();
    private VBox vboxLayout = new VBox();
    private FlowPane flowLayout = new FlowPane();
    private GridPane gridLayout = new GridPane();
    private BorderPane borderLayout = new BorderPane();
    private StackPane stackLayout = new StackPane();
    private TilePane tileLayout = new TilePane();

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX layouts samples");

        Tab hboxTab = new Tab();
        hboxTab.setText("HBox");
        hboxTab.setClosable(false);
        hboxLayout.setBackground(createBackground(Color.LIGHTGREEN));
        hboxTab.setContent(hboxLayout);

        Tab vboxTab = new Tab();
        vboxTab.setText("VBox");
        vboxTab.setClosable(false);
        vboxLayout.setBackground(createBackground(Color.ORANGE));
        vboxTab.setContent(vboxLayout);

        Tab flowPaneTab = new Tab();
        flowPaneTab.setText("FlowPane");
        flowPaneTab.setClosable(false);
        flowLayout.setBackground(createBackground(Color.LIGHTSKYBLUE));
        flowPaneTab.setContent(flowLayout);

        Tab gridPaneTab = new Tab("GridPane");
        gridPaneTab.setClosable(false);
        gridLayout.setBackground(createBackground(Color.LIGHTCORAL));
        gridLayout.setGridLinesVisible(true);
        gridPaneTab.setContent(gridLayout);

        Tab borderPaneTab = new Tab();
        borderPaneTab.setText("BorderPane");
        borderPaneTab.setClosable(false);
        borderLayout.setBackground(createBackground(Color.LIGHTYELLOW));
        borderPaneTab.setContent(borderLayout);

        Tab stackPaneTab = new Tab();
        stackPaneTab.setText("StackPane");
        stackPaneTab.setClosable(false);
        stackLayout.setBackground(createBackground(Color.YELLOW));
        stackPaneTab.setContent(stackLayout);

        Tab tilePaneTab = new Tab("TilePane");
        tilePaneTab.setClosable(false);
        tileLayout.setBackground(createBackground(Color.LIGHTGOLDENRODYELLOW));
        tilePaneTab.setContent(tileLayout);

        updateChildren(false);
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(hboxTab, vboxTab, flowPaneTab, gridPaneTab, borderPaneTab, 
                                 stackPaneTab, tilePaneTab);

        VBox optionsPanel = new VBox();
        CheckBox componentType = new CheckBox("Use Buttons instead of Rectangles");
        componentType.selectedProperty().addListener(
            (observable, oldValue, newValue) -> updateChildren(newValue) );
        optionsPanel.getChildren().add(componentType);
        optionsPanel.setPadding(new Insets(10));

        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(tabPane);
        mainLayout.setLeft(optionsPanel);

        // show the generated scene graph
        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    

    private void updateChildren(boolean useButtons) {
        hboxLayout.getChildren().clear();
        hboxLayout.getChildren().addAll(createChildren(useButtons));

        vboxLayout.getChildren().clear();
        vboxLayout.getChildren().addAll(createChildren(useButtons));

        flowLayout.getChildren().clear();
        flowLayout.getChildren().addAll(createChildren(useButtons));

        List<Node> contents1 = createChildren(useButtons);
        gridLayout.getChildren().clear();
        gridLayout.add(contents1.get(0), 0, 0);
        gridLayout.add(contents1.get(1), 1, 0);
        gridLayout.add(contents1.get(2), 0, 1, 2, 1);
        
        List<Node> contents = createChildren(useButtons);
        borderLayout.getChildren().clear();
        borderLayout.setLeft(contents.get(0));
        borderLayout.setTop(contents.get(1));
        borderLayout.setRight(contents.get(2));
        borderLayout.setBottom(contents.get(3));
        borderLayout.setCenter(contents.get(4));
    
        stackLayout.getChildren().clear();
        stackLayout.getChildren().addAll(createChildren(useButtons));

        tileLayout.getChildren().clear();
        tileLayout.getChildren().addAll(createChildren(useButtons));
    }
    
    
    private List<Node> createChildren(boolean useButtons) {
        if (useButtons) {
            return createNodes();
        }
        
        return createVariableNodes();
    }


    private Background createBackground(Color col) {
        return new Background(
                new BackgroundFill(col, new CornerRadii(0), new Insets(0)));
    }


    private static final String[] buttonTexts = 
            new String[] {"Node",   "NodeABC",    "NodeX",        "Hello", "Hello World",
                          "Button", "LongButton", "LongerButton", "X",     "ABCD"};

    /**
     * Creates a number of buttons with different widths.
     *
     * @return A list of Button objects with different widths.
     */
    private List<Node> createNodes() {
        List<Node> result = new ArrayList<>();
        for (int i = 0;  i < 10;  i++) {
            result.add(new Button(buttonTexts[i] + "Node " + i));
        }
        return result;
    }


    @SuppressWarnings("serial")
    private static final List<RectParams> nodes = new ArrayList<RectParams>() { {
            add(new RectParams(50, 20, Color.ALICEBLUE));
            add(new RectParams(150, 30, Color.ANTIQUEWHITE));
            add(new RectParams(100, 40, Color.BISQUE));
            add(new RectParams(130, 30, Color.GOLDENROD));
            add(new RectParams(140, 50, Color.LIGHTSEAGREEN));
            add(new RectParams(80, 25, Color.LIGHTYELLOW));
            add(new RectParams(50, 20, Color.LAVENDER));
        }
    };

    private List<Node> createVariableNodes() {
        List<Node> result = new ArrayList<>();

        for (RectParams param : nodes) {
            Rectangle rect = new Rectangle(0, 0, param.getWidth(), param.getHeight());
            rect.setFill(param.getColor());
            rect.setStroke(Color.LIGHTGRAY);
            result.add(rect);
        }

        return result;
    }
}
