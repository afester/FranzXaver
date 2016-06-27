package afester.javafx.examples.table.simple;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class LiveTextFieldTableCell<S, T> extends TableCell<S,T> {

    private TextField textField;

    private ObservableValue<String> textProperty;


    /**
     * Creates a new table cell for direct text editing.
     *
     * @param inlineEditCol
     * @return
     */
    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
            TableColumn<TableRow, String> inlineEditCol) {
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

            if (textProperty instanceof StringProperty) {
                textField.textProperty().unbindBidirectional((StringProperty)textProperty);
            }

            // get the observable value and bind it to the text property of the text field
            ObservableValue<?> obsValue = getTableColumn().getCellObservableValue(getIndex());
            if (obsValue instanceof StringProperty) {
                textProperty = (ObservableValue<String>) obsValue;

                // NOTE: bindBidirectional updates the text in the text field - 
                // however, during editing, this also means that the cursor is moved to the
                // beginning of the text field !!!!!
                // Hence, for now, we only update in one direction, from the text field 
                // to the property.
                // Interesting that a single TextField does not behave like this, if bound
                // bi directionally ...
                // unfortunately, the text field is then not updated even initially ... 
                textField.textProperty().bindBidirectional((StringProperty) textProperty);
                // ((StringProperty) textProperty).bind(textField.textProperty());
            }
        }
    }
}
