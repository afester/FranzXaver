package afester.javafx.examples.hexedit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import afester.javafx.hexedit.HexDump;

public class HexEditor extends Application {

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hex editor sample");

        InputStream sampleFile = new FileInputStream("data/Ghostscript_Tiger.svg");
        HexDump hd = new HexDump(sampleFile);

        // show the generated scene graph
        Scene scene = new Scene(hd, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
