package afester.javafx.examples.svg;

import java.io.InputStream;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import afester.javafx.svg.SVGLoader;
import afester.javafx.svg.test.BasicTests;

public class SVGLoaderResultViewer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("SVGLoader result viewer");

        // load the svg file
        InputStream svgFile = BasicTests.class.getResourceAsStream("ellipse.svg");
        SVGLoader loader = new SVGLoader();
        Group svgImage = loader.loadSvg(svgFile);

        // show the generated scene graph
        Scene scene = new Scene(svgImage, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
