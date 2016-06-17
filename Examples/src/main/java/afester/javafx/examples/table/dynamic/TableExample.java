package afester.javafx.examples.table.dynamic;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;

@Example(desc = "JavaFX Table example",
         cat  = "Basic JavaFX")
public class TableExample extends Application {

    private final ObservableList<TableRow> data =
            FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Table example");

        final int rowCount = 6;
        final int columnCount = 4;

        TableView<TableRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // create rows
        for (int i = 0;  i < rowCount;  i++) {
            data.add(new TableRow(i));
        }

        // create columns
        for (int i = 0;  i < columnCount;  i++) { // TableColumn col : columns) {

            TableColumn<TableRow, String> col = new TableColumn<>("Col " + i);
            col.setId("" + i);

            col.setCellValueFactory(
                new Callback<CellDataFeatures<TableRow, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(CellDataFeatures<TableRow, String> param) {
                    int column = Integer.parseInt(param.getTableColumn().getId());
                    String result = param.getValue().getValue(column);
                    return new ReadOnlyStringWrapper(result);
                }

            });

            table.getColumns().add(col);
        }

        data.get(2).setValue(3, "Hello World");

        // set data for the table
        table.setItems(data);

        // show the generated scene graph
        Scene scene = new Scene(table);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
