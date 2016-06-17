package afester.javafx.examples.table.simple;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


/**
 * This is a standard TableView example based on http://docs.oracle.com/javafx/2/ui_controls/table-view.htm
 */
@Example(desc = "Standard Table example",
         cat  = "Basic JavaFX")
public class TableExample extends Application {

    private final ObservableList<TableRow> data =
            FXCollections.observableArrayList(
                    new TableRow("Jacob", "Smith", "jacob.smith@example.com"),
                    new TableRow("Isabella", "Johnson", "isabella.johnson@example.com"),
                    new TableRow("Ethan", "Williams", "ethan.williams@example.com"),
                    new TableRow("Emma", "Jones", "emma.jones@example.com"),
                    new TableRow("Michael", "Brown", "michael.brown@example.com")
            );

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Table example");

        TableView<TableRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TableRow, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<TableRow,String>("firstName")
            );

        TableColumn<TableRow, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<TableRow,String>("lastName")
            );

        TableColumn<TableRow, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(
                new PropertyValueFactory<TableRow,String>("email")
            );        

        table.getColumns().addAll(firstNameCol, lastNameCol, emailCol);

        // set data for the table
        table.setItems(data);

        // show the generated scene graph
        Scene scene = new Scene(table);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
