package afester.javafx.examples.table.dynamic;

import afester.javafx.components.DynamicTable;
import afester.javafx.components.SimpleDynamicTable;
import afester.javafx.examples.Example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * Simple dynamic table.
 * Uses the DynamicTable component from FranzXaver.
 */
@Example(desc = "Simple Dynamic Table",
         cat  = "FranzXaver")
public class SimpleTableExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX dynamic table example");

        SimpleDynamicTable table = new SimpleDynamicTable(5, 5);
        table.setEditable(true);
        table.setValue(3, 3, "Hello World");
        table.setValue(0, 0, "Value = 8");


/*********************/
        VBox buttons = new VBox();
        buttons.setSpacing(10);
        buttons.getChildren().add(new Label("Columns:"));
        Spinner<Integer> colSpinner = new Spinner<>(3, 26, 5);
        colSpinner.valueProperty().addListener((obj, oldValue, newValue) -> {
           table.setColumnCount(newValue);
        });
        buttons.getChildren().add(colSpinner);
        buttons.getChildren().add(new Label("Rows:"));
        Spinner<Integer> rowSpinner = new Spinner<>(3, 100, 5);
        rowSpinner.valueProperty().addListener((obj, oldValue, newValue) -> {
            table.setRowCount(newValue);
         });
        buttons.getChildren().add(rowSpinner);

        table.getSelectionModel().setCellSelectionEnabled(true);
/*********************/

        BorderPane mainLayout = new BorderPane();

        Group vb = new Group();
        vb.getChildren().add(table);
        
        mainLayout.setCenter(vb);

        mainLayout.setRight(buttons);
        BorderPane.setMargin(buttons, new Insets(10));
        BorderPane.setAlignment(table, Pos.TOP_LEFT);

        // show the generated scene graph
        Scene scene = new Scene(mainLayout, 640, 300);
        scene.getStylesheets().add("/afester/javafx/examples/table/dynamic/simpledynamictable.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
