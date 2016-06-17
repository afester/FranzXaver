package afester.javafx.examples.table.array;

import java.util.HashMap;
import java.util.Map;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Simple dynamic table
 */
@Example(desc = "Array Table example",
         cat  = "Basic JavaFX")
public class TableExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Table example");

        DynamicTable<String> table = new DynamicTable<>(5, 5);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setValue(3, 3, "Hello World");
        table.setValue(0, 0, "Sample");

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

        CheckBox toggleHeader = new CheckBox("Show column header");
        toggleHeader.selectedProperty().set(true);
        toggleHeader.setOnAction(e -> {
            table.setShowColumnHeader(toggleHeader.isSelected());
        } );
        buttons.getChildren().add(toggleHeader);

        CheckBox toggleRowHeader = new CheckBox("Show row header");
        toggleRowHeader.selectedProperty().set(true);
        toggleRowHeader.setOnAction(e -> {
            table.setShowRowHeader(toggleRowHeader.isSelected());
        } );
        buttons.getChildren().add(toggleRowHeader);

        CheckBox toggleCellSelection = new CheckBox("Cell selection");
        toggleCellSelection.selectedProperty().set(false);
        toggleCellSelection.setOnAction(e -> {
            table.getSelectionModel().setCellSelectionEnabled(toggleCellSelection.isSelected());
        } );
        buttons.getChildren().add(toggleCellSelection);

        table.getSelectionModel().setCellSelectionEnabled(true);

//        CheckBox toggleRows = new CheckBox("Show non existing rows");
//        toggleRows.selectedProperty().set(true);
//        toggleRows.setOnAction(e -> {
//            
//        } );
//        buttons.getChildren().add(toggleRows);

        BorderPane mainLayout = new BorderPane();

        mainLayout.setCenter(table);
        mainLayout.setRight(buttons);
        BorderPane.setMargin(buttons, new Insets(10));
        BorderPane.setAlignment(table, Pos.TOP_LEFT);

        // show the generated scene graph
        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(
                "/afester/javafx/examples/table/array/dynamictable.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private ObservableList<Map<Integer, String>> generateDataInMap() {
        int max = 10;
        ObservableList<Map<Integer, String>> allData = FXCollections.observableArrayList();
        for (int i = 1; i < max; i++) {
            Map<Integer, String> dataRow = new HashMap<>();
 
            String value1 = "0, " + i;
            String value2 = "1, " + i;
            String value3 = "2, " + i;
            
            dataRow.put(0, value1);
            dataRow.put(1, value2);
            dataRow.put(2, value3);
            
            allData.add(dataRow);
        }
        return allData;
    }
}
