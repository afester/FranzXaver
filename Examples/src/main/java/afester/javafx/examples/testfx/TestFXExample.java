package afester.javafx.examples.testfx;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//@Example(desc = "Simple TestFX example", 
//         cat = "Third Party")
public class TestFXExample extends ApplicationTest {

    private StringProperty obs = new SimpleStringProperty("Initial");

    private TextField textField;

    @Test
    public void simpleTest() {
        assertEquals("Initial", textField.getText());

        type(KeyCode.A);
        assertEquals("a", textField.getText());

        press(KeyCode.SHIFT);
        type(KeyCode.A);
        assertEquals("aA", textField.getText());
        release(KeyCode.SHIFT);
    }

    
    @Test
    public void selectionTest() {
        assertEquals("Initial", textField.getText());

        type(KeyCode.HOME);

        press(KeyCode.SHIFT);
        type(KeyCode.RIGHT);
        type(KeyCode.RIGHT);
        type(KeyCode.RIGHT);
        type(KeyCode.RIGHT);
        assertEquals("Initial", textField.getSelectedText());
    }


    @Override
    public void start(Stage primaryStage) {
        System.err.println("SETTING UP STAGE ....");
        primaryStage.setTitle("JavaFX TextField example");

        VBox mainLayout = new VBox();

        HBox fieldLayout = new HBox();

        textField = new TextField();
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
        clear.setOnAction(e -> obs.set(""));
        Button setVal = new Button("Set value");
        setVal.setOnAction(e -> obs.set("Hello"));
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
