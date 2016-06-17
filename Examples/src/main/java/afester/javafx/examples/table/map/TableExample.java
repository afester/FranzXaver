package afester.javafx.examples.table.map;

import java.util.HashMap;
import java.util.Map;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * This is a standard TableView example based on http://docs.oracle.com/javafx/2/ui_controls/table-view.htm
 */
@Example(desc = "Standard Table example",
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

        TableView<Map<Integer, String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // http://stackoverflow.com/questions/12324464/how-to-javafx-hide-background-header-of-a-tableview
        table.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                // Get the table header
                Pane header = (Pane)table.lookup("TableHeaderRow");
                if(header != null && header.isVisible()) {
                  header.setMaxHeight(0);
                  header.setMinHeight(0);
                  header.setPrefHeight(0);
                  header.setVisible(false);
                  header.setManaged(false);
                }
            }
        });

        TableColumn<Map<Integer, String>, String> col0 = new TableColumn<>("Col 0");
        col0.setCellValueFactory(new MapValueFactory(0));
        TableColumn<Map<Integer, String>, String> col1 = new TableColumn<>("Col 1");
        col1.setCellValueFactory(new MapValueFactory(1));
        TableColumn<Map<Integer, String>, String> col2 = new TableColumn<>("Col 2");
        col2.setCellValueFactory(new MapValueFactory(2));

        table.getColumns().addAll(col0, col1, col2);

        // set data for the table
        table.setItems(generateDataInMap());

        // show the generated scene graph
        Scene scene = new Scene(table);
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
