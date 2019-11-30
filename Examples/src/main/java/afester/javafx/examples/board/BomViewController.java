package afester.javafx.examples.board;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

public class BomViewController implements Initializable {
    @FXML
    private ListView<String> partsList;

    @FXML
    private ListView<String> netsList;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        System.err.println("Initializing BOM view ...");

        for (int i = 0;  i < 30;  i++) {
            netsList.getItems().add(String.format("Net %s", i));
        }
        for (int i = 0;  i < 30;  i++) {
            partsList.getItems().add(String.format("Part %s", i));
        }
    }

}
