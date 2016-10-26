package afester.javafx.examples.fxml;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class FxmlViewer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
       URL location = getClass().getResource("FxmlSample.fxml");
       BorderPane root = FXMLLoader.load(location);
       stage.setScene(new Scene(root));
       stage.show();
    }
}
