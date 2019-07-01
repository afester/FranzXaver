package afester.javafx.examples.board;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class AboutDialog extends Pane {

    public AboutDialog() {
        GridPane gp = new GridPane();
        
        Label l11 = new Label("Application version:");
        GridPane.setConstraints(l11, 0, 0);
        Label l12 = new Label("Development:");
        GridPane.setConstraints(l12, 1, 0);

        Label l21 = new Label("Java version:");
        GridPane.setConstraints(l21, 0, 1);
        Label l22 = new Label(System.getProperty("java.version"));
        GridPane.setConstraints(l22, 1, 1);

        gp.getChildren().addAll(l11, l12, l21, l22);

        getChildren().add(gp);
    }
}
