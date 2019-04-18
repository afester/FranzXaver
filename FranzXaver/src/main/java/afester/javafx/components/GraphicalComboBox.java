package afester.javafx.components;


import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class GraphicalComboBox<T> extends ComboBox<T> {

    public GraphicalComboBox(java.util.function.Function<T, Node> graphicsFactory) {
        setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
            @Override public ListCell<T> call(ListView<T> p) {
                return new ListCell<T>() {

                    // private final ArrowStraightLine content;

                    { 
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY); 
                        //content = new ArrowStraightLine(0, 0, 20, 0);
                    }

                    @Override protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            //content.setStartArrow(new ArrowStyle(ArrowShape.NONE, 10, 30));
                            //content.setEndArrow(new ArrowStyle(item, 10, 30));
                            setGraphic(graphicsFactory.apply(item));
                        }
                   }
              };
          }
       });

       setButtonCell(new ListCell<T>() {
            //private final ArrowStraightLine content;

            { 
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY); 
                // content = new ArrowStraightLine(0, 0, 20, 0);
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    //content.setStartArrow(new ArrowStyle(ArrowShape.NONE, 10, 30));
                    //content.setEndArrow(new ArrowStyle(item, 10, 30));
                    setGraphic(graphicsFactory.apply(item));
                }
            }
        });
    }
}
