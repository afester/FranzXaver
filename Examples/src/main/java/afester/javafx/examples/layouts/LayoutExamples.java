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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Example("Basic JavaFX layout panels")
public class LayoutExamples extends Application {

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    private List<Pane> layouts = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("JavaFX layouts samples");

        Tab hboxTab = new Tab();
        hboxTab.setText("HBox");
        hboxTab.setClosable(false);
        HBox hboxLayout = new HBox();
        hboxLayout.setBackground(createBackground(Color.LIGHTGREEN));
        hboxLayout.getChildren().addAll(createVariableNodes());
        hboxTab.setContent(hboxLayout);
        layouts.add(hboxLayout);
        
        Tab vboxTab = new Tab();
        vboxTab.setText("VBox");
        vboxTab.setClosable(false);
        VBox vboxLayout = new VBox();
        vboxLayout.setBackground(createBackground(Color.ORANGE));
        vboxLayout.getChildren().addAll(createVariableNodes());
        vboxTab.setContent(vboxLayout);
        layouts.add(vboxLayout);

        Tab flowPaneTab = new Tab();
        flowPaneTab.setText("FlowPane");
        flowPaneTab.setClosable(false);
        FlowPane flowLayout = new FlowPane();
        flowLayout.setBackground(createBackground(Color.LIGHTSKYBLUE));
        flowLayout.getChildren().addAll(createVariableNodes());
        flowPaneTab.setContent(flowLayout);
        layouts.add(flowLayout);

        Tab gridPaneTab = new Tab("GridPane");
        gridPaneTab.setClosable(false);
        GridPane gridLayout = new GridPane();
        gridLayout.setBackground(createBackground(Color.LIGHTCORAL));
        List<Node> contents1 = createVariableNodes();
        gridLayout.add(contents1.get(0), 0, 0);
        gridLayout.add(contents1.get(1), 1, 0);
        gridLayout.add(contents1.get(2), 0, 1, 2, 1);
        gridLayout.setGridLinesVisible(true);
        gridPaneTab.setContent(gridLayout);
        layouts.add(gridLayout);

        Tab borderPaneTab = new Tab();
        borderPaneTab.setText("BorderPane");
        borderPaneTab.setClosable(false);
        BorderPane borderLayout = new BorderPane();
        borderLayout.setBackground(createBackground(Color.LIGHTYELLOW));
        List<Node> contents = createVariableNodes();
        borderLayout.setLeft(contents.get(0));
        borderLayout.setTop(contents.get(1));
        borderLayout.setRight(contents.get(2));
        borderLayout.setBottom(contents.get(3));
        borderLayout.setCenter(contents.get(4));
        borderPaneTab.setContent(borderLayout);
        layouts.add(borderLayout);

        Tab stackPaneTab = new Tab();
        stackPaneTab.setText("StackPane");
        stackPaneTab.setClosable(false);
        StackPane stackLayout = new StackPane();
        stackLayout.getChildren().addAll(createVariableNodes());
        stackLayout.setBackground(createBackground(Color.YELLOW));
        stackPaneTab.setContent(stackLayout);
        layouts.add(stackLayout);

        Tab tilePaneTab = new Tab("TilePane");
        tilePaneTab.setClosable(false);
        TilePane tileLayout = new TilePane();
        tileLayout.getChildren().addAll(createVariableNodes());
        tileLayout.setBackground(createBackground(Color.LIGHTGOLDENRODYELLOW));
        tilePaneTab.setContent(tileLayout);
        layouts.add(tileLayout);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(hboxTab, vboxTab, flowPaneTab, gridPaneTab, borderPaneTab, 
                                 stackPaneTab, tilePaneTab);

        VBox optionsPanel = new VBox();
        CheckBox componentType = new CheckBox("Use Buttons instead of Rectangles");
        componentType.selectedProperty().addListener((observable, oldValue, newValue) -> {
            for (Pane pane : layouts) {
                pane.getChildren().clear();
                if (newValue) {
                    pane.getChildren().addAll(createNodes());
                } else {
                    pane.getChildren().addAll(createVariableNodes());
                }
            }
        });
        optionsPanel.getChildren().add(componentType);
        optionsPanel.setPadding(new Insets(10));

        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(tabPane);
        mainLayout.setLeft(optionsPanel);

        // show the generated scene graph
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    

    private Background createBackground(Color col) {
        return new Background(
                new BackgroundFill(col, new CornerRadii(0), new Insets(0)));
    }


    private List<Node> createNodes() {
        List<Node> result = new ArrayList<>();
        for (int i = 0;  i < 10;  i++) {
            result.add(new Button("Node " + i));
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
