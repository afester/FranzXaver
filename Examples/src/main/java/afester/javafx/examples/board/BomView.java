package afester.javafx.examples.board;

import java.io.IOException;

import afester.javafx.examples.board.model.Board;
import afester.javafx.examples.board.model.Part;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

public class BomView extends VBox {

    public BomView(Board board) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BomView.fxml"));
        try {
            SplitPane bomView = loader.load();
            
            BomViewController controller = loader.getController();

            board.getParts().values().forEach(part -> {
                controller.getParts().add(part.getName()); // String.format("%s - %s", part.getName(), 
                                                           // part.getValue()));
            });

            board.getNets().values().forEach(part -> {
                controller.getNets().add(part.getName());
            });

            controller.getPartsList().getSelectionModel().getSelectedItems().
                    addListener((javafx.collections.ListChangeListener.Change<? extends String> change) -> {
                change.next();

                change.getAddedSubList().forEach(partName -> {
                    Part part = board.getPart(partName);
                    part.setSelected(true);
                });

                change.getRemoved().forEach(partName -> {
                    Part part = board.getPart(partName);
                    part.setSelected(false);
                });
                
            });

            getChildren().add(bomView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
