package afester.javafx.examples.svg;


import java.io.InputStream;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import afester.javafx.svg.SVGLoader;

public class TigerExample extends Application {
	
	public static void main(String[] args) {
        launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("SVG Display sample");

        // load the sample svg file
        InputStream svgFile = getClass().getResourceAsStream("Ghostscript_Tiger.svg");
        SVGLoader loader = new SVGLoader();
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
