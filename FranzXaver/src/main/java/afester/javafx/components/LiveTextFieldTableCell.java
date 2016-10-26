package afester.javafx.components;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class LiveTextFieldTableCell<S, T> extends TableCell<S,T> {

    private TextField textField;

    /**
     * @return A new table cell for inline text editing.
     */
    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn() {
        return list -> new LiveTextFieldTableCell<S,T>();
    }


    private LiveTextFieldTableCell() {
        this.getStyleClass().add("live-text-field-table-cell");

        this.textField = new TextField();

        // Add a listener on the text field to update the observable value
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            ObservableValue<?> obsValue = getTableColumn().getCellObservableValue(getIndex());
            ((StringProperty) obsValue).set(newVal);
        });

    }

/**
 * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Cell.html#updateItem-T-boolean-
 * "... there is no need to manage bindings - simply react to the change in items when this method occurs"
 */
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            // That is the problem:
            // ====================
            // * Whenever the text in the observable changes, then the text is updated in the TextField.
            //   (either by calling setText like here, or implicitly through the bi-directional binding).
            //
            // * However, when the text is updated, then the cursor is set to the beginning of the text field.
            //
            // * Also, unfortunately the text is also updated when it is changed from the TextField to 
            //   the observable value - this should probably not happen!
            // 
            //   The following listener (implemented in TableCell) is called when setting the new value 
            //   of the observable object from the model:
            //
            //            private final InvalidationListener tableRowUpdateObserver = value -> {
            //                itemDirty = true;
            //                requestLayout();      // !! This finally calls updateItem()
            //            };

            // Workaround:
            // ============
            // Instead of using a bi-directional binding (which would synchronize the
            // text field text automatically with the property), we 
            // * explicitly set the text of the text field here, and at the same time we remember the 
            //   caret position so that it can be restored afterwards
            // * explicitly synchronize the property in the data model through a listener
            //   on the text field's text property
            
            int old = textField.getCaretPosition();
            textField.setText(item.toString());
            textField.positionCaret(old);

            setGraphic(textField);
        }
    }
}
