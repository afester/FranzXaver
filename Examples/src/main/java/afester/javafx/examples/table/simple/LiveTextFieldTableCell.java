package afester.javafx.examples.table.simple;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class LiveTextFieldTableCell<S, T> extends TableCell<S,T> {

    private TextField textField;


    /**
     * Creates a new table cell for direct text editing.
     *
     * @param inlineEditCol
     * @return
     */
    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
            TableColumn<TableRow, Boolean> inlineEditCol) {
        return list -> new LiveTextFieldTableCell<S,T>(); // getSelectedProperty, converter);
    }


    private LiveTextFieldTableCell() {
        this.getStyleClass().add("direct-text-field-table-cell");
        this.textField = new TextField();
    }


    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        System.err.printf("updateItem: %s, %s%n", item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setGraphic(textField);
/*
            // bind the text field's text property to the cell's text property
            if (booleanProperty instanceof BooleanProperty) {
                checkBox.selectedProperty().unbindBidirectional((BooleanProperty)booleanProperty);
            }

            ObservableValue<?> obsValue = getSelectedProperty();
            if (obsValue instanceof BooleanProperty) {
                booleanProperty = (ObservableValue<Boolean>) obsValue;

                // checkBox.selectedProperty().bindBidirectional((BooleanProperty)booleanProperty);
                textField.textProperty().bindBidirectional(other);
            }
         */   
        }
    }
}
