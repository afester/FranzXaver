package afester.javafx.examples.board;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class BomViewController implements Initializable {
    @FXML
    private ListView<String> partsList;

    @FXML
    private ListView<String> netsList;

    public ObservableList<String> getParts() {
        return partsList.getItems();
    }

    public ObservableList<String> getNets() {
        return netsList.getItems();
    }

    public ListView<String> getPartsList() {
        return partsList;
    }

    public ListView<String> getNetsList() {
        return netsList;
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        partsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        netsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

}
