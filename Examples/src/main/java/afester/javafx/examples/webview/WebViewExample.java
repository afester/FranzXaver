package afester.javafx.examples.webview;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

@Example(desc = "Standard Table example", 
         cat = "Basic JavaFX")
public class WebViewExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX WebView example");

        HBox mainLayout = new HBox();

        WebView left = new WebView();
        WebView right = new WebView();

        left.getEngine().load("http://www.google.de");
        right.getEngine().load("http://www.oracle.com");
        mainLayout.getChildren().addAll(left, right);

        // show the generated scene graph
        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
