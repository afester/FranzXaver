package afester.javafx.examples.board;

import java.net.URL;
import java.util.ResourceBundle;

import afester.javafx.examples.board.model.Net;
import afester.javafx.examples.board.model.Part;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class BomViewController implements Initializable {
    @FXML
    private ListView<Part> partsList;

    @FXML
    private ListView<Net> netsList;

    public ListView<Part> getPartsList() {
        return partsList;
    }

    public ListView<Net> getNetsList() {
        return netsList;
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        partsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        partsList.setCellFactory(listView -> {
            return new ListCell<>() {

                @Override
                protected void updateItem(Part item, boolean empty) {
                    // calling super here is very important - don't skip this!
                    super.updateItem(item, empty);

                    if (item != null) {
                        if (item.getValue().isEmpty()) {
                            setText(String.format("%s", item.getName()));
                        } else {
                            setText(String.format("%s - %s", item.getName(),
                                                             item.getValue()));
                        }
                    } else {
                        setText("");
                    }
                }
            };
            
        });

        netsList.setCellFactory(listView -> {
            return new ListCell<>() {

                @Override
                protected void updateItem(Net net, boolean empty) {
                    // calling super here is very important - don't skip this!
                    super.updateItem(net, empty);

                    if (net != null) {
                        setText(net.getName());
                    } else {
                        setText("");
                    }
                }
            };
            
        });

    }

}
