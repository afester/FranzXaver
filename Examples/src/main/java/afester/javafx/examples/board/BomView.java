package afester.javafx.examples.board;

import java.io.IOException;

import afester.javafx.examples.board.model.Board;
import afester.javafx.examples.board.model.Part;
import afester.javafx.examples.board.model.Net;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

public class BomView extends VBox {

    public BomView(final Board board) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BomView.fxml"));
        try {
            final SplitPane bomView = loader.load();
            final BomViewController controller = loader.getController();

            // simple sorting of the Parts by name
            final var partList = FXCollections.observableArrayList(board.getParts().values());
            final var sortedParts = new SortedList<Part>(partList, 
                                (a, b) -> a.getName().compareTo(b.getName()));
            controller.getPartsList().setItems(sortedParts);

            // simple sorting of the Nets by name
            final var netList = FXCollections.observableArrayList(board.getNets().values());
            final var sortedNets = new SortedList<Net>(netList, 
                                (a, b) -> a.getName().compareTo(b.getName()));
            controller.getNetsList().setItems(sortedNets);

            // monitor the selections in the list views
            controller.getPartsList().getSelectionModel().getSelectedItems().
                    addListener((javafx.collections.ListChangeListener.Change<? extends Part> change) -> {
                        change.next();
                        change.getAddedSubList().forEach(part -> part.setSelected(true));
                        change.getRemoved().forEach(part -> part.setSelected(false));
                    });
            controller.getNetsList().getSelectionModel().getSelectedItems().
                    addListener((javafx.collections.ListChangeListener.Change<? extends Net> change) -> {
                        change.next();
                        change.getAddedSubList().forEach(net -> net.setSelected(true));
                        change.getRemoved().forEach(net -> net.setSelected(false));
                    });

            getChildren().add(bomView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
