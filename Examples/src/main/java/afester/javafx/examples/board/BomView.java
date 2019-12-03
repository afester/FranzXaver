package afester.javafx.examples.board;

import java.io.IOException;

import afester.javafx.examples.board.model.Part;
import afester.javafx.examples.board.view.BoardView;
import afester.javafx.examples.board.view.Interactable;
import afester.javafx.examples.board.view.PartView;
import afester.javafx.examples.board.model.Net;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

public class BomView extends VBox {

    private BomViewController controller = null;

    public BomView(final BoardView boardView) {
        final var board = boardView.getBoard();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BomView.fxml"));
        try {
            final SplitPane bomView = loader.load();
            controller = loader.getController();

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
                        change.getAddedSubList().forEach(part -> {
                            part.setSelected(true);
                            // boardView.selectedObjectsProperty().add(part);
                        });
                        change.getRemoved().forEach(part -> part.setSelected(false));
                    });

            // update selection from selection changes in the graph view
            boardView.selectedObjectsProperty().addListener((javafx.collections.ListChangeListener.Change<? extends Interactable> change) -> {
                change.next();
                controller.getPartsList().getSelectionModel().clearSelection();
                change.getList().forEach(e ->
                    controller.getPartsList().getSelectionModel().select(((PartView) e).getPart()));
            });
            controller.getPartsList().setContextMenu(createContextMenu());
            
            
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


    private ContextMenu createContextMenu() {
        final var result = new ContextMenu();
        
        final var hideMenuItem = new MenuItem("Hide");
        hideMenuItem.setOnAction(a -> hideSelectedParts());

        final var showMenuItem = new MenuItem("Show");
        showMenuItem.setOnAction(a -> showSelectedParts());

        result.getItems().addAll(hideMenuItem, showMenuItem);
        return result;
    }


    private void hideSelectedParts() {
        controller.getPartsList().getSelectionModel().getSelectedItems().forEach(part -> {
            part.setHidden(true);
        });
    }

    private void showSelectedParts() {
        controller.getPartsList().getSelectionModel().getSelectedItems().forEach(part -> {
            part.setHidden(false);
        });
    }
}
