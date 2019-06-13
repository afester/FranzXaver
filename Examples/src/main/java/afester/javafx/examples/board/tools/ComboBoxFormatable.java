package afester.javafx.examples.board.tools;

import java.util.function.Function;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ComboBoxFormatable<T> extends ComboBox<T> {

    private Function<T, String> mapper = item -> item.toString();

    public ComboBoxFormatable() {

        // map printer to printer name in the button cell
        setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    String text = mapper.apply(item);
                    setText(text);
                }
            }
        });

        // map printer to printer name in the dropdown cells
        setCellFactory(new Callback<ListView<T>, ListCell<T>>() {

            @Override
            public ListCell<T> call(ListView<T> p) {
                return new ListCell<T>() {

                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                        } else {
                            String text = mapper.apply(item);
                            setText(text);
                        }
                    }
                };
            }
        });
    }
    

    public void mapText(Function<T, String> mapper) {
        this.mapper = mapper;
    }
}
