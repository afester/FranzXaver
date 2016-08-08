package afester.javafx.examples.table.dynamic;

import afester.javafx.components.DynamicTable;
import afester.javafx.examples.Example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

enum CellType {
    STRING, DATE, NUMBER, CURRENCY; 
}

class CellContent {
    private String formula;
    private CellType cellType;
    private String result;

    public CellContent(CellType type) {
        cellType = type;
    }
    
    public String getFormula() {
        return formula;
    }

    public void setFormula(String newFormula) {
        formula = newFormula;
    }

    public String getResult() {
        if (formula != null) {
            result = "EVAL";
        }

        return result;
    }

    public void setResult(String string) {
        this.result = string;
    }
}


/**
 * Simple dynamic table.
 * Uses the DynamicTable component from FranzXaver.
 */
@Example(desc = "Dynamic Table",
         cat  = "FranzXaver")
public class TableExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX dynamic table example");

        DynamicTable<CellContent> table = new DynamicTable<>(10, 15);
        table.setEditable(true);
        //table.setMinWidth(400);
        //table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setCellConverter(new StringConverter<CellContent>() {

            @Override
            public String toString(CellContent object) {
                System.err.println("toString: " + object);
                String result = "";
                if (object != null) {
                    result = object.getResult();
                }
                return result;
            }

            @Override
            public CellContent fromString(String string) {
                System.err.println("fromString: " + string);

                CellContent result = new CellContent(CellType.STRING);
                if (string.startsWith("=")) {
                    result.setFormula(string);
                } else {
                    result.setResult(string);                    
                }

                return result;
            }
        });

        CellContent c1 = new CellContent(CellType.STRING);
        c1.setResult("Hello World");
        table.setValue(3, 3, c1);

        CellContent c2 = new CellContent(CellType.STRING);
        c2.setFormula("=4+4");
        table.setValue(0, 0, c2);

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
        Scene scene = new Scene(mainLayout, 640, 300);
        scene.getStylesheets().add("/afester/javafx/examples/table/dynamic/dynamictable.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
