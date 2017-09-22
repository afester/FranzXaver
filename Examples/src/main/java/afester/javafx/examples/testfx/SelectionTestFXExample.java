package afester.javafx.examples.testfx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SelectionTestFXExample extends ApplicationTest {

    private TextField textField;

    @Test
    public void selectionTest() {
        assertEquals("Initial", textField.getText());

        type(KeyCode.HOME);

        press(KeyCode.SHIFT);
        type(KeyCode.RIGHT);
        assertThat(robotContext().getKeyboardRobot().getPressedKeys(), hasItem(KeyCode.SHIFT));
        type(KeyCode.RIGHT);
        type(KeyCode.RIGHT);
        type(KeyCode.RIGHT);

        assertEquals("Init", textField.getSelectedText());
        release(KeyCode.SHIFT);
    }


    @Override
    public void start(Stage primaryStage) {
        VBox mainLayout = new VBox();
        textField = new TextField();
        textField.setText("Initial");
        mainLayout.getChildren().addAll(textField);
        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
