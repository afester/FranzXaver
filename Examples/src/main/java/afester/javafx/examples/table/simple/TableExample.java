package afester.javafx.examples.table.simple;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;


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
                    new TableRow("Emma", "Jones", "emma.jones@example.com", true, "Hello World"),
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
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TableRow, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<TableRow,String>("firstName")
            );
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setOnEditCommit(
            new EventHandler<CellEditEvent<TableRow, String>>() {
                @Override
                public void handle(CellEditEvent<TableRow, String> t) {
                    String newValue = t.getNewValue();
                    int rowIdx = t.getTablePosition().getRow();
                    TableRow row = t.getTableView().getItems().get(rowIdx);

                    row.setFirstName(newValue);
                }
            }
        );

        TableColumn<TableRow, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<TableRow, String>("lastName")
            );

        TableColumn<TableRow, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(
                new PropertyValueFactory<TableRow, String>("email")
            );        

        TableColumn<TableRow, Boolean> flagCol = new TableColumn<>("Flag");
        flagCol.setCellFactory(CheckBoxTableCell.forTableColumn((TableColumn<TableRow, Boolean>)null));
        flagCol.setCellValueFactory(
                new PropertyValueFactory<TableRow, Boolean>("flag")
            );

        TableColumn<TableRow, String> inlineEditCol = new TableColumn<>("Edit");
        inlineEditCol.setCellFactory(LiveTextFieldTableCell.forTableColumn(inlineEditCol));

        // sets the obervable factory for the cell value.
        // In the cell renderer, the corresponding observable can be retrieved through 
        // getTableColumn().getCellObservableValue(getIndex());
        inlineEditCol.setCellValueFactory(
                new PropertyValueFactory<TableRow, String>("comment")
            );

        table.getColumns().addAll(firstNameCol, lastNameCol, emailCol, flagCol, inlineEditCol);
        
        // set data for the table
        table.setItems(data);

        VBox layout = new VBox();
        layout.getChildren().add(table);
        Button dumpIt = new Button("Dump");
        dumpIt.setOnAction(e -> {
            for (TableRow row : data) {
                System.err.println(row.toString());
            }
        });
        layout.getChildren().add(dumpIt);

        // show the generated scene graph
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
