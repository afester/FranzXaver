package afester.javafx.examples.list;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Data container
 */
class Item {
    private String value;
    private boolean isActive;
    
    public Item(String value, boolean isActive) {
        this.value = value;
        this.isActive = isActive;
    }
    
    public String getValue() {
        return value;
    }
    
    public boolean isActive() {
        return isActive;
    }
}



@Example(desc = "Colorized ListView example",
         cat  = "Basic JavaFX")
public class ListExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX ListView example");

        // Set up the list view and add items
        ListView<Item> listView = new ListView<>();
        listView.getItems().add(new Item("Active 1", true));
        listView.getItems().add(new Item("Inactive", false));
        listView.getItems().add(new Item("Active 2", true));

        // set up a custom cell factory which creates ColorizedCell objects
        listView.setCellFactory(new Callback<ListView<Item>, ListCell<Item>>() {

                    @Override 
                    public ListCell<Item> call(ListView<Item> list) {
                        return new ColorizedCell();
                    }
                }
            );

        // show the generated scene graph
        Scene scene = new Scene(listView);
        scene.getStylesheets().add("/afester/javafx/examples/list/listview.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    /**
     * A colorized cell which can be styled through CSS. 
     */
    static class ColorizedCell extends ListCell<Item> {

        @Override
        public void updateItem(Item item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                setText(item.getValue());
                if (item.isActive()) {
                   getStyleClass().add("active");
                } else {
                   getStyleClass().add("inactive");
                }
            }
        }
    }

}
