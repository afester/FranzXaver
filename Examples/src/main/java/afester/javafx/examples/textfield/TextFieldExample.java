package afester.javafx.examples.textfield;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


@Example(desc = "TextField example",
         cat  = "Basic JavaFX")
public class TextFieldExample extends Application {

    private StringProperty obs = new SimpleStringProperty("Initial");

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX TextField example");

        VBox mainLayout = new VBox();

        HBox fieldLayout = new HBox();

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

        textField.textProperty().bindBidirectional(obs);

        fieldLayout.getChildren().addAll(textField, button);

        HBox buttons = new HBox();
        Button clear = new Button("Clear");
        clear.setOnAction(e -> obs.set("") );
        Button setVal = new Button("Set value");
        setVal.setOnAction(e -> obs.set("Hello") );
        Button dump = new Button("Dump");
        dump.setOnAction(e -> System.err.println("Current: " + obs.get()));
        buttons.getChildren().addAll(clear, setVal, dump);

        mainLayout.getChildren().addAll(fieldLayout, buttons);
        
        // show the generated scene graph
        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
