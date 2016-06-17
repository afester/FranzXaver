package afester.javafx.examples.textfield;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


@Example(desc = "Standard Table example",
         cat  = "Basic JavaFX")
public class TextFieldExample extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX TextField example");

        HBox mainLayout = new HBox();

        TextField textField = new TextField();
        Button button = new Button("X");
        button.setDisable(true);
        textField.textProperty().addListener((obj, oldVal, newVal) -> { 
            if (newVal.trim().isEmpty()) {
                button.setDisable(true);
           } else {
                button.setDisable(false); 
            }
        });
        mainLayout.getChildren().addAll(textField, button);

        // show the generated scene graph
        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
