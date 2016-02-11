package afester.javafx.examples.svg;

import afester.javafx.svg.SvgLoader;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TigerExample extends Application {

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("SVG Display sample");

        // load the sample svg file
        InputStream svgFile = new FileInputStream("data/Ghostscript_Tiger.svg");
        SvgLoader loader = new SvgLoader();
        Group svgImage = loader.loadSvg(svgFile);

        // show the generated scene graph
        svgImage.setTranslateX(-200);
        svgImage.setTranslateY(-200);
        svgImage.setScaleX(0.5);
        svgImage.setScaleY(0.5);
        Scene scene = new Scene(svgImage, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
