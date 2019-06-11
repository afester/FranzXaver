package afester.javafx.examples.board;

import javafx.event.ActionEvent;
import javafx.scene.Group;

public class PrintControlPanel extends Group {
  
    public PrintControlPanel() {
        System.err.println("YES!");
    }

    public void handlePrintAction(ActionEvent event) {
        System.out.println("Printing ...");
    }
}
