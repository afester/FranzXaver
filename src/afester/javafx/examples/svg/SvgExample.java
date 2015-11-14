package afester.javafx.examples.svg;


import afester.javafx.svg.SVGLoader;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SvgExample extends Application {
	
	public static void main(String[] args) {
        launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("SVG Display sample");

        SVGLoader loader = new SVGLoader();
		Group svgImage = loader.loadSvg("Ghostscript_Tiger.svg");

        Scene scene = new Scene(svgImage, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
	}
}
