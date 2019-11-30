package afester.javafx.examples.board;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

public class BomView extends VBox {

    public BomView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BomView.fxml"));
        try {
            SplitPane bomView = loader.load();
            getChildren().add(bomView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
