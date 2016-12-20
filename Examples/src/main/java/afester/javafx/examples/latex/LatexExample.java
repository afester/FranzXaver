package afester.javafx.examples.latex;

import afester.javafx.examples.Example;
import afester.javafx.latex.LatexRenderer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


@Example(desc = "LaTeX rendering example",
         cat  = "Basic JavaFX")
public class LatexExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX LaTeX example");

        VBox mainLayout = new VBox();

        HBox fieldLayout = new HBox();
        TextField textField = new TextField();
        Button button = new Button("Render");

        fieldLayout.getChildren().addAll(textField, button);

        ImageView iv = new ImageView();
        LatexRenderer lr = new LatexRenderer();
        button.setOnAction(e -> {
            String latex = textField.getText();
            Image img = lr.render(latex);
            iv.setImage(img);
            primaryStage.sizeToScene();
        });

        mainLayout.getChildren().addAll(fieldLayout, iv);

        // show the generated scene graph
        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
