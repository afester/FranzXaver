package afester.javafx.examples.layouts;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LayoutExamples extends Application {

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("JavaFX layouts samples");

        Tab hboxTab = new Tab();
        hboxTab.setText("HBox");
        hboxTab.setClosable(false);
        HBox hboxLayout = new HBox();
        hboxLayout.setBackground(createBackground(Color.LIGHTGREEN));
        hboxLayout.getChildren().addAll(createNodes());
        hboxTab.setContent(hboxLayout);
        
        Tab vboxTab = new Tab();
        vboxTab.setText("VBox");
        vboxTab.setClosable(false);
        VBox vboxLayout = new VBox();
        vboxLayout.setBackground(createBackground(Color.ORANGE));
        vboxLayout.getChildren().addAll(createNodes());
        vboxTab.setContent(vboxLayout);

        Tab flowPaneTab = new Tab();
        flowPaneTab.setText("FlowPane");
        flowPaneTab.setClosable(false);
        FlowPane flowLayout = new FlowPane();
        flowLayout.setBackground(createBackground(Color.LIGHTSKYBLUE));
        flowLayout.getChildren().addAll(createNodes());
        flowPaneTab.setContent(flowLayout);

        Tab gridPaneTab = new Tab("GridPane");
        gridPaneTab.setClosable(false);
        GridPane gridLayout = new GridPane();
        gridLayout.setBackground(createBackground(Color.LIGHTCORAL));
        List<Node> contents1 = createNodes();
        gridLayout.add(contents1.get(0), 0, 0);
        gridLayout.add(contents1.get(1), 1, 0);
        gridLayout.add(new Button("Advanced test"), 0, 1, 2, 1);
        gridLayout.setGridLinesVisible(true);
        gridPaneTab.setContent(gridLayout);

        Tab borderPaneTab = new Tab();
        borderPaneTab.setText("BorderPane");
        borderPaneTab.setClosable(false);
        BorderPane borderLayout = new BorderPane();
        borderLayout.setBackground(createBackground(Color.LIGHTYELLOW));
        List<Node> contents = createNodes();
        borderLayout.setLeft(contents.get(0));
        borderLayout.setTop(contents.get(1));
        borderLayout.setRight(contents.get(2));
        borderLayout.setBottom(contents.get(3));
        borderLayout.setCenter(contents.get(4));
        borderPaneTab.setContent(borderLayout);
        
        Tab stackPaneTab = new Tab();
        stackPaneTab.setText("StackPane");
        stackPaneTab.setClosable(false);
        StackPane stackLayout = new StackPane();
        stackLayout.getChildren().addAll(createNodes());
        stackLayout.setBackground(createBackground(Color.YELLOW));
        stackPaneTab.setContent(stackLayout);

        Tab tilePaneTab = new Tab("TilePane");
        tilePaneTab.setClosable(false);
        TilePane tileLayout = new TilePane();
        tileLayout.getChildren().addAll(createNodes());
        tileLayout.setBackground(createBackground(Color.LIGHTGOLDENRODYELLOW));
        tilePaneTab.setContent(tileLayout);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(hboxTab, vboxTab, flowPaneTab, gridPaneTab, borderPaneTab, 
                                 stackPaneTab, tilePaneTab);

        // show the generated scene graph
        Scene scene = new Scene(tabPane, 800, 600);
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
}
