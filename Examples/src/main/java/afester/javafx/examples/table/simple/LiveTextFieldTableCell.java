package afester.javafx.examples.table.simple;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class LiveTextFieldTableCell<S, T> extends TableCell<S,T> {

    private TextField textField;

    private ObservableValue<String> textProperty = new SimpleStringProperty();


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
        System.err.println("Create...");
        this.getStyleClass().add("direct-text-field-table-cell");
        this.textField = new TextField();
    }

/**
 * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Cell.html#updateItem-T-boolean-
 * "... there is no need to manage bindings - simply react to the change in items when this method occurs"
 */
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        System.err.printf("updateItem: %s, %s%n", item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            // That is the problem:
            // * Whenever the text in the observable changes, then the text is updated in the TextField.
            //   (either by calling setText like here, or implicitly through the bi-directional binding).
            // * However, when the text is updated, then the cursor is set to the beginning of the text field.
            // * Also, unfortunately the text is also updated when it is changed from the TextField to the observable value -
            //   this should probably not happen!

            // The following listener is called when setting the new value of the observable object from the model:
            //            private final InvalidationListener tableRowUpdateObserver = value -> {
            //                itemDirty = true;
            //                requestLayout();      // !!!!!!!!!!!!!!!!!!!!!!!!!
            //            };

    //        textField.setText(item.toString());

            setGraphic(textField);

/*
            if (textProperty instanceof StringProperty) {
                textField.textProperty().unbindBidirectional((StringProperty) textProperty);
            }
*/
            // get the observable value and bind it to the text property of the text field
            ObservableValue<?> obsValue = getTableColumn().getCellObservableValue(getIndex());
            textField.textProperty().addListener((obs, oldVal, newVal) -> {
                System.err.println("Setting new value ...");
                ((StringProperty) obsValue).set(newVal);   // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                                           // calls requestLayout(), see above
                System.err.println("   Done.");
            });

//            if (obsValue instanceof StringProperty) {
//                ((StringProperty) obsValue).bind(textField.textProperty());

/*                textProperty = (ObservableValue<String>) obsValue;

                // NOTE: bindBidirectional updates the text in the text field - 
                // however, during editing, this also means that the cursor is moved to the
                // beginning of the text field !!!!!
                // Hence, for now, we only update in one direction, from the text field 
                // to the property.
                // Interesting that a single TextField does not behave like this, if bound
                // bi directionally ...
                // unfortunately, the text field is then not updated even initially ...
                if (!textField.textProperty().isBound()) {
                    System.err.printf("Binding...%n");
                    textField.textProperty().bindBidirectional((StringProperty) textProperty);
                    
                }
*/                // ((StringProperty) textProperty).bind(textField.textProperty());
//            }
        }
    }
}
